package com.seguradora.site.rag;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class LangChain4jRagService {

        private static final Logger log = LoggerFactory.getLogger(LangChain4jRagService.class);

    private final String apiKey;
        private final boolean seedOnStartup;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final EmbeddingModel embeddingModel;
    private final ChatLanguageModel chatModel;
        private final DocumentByParagraphSplitter documentSplitter;
    private final AtomicInteger docsIngeridos = new AtomicInteger(0);
    private final AtomicInteger segmentosIngeridos = new AtomicInteger(0);

    public LangChain4jRagService(
            @Value("${OPENAI_API_KEY:demo-key}") String apiKey,
                        @Value("${app.rag.seed-on-startup:false}") boolean seedOnStartup,
            @Value("${OPENAI_MODEL:gpt-4o-mini}") String chatModelName,
            @Value("${OPENAI_EMBEDDING_MODEL:text-embedding-3-small}") String embeddingModelName
    ) {
        this.apiKey = apiKey;
                this.seedOnStartup = seedOnStartup;
        this.embeddingStore = new InMemoryEmbeddingStore<>();
        this.embeddingModel = OpenAiEmbeddingModel.builder()
                .apiKey(apiKey)
                .modelName(embeddingModelName)
                .build();
        this.chatModel = OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(chatModelName)
                .temperature(0.2)
                .build();
        this.documentSplitter = new DocumentByParagraphSplitter(600, 100);
    }

    @PostConstruct
    public void carregarBaseInicial() {
                if (!seedOnStartup || apiKeyInvalida()) {
            return;
        }

        String conteudoInicial = """
                A Seguradora Java Site oferece quatro linhas principais de seguro: Auto, Residencial, Empresarial e Vida.
                Seguro Auto: cobertura contra colisao, roubo e assistencia 24h.
                Seguro Residencial: protecao para estrutura do imovel, conteudo e responsabilidade civil.
                Seguro Empresarial: cobertura patrimonial, equipamentos e lucros cessantes.
                Seguro de Vida: cobertura para morte, invalidez e assistencia familiar.
                O processo de cotacao coleta nome, email, telefone, tipo de seguro, cobertura e valor aproximado do bem.
                O atendimento prioriza resposta consultiva e personalizacao por perfil de cliente.
                """;

                try {
                        ingerirDocumento("base-inicial-seguros", conteudoInicial);
                } catch (Exception ex) {
                        log.warn("Falha ao carregar base inicial RAG no startup: {}", ex.getMessage());
                }
    }

    public IngestaoResultado ingerirDocumento(String titulo, String conteudo) {
        validarApiKey();

        Document document = Document.from(
                conteudo,
                Metadata.from("source", titulo)
        );

        List<TextSegment> segmentos = documentSplitter.split(document);
        List<Embedding> embeddings = embeddingModel.embedAll(segmentos).content();
        embeddingStore.addAll(embeddings, segmentos);

        docsIngeridos.incrementAndGet();
        segmentosIngeridos.addAndGet(segmentos.size());

        return new IngestaoResultado(titulo, segmentos.size(), docsIngeridos.get(), segmentosIngeridos.get());
    }

    public ConsultaResultado perguntar(String pergunta, int maxResultados, double scoreMinimo) {
        validarApiKey();

        Embedding embeddingPergunta = embeddingModel.embed(pergunta).content();
        List<EmbeddingMatch<TextSegment>> matches = new ArrayList<>(
                embeddingStore.findRelevant(embeddingPergunta, maxResultados, scoreMinimo)
        );

        matches.sort(Comparator.comparingDouble(EmbeddingMatch<TextSegment>::score).reversed());

        String contexto = matches.stream()
                .map(match -> "[score=" + String.format("%.4f", match.score()) + "] " + match.embedded().text())
                .collect(Collectors.joining("\n\n"));

        String promptFinal = """
                Voce e um assistente da Seguradora Java Site.
                Use APENAS o contexto a seguir para responder.
                Se o contexto for insuficiente, diga explicitamente que nao encontrou a informacao na base.

                Contexto:
                %s

                Pergunta:
                %s
                """.formatted(contexto.isBlank() ? "(sem contexto recuperado)" : contexto, pergunta);

        String resposta = chatModel.generate(promptFinal);

        List<TrechoRecuperado> trechos = matches.stream()
                .map(match -> new TrechoRecuperado(match.score(), match.embedded().text()))
                .toList();

        return new ConsultaResultado(resposta, trechos, docsIngeridos.get(), segmentosIngeridos.get());
    }

    public StatusRag status() {
        return new StatusRag(docsIngeridos.get(), segmentosIngeridos.get(), !apiKeyInvalida());
    }

    private void validarApiKey() {
        if (apiKeyInvalida()) {
            throw new IllegalStateException("OPENAI_API_KEY nao configurada. Defina a variavel de ambiente para usar o RAG.");
        }
    }

    private boolean apiKeyInvalida() {
        return apiKey == null || apiKey.isBlank() || "demo-key".equals(apiKey);
    }

    public record IngestaoResultado(
            String titulo,
            int segmentosGerados,
            int totalDocumentos,
            int totalSegmentos
    ) {
    }

    public record TrechoRecuperado(
            double score,
            String texto
    ) {
    }

    public record ConsultaResultado(
            String resposta,
            List<TrechoRecuperado> contextoRecuperado,
            int totalDocumentos,
            int totalSegmentos
    ) {
    }

    public record StatusRag(
            int totalDocumentos,
            int totalSegmentos,
            boolean apiKeyConfigurada
    ) {
    }
}