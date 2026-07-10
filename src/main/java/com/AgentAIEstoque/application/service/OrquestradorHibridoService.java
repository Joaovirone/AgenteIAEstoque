package com.AgentAIEstoque.application.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class OrquestradorHibridoService {

    private static final long TIMEOUT_CHAT_SEGUNDOS = 45;
    
    private final ChatClient chatClient;
    
    public OrquestradorHibridoService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public String responderPergunta(String perguntaUsuario) {
        try {
            return CompletableFuture.supplyAsync(() -> chatClient.prompt()
                    .system("Você é um assistente logístico sênior. Você DEVE usar as funções fornecidas para buscar dados antes de responder. Nunca invente dados de estoque ou regras de manuais.")
                    .user(perguntaUsuario)
                    .tools("consultarBancoDeDados", "consultarManuais")
                    .call()
                    .content())
                    .orTimeout(TIMEOUT_CHAT_SEGUNDOS, TimeUnit.SECONDS)
                    .join();
        } catch (CompletionException ex) {
            if (ex.getCause() instanceof TimeoutException) {
                throw new RuntimeException("Tempo limite ao consultar o modelo de IA.", ex);
            }
            throw ex;
        }
    }
}