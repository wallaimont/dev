# 🤖 ProtheusAI

Plataforma de inteligência artificial especializada no ecossistema **TOTVS Protheus**, com RAG (Retrieval-Augmented Generation) sobre toda a documentação técnica do sistema.

## ✨ Funcionalidades

| Recurso | Descrição |
|---------|-----------|
| 💬 **Chat IA** | Responde dúvidas sobre módulos, APIs, TLPP, AdvPL com memória de sessão |
| ⚙️ **Gerador de Código** | Gera código AdvPL ou TLPP a partir de uma descrição em linguagem natural |
| 📊 **Análise Técnica** | Consultoria aprofundada com recomendações estruturadas |
| 📚 **Base de Conhecimento** | Ingestão de PDFs e URLs para enriquecer o contexto da IA |

## 🏗️ Stack

- **Backend**: FastAPI · LangChain 0.3 · ChromaDB · OpenAI GPT-4o
- **Frontend**: React 18 · Vite 5 · Lucide Icons
- **IA**: RAG com `text-embedding-3-small` + `gpt-4o`

## 🚀 Início Rápido

### Pré-requisitos

- Python 3.11+
- Node.js 20+
- Chave de API da OpenAI

### 1. Clone e configure

```bash
git clone <repo>
cd protheus-ai
```

### 2. Backend

```bash
cd backend
cp .env.example .env
# Edite .env e insira sua OPENAI_API_KEY
pip install -r requirements.txt
python main.py
```

O backend sobe em `http://localhost:8000`. A base de conhecimento Protheus é indexada automaticamente na primeira execução.

### 3. Frontend

```bash
cd frontend
npm install
npm run dev
```

Acesse `http://localhost:5173`.

---

## 🐳 Docker (produção)

```bash
# 1. Configure o .env do backend
cp backend/.env.example backend/.env
# Edite backend/.env com sua OPENAI_API_KEY

# 2. Suba os serviços
docker compose up --build -d
```

Acesse `http://localhost:5173`.

---

## ⚙️ Variáveis de Ambiente

| Variável | Padrão | Descrição |
|----------|--------|-----------|
| `OPENAI_API_KEY` | — | **Obrigatório**. Chave da OpenAI |
| `OPENAI_MODEL` | `gpt-4o` | Modelo LLM |
| `OPENAI_EMBEDDING_MODEL` | `text-embedding-3-small` | Modelo de embeddings |
| `PORT` | `8000` | Porta do backend |
| `CHROMA_PERSIST_DIR` | `./vectorstore` | Diretório de persistência do ChromaDB |
| `CHROMA_COLLECTION_NAME` | `protheus_knowledge` | Nome da coleção |
| `CHUNK_SIZE` | `1000` | Tamanho dos chunks de texto |
| `CHUNK_OVERLAP` | `200` | Sobreposição entre chunks |
| `MAX_CONTEXT_DOCS` | `6` | Máximo de documentos no contexto RAG |

---

## 📁 Estrutura do Projeto

```
protheus-ai/
├── backend/
│   ├── app/
│   │   ├── models/schemas.py        # Modelos Pydantic
│   │   ├── routes/                  # Endpoints FastAPI
│   │   │   ├── chat.py              # POST /api/chat
│   │   │   ├── codegen.py           # POST /api/codegen
│   │   │   ├── analysis.py          # POST /api/analysis
│   │   │   └── docs.py              # POST /api/docs/upload, /ingest-url
│   │   ├── services/
│   │   │   ├── rag_service.py       # ChromaDB + ingestão
│   │   │   ├── chat_service.py      # Chat com memória de sessão
│   │   │   ├── codegen_service.py   # Geração de código AdvPL/TLPP
│   │   │   └── analysis_service.py  # Análise técnica
│   │   ├── config.py                # Settings via pydantic-settings
│   │   └── __init__.py              # FastAPI app + CORS + routers
│   ├── knowledge_base/
│   │   └── protheus_knowledge.md    # Base de conhecimento pré-carregada
│   ├── main.py                      # Entry point
│   ├── requirements.txt
│   └── .env.example
├── frontend/
│   ├── src/
│   │   ├── pages/
│   │   │   ├── ChatPage.jsx
│   │   │   ├── CodeGenPage.jsx
│   │   │   ├── AnalysisPage.jsx
│   │   │   └── DocsPage.jsx
│   │   ├── components/
│   │   │   ├── Sidebar.jsx
│   │   │   └── MarkdownRenderer.jsx
│   │   ├── App.jsx
│   │   └── main.jsx
│   ├── vite.config.js               # Proxy /api → localhost:8000
│   └── package.json
├── docker-compose.yml
└── README.md
```

## 📄 API Reference

### `POST /api/chat`
```json
{ "message": "Como usar RecLock no AdvPL?", "session_id": "uuid-opcional" }
```

### `POST /api/codegen`
```json
{ "description": "Buscar pedidos em aberto de um cliente", "language": "advpl", "module": "SIGAVND" }
```

### `POST /api/analysis`
```json
{ "question": "Melhor estratégia para customizar NF de saída", "module": "SIGAFIS - Fiscal" }
```

### `POST /api/docs/upload`
Multipart form-data com campo `file` (PDF, máx 50MB).

### `POST /api/docs/ingest-url`
```json
{ "url": "https://tdn.totvs.com/..." }
```

### `GET /api/docs/stats`
Retorna `{ "total_documents": 1234 }`.

### `GET /api/health`
Health check do serviço.
