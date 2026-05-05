# Transformers Python Starter

Projeto base para trabalhar com Hugging Face Transformers em Python com:
- inferencia de sentimento em portugues
- treino basico de classificacao de texto em portugues
- API com FastAPI para servir previsoes

## 1. Criar ambiente virtual

No Windows PowerShell:

```powershell
cd "c:\Users\wallace\Desktop\Nova pasta (3)\transformers-python-starter"
python -m venv .venv
.\.venv\Scripts\Activate.ps1
pip install --upgrade pip
pip install -r requirements.txt
```

## 2. Inferencia rapida

```powershell
python .\src\infer.py
```

## 3. Treino basico

```powershell
python .\src\train.py
```

Isso salva o modelo ajustado em `./model-output`.

## 4. Rodar API FastAPI

```powershell
uvicorn api.main:app --reload --host 0.0.0.0 --port 8000
```

Teste rapido:

```powershell
Invoke-RestMethod -Method Post -Uri "http://localhost:8000/predict" -ContentType "application/json" -Body '{"text":"Este atendimento foi excelente"}'
```

A API retorna os rotulos em portugues: `positivo`, `neutro` ou `negativo`.

Exemplo de resposta:

```json
{
	"label": "positivo",
	"original_label": "5 stars",
	"stars": 5,
	"score": 0.78
}
```

## Estrutura

- `src/infer.py`: inferencia com pipeline
- `src/train.py`: fine-tuning rapido com Trainer (dataset de exemplo em portugues)
- `api/main.py`: endpoint `/predict`

## Observacoes

- O primeiro download de modelo pode demorar.
- Se quiser CPU apenas, o codigo ja funciona sem GPU.
- Para producao, adicione autenticacao, logs, monitoramento e controle de versao de modelo.
