import chromadb
from langchain_chroma import Chroma
from langchain_ollama import OllamaEmbeddings
from langchain_community.document_loaders import PyPDFLoader, WebBaseLoader
from langchain_text_splitters import RecursiveCharacterTextSplitter
from langchain_core.documents import Document
import os
import logging
from app.config import get_settings

logger = logging.getLogger(__name__)
settings = get_settings()

_vectorstore: Chroma | None = None


def get_vectorstore() -> Chroma:
    global _vectorstore
    if _vectorstore is None:
        embeddings = OllamaEmbeddings(
            model=settings.embedding_model,
            base_url=settings.ollama_base_url,
        )
        _vectorstore = Chroma(
            collection_name=settings.chroma_collection_name,
            embedding_function=embeddings,
            persist_directory=settings.chroma_persist_dir,
        )
    return _vectorstore


def get_text_splitter() -> RecursiveCharacterTextSplitter:
    return RecursiveCharacterTextSplitter(
        chunk_size=settings.chunk_size,
        chunk_overlap=settings.chunk_overlap,
        separators=["\n\n", "\n", ".", " ", ""],
    )


async def ingest_pdf(file_path: str) -> int:
    loader = PyPDFLoader(file_path)
    docs = loader.load()
    splitter = get_text_splitter()
    chunks = splitter.split_documents(docs)
    vs = get_vectorstore()
    vs.add_documents(chunks)
    logger.info(f"Ingeridos {len(chunks)} chunks do PDF: {file_path}")
    return len(chunks)


async def ingest_url(url: str) -> int:
    loader = WebBaseLoader(url)
    docs = loader.load()
    splitter = get_text_splitter()
    chunks = splitter.split_documents(docs)
    vs = get_vectorstore()
    vs.add_documents(chunks)
    logger.info(f"Ingeridos {len(chunks)} chunks da URL: {url}")
    return len(chunks)


async def ingest_text(text: str, metadata: dict | None = None) -> int:
    doc = Document(page_content=text, metadata=metadata or {})
    splitter = get_text_splitter()
    chunks = splitter.split_documents([doc])
    vs = get_vectorstore()
    vs.add_documents(chunks)
    return len(chunks)


def get_retriever():
    vs = get_vectorstore()
    return vs.as_retriever(
        search_type="similarity",
        search_kwargs={"k": settings.max_context_docs},
    )


def get_collection_stats() -> dict:
    vs = get_vectorstore()
    try:
        count = vs._collection.count()
    except Exception:
        count = 0
    return {
        "total_documents": count,
        "collection_name": settings.chroma_collection_name,
    }
