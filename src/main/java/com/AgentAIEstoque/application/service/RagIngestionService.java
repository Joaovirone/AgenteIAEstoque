package com.AgentAIEstoque.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RagIngestionService {
    
    private static final Logger log = LoggerFactory.getLogger(RagIngestionService.class);

    private final VectorStore vectorStore;

    @Value("classpath:/manuais/manual-parafusadeira.txt")
    private Resource manualResource;

    public void processarESalvarDocumento(){

        log.info("Iniciando leitura do documento");

        TextReader textReader = new TextReader(manualResource);

        textReader.getCustomMetadata().put("sku_relacionado", "FE-001");
        textReader.getCustomMetadata().put("tipo_documento", "manual_manutenção");

        TokenTextSplitter splitter = new TokenTextSplitter();

        log.info("Quebrando documento em vetores e enviando para o Ollama");

        vectorStore.accept(
                    splitter.apply(textReader.get())
        );      

        log.info("Documento ingerido e vetorizado com sucesso no pgvector");
    }

}
