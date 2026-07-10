package com.AgentAIEstoque.application.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.Locale;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class OrquestradorHibridoService {

    private static final long TIMEOUT_CHAT_SEGUNDOS = 45;
    
    private final ChatClient chatClient;
    private final ProdutoService produtoService;
    
    public OrquestradorHibridoService(ChatClient.Builder chatClientBuilder, ProdutoService produtoService) {
        this.chatClient = chatClientBuilder.build();
        this.produtoService = produtoService;
    }

    public String responderPergunta(String perguntaUsuario) {
        if (deveUsarRespostaRapida(perguntaUsuario)) {
            return produtoService.gerarResumoRapidoEstoque();
        }

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

    private boolean deveUsarRespostaRapida(String perguntaUsuario) {
        if (perguntaUsuario == null || perguntaUsuario.isBlank()) {
            return false;
        }

        String texto = perguntaUsuario.toLowerCase(Locale.ROOT);

        boolean perguntaQuantidade = texto.contains("quantos")
                || texto.contains("quantidade")
                || texto.contains("total")
                || texto.contains("resumo");

        boolean contextoEstoque = texto.contains("estoque")
                || texto.contains("itens")
                || texto.contains("produtos")
                || texto.contains("critico")
                || texto.contains("minimo");

        boolean perguntaManual = texto.contains("manual")
                || texto.contains("procedimento")
                || texto.contains("instru")
                || texto.contains("parafusadeira");

        return perguntaQuantidade && contextoEstoque && !perguntaManual;
    }
}