from langchain_ollama import ChatOllama
from langchain_core.prompts import ChatPromptTemplate, MessagesPlaceholder
from langchain_core.messages import HumanMessage, AIMessage
from langchain_core.output_parsers import StrOutputParser
from langchain_core.runnables import RunnablePassthrough
from app.config import get_settings
from app.services.rag_service import get_retriever
import logging

logger = logging.getLogger(__name__)
settings = get_settings()

SYSTEM_PROMPT = """Você é ProtheusAI, um assistente especialista no sistema ERP TOTVS Protheus.
Seu conhecimento abrange:
- Linguagens de programação: AdvPL e TLPP (TL++ Plus Plus)
- Módulos: Financeiro (SIGAFIN), Fiscal (SIGAFIS), Contábil (SIGACON), Estoque (SIGAEST), 
  Compras (SIGACOM), Vendas (SIGAVND), RH/DP (SIGAPON, SIGAGPE), Produção (SIGAPCP),
  Manutenção (SIGAMNT), Ativo Fixo (SIGAATF), CRM (SIGACRM), Fiscal Escrituração (SIGAEFD)
- APIs REST do Protheus e integração com Fluig
- Tabelas do sistema (SB1, SA1, SA2, SF2, SF1, SE1, SE2...)
- Funções nativas: MaFisRef, ExecBlock, FWMVCMODEL, FWBrwHelp, etc.
- TOTVS Carol, TOTVS Fluig, TOTVS Datasul
- Configuração de ambiente, AppServer, SmartClient, DBAccess
- Integração via REST, SOAP e Message Broker

Use o contexto fornecido para responder com precisão. Se não encontrar a informação no contexto,
informe o usuário e responda com base no seu conhecimento geral sobre Protheus.
Responda sempre em português brasileiro, de forma clara e técnica.

Contexto relevante:
{context}"""

CHAT_PROMPT = ChatPromptTemplate.from_messages([
    ("system", SYSTEM_PROMPT),
    MessagesPlaceholder(variable_name="chat_history"),
    ("human", "{question}"),
])

# Armazena histórico de mensagens por sessão (máx. 10 pares)
_sessions: dict[str, list] = {}


def get_llm() -> ChatOllama:
    return ChatOllama(
        model=settings.llm_model,
        base_url=settings.ollama_base_url,
        temperature=0.3,
    )


def get_history(session_id: str) -> list:
    if session_id not in _sessions:
        _sessions[session_id] = []
    return _sessions[session_id]


async def chat(message: str, session_id: str = "default") -> dict:
    llm = get_llm()
    retriever = get_retriever()
    history = get_history(session_id)

    # Recupera documentos relevantes
    relevant_docs = retriever.invoke(message)
    context = "\n\n".join(doc.page_content for doc in relevant_docs)
    sources = list({
        doc.metadata.get("source", "Base de conhecimento")
        for doc in relevant_docs
    })

    # Monta e executa o pipeline LCEL
    chain = CHAT_PROMPT | llm | StrOutputParser()
    answer = chain.invoke({
        "context": context or "Nenhum contexto específico encontrado.",
        "chat_history": history,
        "question": message,
    })

    # Atualiza histórico (mantém últimas 10 trocas)
    history.append(HumanMessage(content=message))
    history.append(AIMessage(content=answer))
    if len(history) > 20:
        _sessions[session_id] = history[-20:]

    return {
        "answer": answer,
        "sources": sources,
    }
