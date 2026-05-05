import fs from "node:fs";
import os from "node:os";
import path from "node:path";
import { afterEach, describe, expect, it } from "vitest";
import { stageBundledPluginRuntime } from "../../scripts/stage-bundled-plugin-runtime.mjs";

describe("stageBundledPluginRuntime", () => {
  const originalSymlinkSync = fs.symlinkSync;
  const tempRoots: string[] = [];

  afterEach(() => {
    fs.symlinkSync = originalSymlinkSync;
    for (const tempRoot of tempRoots.splice(0)) {
      fs.rmSync(tempRoot, { recursive: true, force: true });
    }
  });

  it("copies regular files when symlink creation is not permitted", () => {
    const repoRoot = fs.mkdtempSync(path.join(os.tmpdir(), "openclaw-runtime-overlay-"));
    tempRoots.push(repoRoot);

    const pluginDir = path.join(repoRoot, "dist", "extensions", "fixture-plugin");
    fs.mkdirSync(pluginDir, { recursive: true });
    fs.writeFileSync(path.join(pluginDir, "SKILL.md"), "# fixture\n", "utf8");
    fs.writeFileSync(path.join(pluginDir, "index.js"), "export const value = 1;\n", "utf8");

    fs.symlinkSync = ((target, targetPath, type) => {
      if (String(targetPath).endsWith(`${path.sep}SKILL.md`)) {
        const error = new Error("blocked") as NodeJS.ErrnoException;
        error.code = "EPERM";
        throw error;
      }
      return originalSymlinkSync(target, targetPath, type);
    }) as typeof fs.symlinkSync;

    stageBundledPluginRuntime({ cwd: repoRoot });

    const runtimePluginDir = path.join(repoRoot, "dist-runtime", "extensions", "fixture-plugin");
    const runtimeSkillPath = path.join(runtimePluginDir, "SKILL.md");
    const runtimeJsPath = path.join(runtimePluginDir, "index.js");

    expect(fs.existsSync(runtimeSkillPath)).toBe(true);
    expect(fs.readFileSync(runtimeSkillPath, "utf8")).toBe("# fixture\n");
    expect(fs.lstatSync(runtimeSkillPath).isSymbolicLink()).toBe(false);
    expect(fs.readFileSync(runtimeJsPath, "utf8")).toContain("export default module.default;");
  });
});
