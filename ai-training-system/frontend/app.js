const els = {
  apiBase: document.getElementById("apiBase"),
  apiToken: document.getElementById("apiToken"),
  configPath: document.getElementById("configPath"),
  uploadConfig: document.getElementById("uploadConfig"),
  trainCsvFile: document.getElementById("trainCsvFile"),
  modelPath: document.getElementById("modelPath"),
  recordsJson: document.getElementById("recordsJson"),
  predictCsvFile: document.getElementById("predictCsvFile"),
  expectedColumns: document.getElementById("expectedColumns"),
  resultBox: document.getElementById("resultBox"),
  healthBtn: document.getElementById("healthBtn"),
  saveConfigBtn: document.getElementById("saveConfigBtn"),
  trainBtn: document.getElementById("trainBtn"),
  trainUploadBtn: document.getElementById("trainUploadBtn"),
  predictRecordsBtn: document.getElementById("predictRecordsBtn"),
  predictUploadBtn: document.getElementById("predictUploadBtn"),
};

const STORAGE_KEY = "ai-training-console-config";

function defaultUploadConfig() {
  return {
    task: "classification",
    data: {
      target_column: "churned",
      expected_feature_columns: ["age", "monthly_spend", "contract_months", "support_tickets", "payment_method"],
    },
    model: {
      name: "random_forest",
      params: { n_estimators: 100, max_depth: 6 },
    },
    training: { use_cv: false },
    output: {
      model_path: "artifacts/model_upload.joblib",
      metrics_path: "artifacts/metrics_upload.json",
      cv_results_path: "artifacts/cv_upload.json",
      uploaded_train_path: "artifacts/uploaded_train.csv",
    },
  };
}

function defaultRecords() {
  return [
    {
      age: 35,
      monthly_spend: 130,
      contract_months: 8,
      support_tickets: 2,
      payment_method: "pix",
    },
  ];
}

function setResult(data, isError = false) {
  els.resultBox.style.borderColor = isError ? "rgba(251,113,133,0.6)" : "rgba(45,212,191,0.45)";
  els.resultBox.textContent = typeof data === "string" ? data : JSON.stringify(data, null, 2);
}

function getSavedConfig() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    return raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
}

function saveConfig() {
  const payload = {
    apiBase: els.apiBase.value.trim(),
    apiToken: els.apiToken.value.trim(),
    modelPath: els.modelPath.value.trim(),
    configPath: els.configPath.value.trim(),
  };
  localStorage.setItem(STORAGE_KEY, JSON.stringify(payload));
  setResult({ ok: true, message: "Configuração salva no navegador." });
}

function baseUrl(path) {
  const root = els.apiBase.value.trim().replace(/\/$/, "");
  return `${root}${path}`;
}

async function apiCall(path, options = {}) {
  const token = els.apiToken.value.trim();
  const headers = options.headers || {};

  if (!(options.body instanceof FormData)) {
    headers["Content-Type"] = headers["Content-Type"] || "application/json";
  }

  headers.Authorization = `Bearer ${token}`;

  const res = await fetch(baseUrl(path), { ...options, headers });
  const text = await res.text();
  let body;
  try {
    body = text ? JSON.parse(text) : {};
  } catch {
    body = { raw: text };
  }

  if (!res.ok) {
    throw new Error(JSON.stringify({ status: res.status, body }, null, 2));
  }
  return body;
}

async function checkHealth() {
  const res = await fetch(baseUrl("/health"));
  const body = await res.json();
  setResult({ status: res.status, body });
}

async function trainByConfigPath() {
  const payload = { config_path: els.configPath.value.trim() };
  const body = await apiCall("/train", { method: "POST", body: JSON.stringify(payload) });
  setResult(body);
}

async function trainByUpload() {
  const file = els.trainCsvFile.files?.[0];
  if (!file) {
    throw new Error("Selecione um CSV de treino.");
  }

  const config = JSON.parse(els.uploadConfig.value.trim());
  const form = new FormData();
  form.append("config_json", JSON.stringify(config));
  form.append("file", file);

  const body = await apiCall("/train-upload", { method: "POST", body: form, headers: {} });
  setResult(body);
}

async function predictRecords() {
  const modelPath = els.modelPath.value.trim();
  const records = JSON.parse(els.recordsJson.value.trim());
  const body = await apiCall("/predict-records", {
    method: "POST",
    body: JSON.stringify({ model_path: modelPath, records }),
  });
  setResult(body);
}

async function predictByUpload() {
  const file = els.predictCsvFile.files?.[0];
  if (!file) {
    throw new Error("Selecione um CSV para predição.");
  }

  const modelPath = els.modelPath.value.trim();
  const form = new FormData();
  form.append("model_path", modelPath);
  form.append("file", file);

  const expected = els.expectedColumns.value.trim();
  if (expected) {
    JSON.parse(expected);
    form.append("expected_columns_json", expected);
  }

  const body = await apiCall("/predict-upload", { method: "POST", body: form, headers: {} });
  setResult(body);
}

function bind(button, fn) {
  button.addEventListener("click", async () => {
    try {
      button.disabled = true;
      setResult("Executando...");
      await fn();
    } catch (err) {
      setResult(String(err.message || err), true);
    } finally {
      button.disabled = false;
    }
  });
}

function init() {
  const saved = getSavedConfig();
  if (saved) {
    els.apiBase.value = saved.apiBase || els.apiBase.value;
    els.apiToken.value = saved.apiToken || els.apiToken.value;
    els.modelPath.value = saved.modelPath || els.modelPath.value;
    els.configPath.value = saved.configPath || els.configPath.value;
  }

  els.uploadConfig.value = JSON.stringify(defaultUploadConfig(), null, 2);
  els.recordsJson.value = JSON.stringify(defaultRecords(), null, 2);

  bind(els.healthBtn, checkHealth);
  bind(els.saveConfigBtn, saveConfig);
  bind(els.trainBtn, trainByConfigPath);
  bind(els.trainUploadBtn, trainByUpload);
  bind(els.predictRecordsBtn, predictRecords);
  bind(els.predictUploadBtn, predictByUpload);
}

init();
