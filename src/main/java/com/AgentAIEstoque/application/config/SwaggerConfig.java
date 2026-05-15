package com.AgentAIEstoque.application.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "AgenteAI Estoque",
        version = "1.0",
        description = "Documentação da API para o AgenteAI Estoque",
        license= @io.swagger.v3.oas.annotations.info.License(
            name = "Apache License 2.0",
            url = "https://www.apache.org/licenses/LICENSE-2.0"
        ),
        contact = @io.swagger.v3.oas.annotations.info.Contact(
            name = "João Vitor Pereira (Joãovirone)",
            email = "jovmamikl@gmail.com" 
        )

        
    )
)
public class SwaggerConfig {

    

    
}
