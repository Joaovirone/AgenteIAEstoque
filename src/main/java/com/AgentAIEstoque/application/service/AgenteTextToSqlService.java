package com.AgentAIEstoque.application.service;

import java.nio.charset.StandardCharsets;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class AgenteTextToSqlService {

    private final ChatClient chatClient;

    @Value("classpath:/prompts/system-prompt-estoque.st")
    private Resource systemPromptResource;
    
    public AgenteTextToSqlService(ChatClient.Builder chatClientBuilder){

        this.chatClient = chatClientBuilder.build();
    }

    public String traduzirPerguntaParaSql (String perguntaUsuario){

        try {

            String systemPromptText = new String(systemPromptResource.getContentAsByteArray(), StandardCharsets.UTF_8);

            String sqlGerado = this.chatClient.prompt()
                                .system(systemPromptText)
                                .user(perguntaUsuario)
                                .call()
                                .content();

            return sqlGerado.trim();
        } catch (Exception e) {
            throw new RuntimeException("Falha de comunicação com o Ollama: " + e.getMessage());
        }
    }
}
