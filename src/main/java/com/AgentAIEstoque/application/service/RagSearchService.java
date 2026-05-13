package com.AgentAIEstoque.application.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;


import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RagSearchService {
    
    private static final Logger log = LoggerFactory.getLogger(RagSearchService.class);

    private final VectorStore vectorStore;

    public String buscarContextoRelevante(String perguntaUsuario) {
        log.info("Executando busca vetorial no pgvector para a pergunta: {}", perguntaUsuario);

        SearchRequest request = SearchRequest.builder()
                .query(perguntaUsuario)
                .topK(2)
                .similarityThreshold(0.7)
                .build();

        List<Document> documentosEncontrados = vectorStore.similaritySearch(request);

        if (documentosEncontrados.isEmpty()) {
            log.warn("Nenhum contexto relevante encontrado nos manuais.");
            return "Nenhuma informação encontrada nos manuais internos.";
        }

        String contextoFormatado = documentosEncontrados.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n---\n\n"));

        log.info("Contexto recuperado com sucesso. Tamanho: {} caracteres.", contextoFormatado.length());
        return contextoFormatado;
    }
}
