from langchain_ollama import ChatOllama
from langchain_core.prompts import ChatPromptTemplate
from app.config import get_settings
from app.services.rag_service import get_retriever
import logging

logger = logging.getLogger(__name__)
settings = get_settings()

ANALYSIS_SYSTEM = """Você é um consultor especialista em TOTVS Protheus com mais de 15 anos de experiência.
Analise a questão do usuário com profundidade técnica e forneça:
1. Uma análise detalhada da situação
2. Recomendações práticas e implementáveis
3. Referências a módulos, tabelas e funções relevantes do Protheus
4. Possíveis impactos e pontos de atenção

Módulo de foco: {module}

Base de conhecimento relevante:
{context}

Responda sempre em português brasileiro, de forma técnica e objetiva."""

ANALYSIS_HUMAN = "{question}"


async def analyze(question: str, module: str | None = None) -> dict:
    llm = ChatOllama(
        model=settings.llm_model,
        base_url=settings.ollama_base_url,
        temperature=0.4,
    )

    retriever = get_retriever()
    relevant_docs = retriever.invoke(question)
    rag_context = "\n\n".join(doc.page_content for doc in relevant_docs)
    sources = list({
        doc.metadata.get("source", "Base de conhecimento")
        for doc in relevant_docs
    })

    prompt = ChatPromptTemplate.from_messages([
        ("system", ANALYSIS_SYSTEM),
        ("human", ANALYSIS_HUMAN),
    ])

    chain = prompt | llm

    response = chain.invoke({
        "module": module or "Todos os módulos",
        "context": rag_context or "Nenhum contexto específico encontrado.",
        "question": question,
    })

    raw = response.content

    # Extrai recomendações numeradas do texto
    import re
    recs = re.findall(r"(?:^|\n)\s*[\d•\-]+[.)]\s*(.+)", raw)
    recommendations = [r.strip() for r in recs[:8] if len(r.strip()) > 10]

    return {
        "analysis": raw,
        "recommendations": recommendations,
        "sources": sources,
    }
