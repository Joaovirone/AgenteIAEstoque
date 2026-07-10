package com.AgentAIEstoque.application.config;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import com.AgentAIEstoque.application.dto.ProdutoResponseDTO;
import com.AgentAIEstoque.application.service.ProdutoService;
import com.AgentAIEstoque.application.service.RagSearchService;

@Configuration
public class FerramentasDoAgenteConfig {



    @Bean
    @Description("Busca a lista de todos os produtos disponíveis no estoque do banco de dados, incluindo quantidades, SKUs e preços.")
    public Function<RequisicaoVazia, List<ProdutoResponseDTO>> consultarBancoDeDados(ProdutoService produtoService) {
        
        return requisicao -> produtoService.listarTodos();
    }

    @Bean
    @Description("Verifica se um produto específico existe no estoque através do seu código SKU.")
    public Function<RequisicaoSku, Boolean> consultarProdutoPorSku(ProdutoService produtoService) {
    
        return requisicao -> produtoService.existePorSku(requisicao.sku());
    }

    @Bean
    @Description("Consulta os manuais internos vetorizados para recuperar contexto técnico relevante para a pergunta do usuário.")
    public Function<RequisicaoPergunta, String> consultarManuais(RagSearchService ragSearchService) {
        return requisicao -> ragSearchService.buscarContextoRelevante(normalizarPergunta(requisicao.pergunta()));
    }

    private static String normalizarPergunta(Object payloadPergunta) {
        if (payloadPergunta == null) {
            return "";
        }

        if (payloadPergunta instanceof String texto) {
            return texto;
        }

        if (payloadPergunta instanceof Map<?, ?> mapa) {
            Object valorDireto = mapa.get("pergunta");
            if (valorDireto != null) {
                return normalizarPergunta(valorDireto);
            }

            Object query = mapa.get("query");
            if (query != null) {
                return normalizarPergunta(query);
            }

            Object texto = mapa.get("texto");
            if (texto != null) {
                return normalizarPergunta(texto);
            }

            return mapa.toString();
        }

        return payloadPergunta.toString();
    }

    public record RequisicaoVazia() {}
    public record RequisicaoSku(String sku) {}
    public record RequisicaoPergunta(Object pergunta) {}

}