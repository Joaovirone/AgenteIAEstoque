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

import lombok.AllArgsConstructor;

import java.util.*;

@Service
@AllArgsConstructor
public class RagIngestionService {
    
    private static final Logger log = LoggerFactory.getLogger(RagIngestionService.class);

    private final VectorStore vectorStore;

    @Value("classpath:/manuais/manual-parafusadeira.txt")
    private Resource manualResource;

    public void processarESalvarDocumento(){

        log.info("Iniciando leitura do documento");

        try {
            List<Document> documentosExtraidos = lerDocumento();

            List<Document> documentosFatiados = fatiarDocumento(documentosExtraidos);

            salvarNoBancoVetorial(documentosFatiados);
        } catch (Exception e) {

            log.error("Falha na ingestão do documento RAG: {}", e.getMessage(), e);
        }

        log.info("Documento ingerido e vetorizado com sucesso no pgvector");
    }

    private List<Document> lerDocumento() {
        log.debug("Lendo o documento da pasta resources...");
        TextReader textReader = new TextReader(manualResource);
        
        textReader.getCustomMetadata().put("tipo", "manual_manutencao");
        textReader.getCustomMetadata().put("ferramenta", "parafusadeira_bosch");
        
        return textReader.get();
    }

    private List<Document> fatiarDocumento(List<Document> documentosBrutos) {
        log.debug("Fatiando (Chunking) o documento em pedaços menores...");
        
        TokenTextSplitter splitter = new TokenTextSplitter();
        List<Document> fatias = splitter.apply(documentosBrutos);
        
        log.info("Documento dividido em {} blocos de contexto (Chunks).", fatias.size());
        return fatias;
    }

    private void salvarNoBancoVetorial(List<Document> documentosFatiados) {
        log.debug("Gerando Embeddings e salvando no banco vetorial...");
        vectorStore.accept(documentosFatiados);
        log.info("Ingestão concluída com sucesso! Os vetores estão prontos para o Orquestrador.");
    }

}
