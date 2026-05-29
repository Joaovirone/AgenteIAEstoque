package com.AgentAIEstoque.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RagIngestionService {

    private static final Logger log = LoggerFactory.getLogger(RagIngestionService.class);

    // Injeção direta pelo Spring
    private final VectorStore vectorStore;

    // Injeta o ficheiro como um Resource do classpath
    @Value("classpath:manuais/manual-parafusadeira.txt")
    private Resource manualResource;

    // Construtor explícito APENAS para o VectorStore
    // Não colocamos o 'Resource' aqui para que o Spring não se confunda!
    public RagIngestionService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void processarESalvarDocumento() {
        log.info("A iniciar o pipeline de ingestão para o ficheiro: {}", manualResource.getFilename());

        try {
            List<Document> documentosExtraidos = lerDocumento();
            List<Document> documentosFatiados = fatiarDocumento(documentosExtraidos);
            salvarNoBancoVetorial(documentosFatiados);
        } catch (Exception e) {
            log.error("Falha catastrófica na ingestão do documento RAG: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao processar manuais. Verifique os logs do servidor."); 
        }
    }

    private List<Document> lerDocumento() {
        log.debug("A ler o documento da pasta resources...");
        TextReader textReader = new TextReader(manualResource);
        
        textReader.getCustomMetadata().put("tipo", "manual_manutencao");
        textReader.getCustomMetadata().put("ferramenta", "parafusadeira_bosch");
        
        return textReader.get();
    }

    private List<Document> fatiarDocumento(List<Document> documentosBrutos) {
        log.debug("A fatiar (Chunking) o documento em pedaços menores...");
        TokenTextSplitter splitter = new TokenTextSplitter();
        List<Document> fatias = splitter.apply(documentosBrutos);
        
        log.info("Documento dividido em {} blocos de contexto (Chunks).", fatias.size());
        return fatias;
    }

    private void salvarNoBancoVetorial(List<Document> documentosFatiados) {
        log.debug("A gerar Embeddings e a guardar no banco vetorial...");
        vectorStore.accept(documentosFatiados);
        log.info("Ingestão concluída com sucesso! Os vetores estão prontos para o Orquestrador.");
    }
}