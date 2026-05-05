"use client";

import { useMemo } from "react";
import ptBR from "@/messages/pt-BR.json";
import enUS from "@/messages/en-US.json";

type Dict = Record<string, unknown>;
type Locale = "pt-BR" | "en-US";

const dictionaries: Record<Locale, Dict> = {
  "pt-BR": ptBR as Dict,
  "en-US": enUS as Dict
};

function getPath(obj: unknown, path: string): string {
  return path
    .split(".")
    .reduce<unknown>((acc, key) => (acc && typeof acc === "object" ? (acc as Record<string, unknown>)[key] : undefined), obj)
    ?.toString() ?? path;
}

function detectLocale(): Locale {
  if (typeof window === "undefined") {
    return "pt-BR";
  }
  const lang = navigator.language;
  if (lang.toLowerCase().startsWith("en")) {
    return "en-US";
  }
  return "pt-BR";
}

export function useI18n() {
  const locale = detectLocale();
  const dict = useMemo(() => dictionaries[locale], [locale]);

  return {
    locale,
    t: (key: string) => getPath(dict, key)
  };
}
