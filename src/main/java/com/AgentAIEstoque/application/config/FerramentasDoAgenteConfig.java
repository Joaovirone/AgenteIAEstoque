package com.AgentAIEstoque.application.config;

import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import com.AgentAIEstoque.application.service.AgenteTextToSqlService;
import com.AgentAIEstoque.application.service.RagSearchService;

import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
public class FerramentasDoAgenteConfig {
    

    private final AgenteTextToSqlService sqlService;
    private final RagSearchService ragService;

    @Bean
    @Description("Use esta ferramenta APENAS quando o usuário perguntar sobre quantidades, preços, SKUs ou status exato de produtos no estoque.")
    public Function<String, String> consultarBancoDeDados() {
        return (pergunta) -> {
            System.out.println("🤖 IA decidiu usar a ferramenta: TEXT-TO-SQL");
            // Nota: Se o seu serviço SQL devolvia o JSON/Lista, você pode precisar 
            // ajustá-lo para devolver uma String legível aqui, ou o Spring AI converte automaticamente.
            return sqlService.traduzirPerguntaParaSql(pergunta);
        };
    }

    // Ferramenta 2: RAG
    @Bean
    @Description("Use esta ferramenta APENAS quando o usuário perguntar sobre manuais, manutenção, garantias, defeitos ou políticas internas.")
    public Function<String, String> consultarManuais() {
        return (pergunta) -> {
            System.out.println("🤖 IA decidiu usar a ferramenta: RAG (Manuais)");
            return ragService.buscarContextoRelevante(pergunta);
        };
    }

}
