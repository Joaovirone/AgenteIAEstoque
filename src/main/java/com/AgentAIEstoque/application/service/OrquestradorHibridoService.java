package com.AgentAIEstoque.application.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class OrquestradorHibridoService {
    
    private final ChatClient chatClient;
    
    public OrquestradorHibridoService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public String responderPergunta(String perguntaUsuario) {
        return chatClient.prompt()
                .system("Você é um assistente logístico sênior. Você DEVE usar as funções fornecidas para buscar dados antes de responder. Nunca invente dados de estoque ou regras de manuais.")
                .user(perguntaUsuario)
                .tools("consultarBancoDeDados", "consultarManuais") 
                .call()
                .content();
    }
}