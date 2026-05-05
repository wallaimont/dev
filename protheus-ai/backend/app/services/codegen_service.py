from langchain_ollama import ChatOllama
from langchain_core.prompts import ChatPromptTemplate
from app.config import get_settings
from app.services.rag_service import get_retriever
import logging

logger = logging.getLogger(__name__)
settings = get_settings()

CODEGEN_SYSTEM = """Você é um especialista em geração de código {language} para o sistema TOTVS Protheus.

Regras para geração de código:
- Use sintaxe correta de {language} ({lang_desc})
- Inclua cabeçalho com #Include quando necessário
- Use convenções do Protheus: nomes de variáveis com prefixo de tipo (c=char, n=numeric, l=logical, d=date, a=array, o=object)
- Comente o código em português
- Para TLPP: use classes, herança e tipagem estática quando apropriado
- Para AdvPL: use funções clássicas, User Functions e métodos de objetos
- Inclua tratamento de erros com BEGIN SEQUENCE / RECOVER SEQUENCE / END SEQUENCE
- Para acesso ao banco, use as funções padrão: DbSelectArea, MsSeek, RecLock, etc.
- Siga as práticas recomendadas pela TOTVS

Módulo de contexto: {module}

Contexto adicional da base de conhecimento:
{context}

Gere o código completo e funcional, com explicação clara de cada parte."""

CODEGEN_HUMAN = "Crie o seguinte código em {language}: {description}"

EXPLAIN_TEMPLATE = """Além do código, forneça uma explicação estruturada em JSON com os campos:
- "code": o código completo
- "explanation": explicação em português do que o código faz, passo a passo"""


async def generate_code(
    description: str,
    language: str = "tlpp",
    module: str | None = None,
    context: str | None = None,
) -> dict:
    llm = ChatOllama(
        model=settings.llm_model,
        base_url=settings.ollama_base_url,
        temperature=0.2,
    )

    retriever = get_retriever()
    relevant_docs = retriever.invoke(description)
    rag_context = "\n\n".join(doc.page_content for doc in relevant_docs)

    lang_desc = {
        "tlpp": "TLPP (TL++ Plus Plus) - linguagem orientada a objetos do Protheus, moderna",
        "advpl": "AdvPL (Advanced Protheus Language) - linguagem clássica do Protheus",
    }.get(language, language)

    prompt = ChatPromptTemplate.from_messages([
        ("system", CODEGEN_SYSTEM),
        ("human", CODEGEN_HUMAN + "\n\n" + EXPLAIN_TEMPLATE),
    ])

    chain = prompt | llm

    response = chain.invoke({
        "language": language.upper(),
        "lang_desc": lang_desc,
        "module": module or "Geral",
        "context": rag_context or "Nenhum contexto específico encontrado.",
        "description": description,
    })

    raw = response.content

    # Tenta extrair código e explicação do JSON retornado
    import json, re
    try:
        json_match = re.search(r"\{[\s\S]*\}", raw)
        if json_match:
            parsed = json.loads(json_match.group())
            return {
                "code": parsed.get("code", raw),
                "explanation": parsed.get("explanation", ""),
                "language": language,
            }
    except Exception:
        pass

    # Fallback: separa código do texto
    code_match = re.search(r"```(?:advpl|tlpp|pascal)?\n([\s\S]*?)```", raw, re.IGNORECASE)
    code = code_match.group(1) if code_match else raw
    explanation = raw.replace(code_match.group(0), "").strip() if code_match else ""

    return {
        "code": code,
        "explanation": explanation,
        "language": language,
    }
