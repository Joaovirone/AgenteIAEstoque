package com.AgentAIEstoque.application.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AgenteTextToSqlService {

    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = """
        Você é um arquiteto de banco de dados Sênior especializado em PostgreSQL.
        Sua única função é converter perguntas de linguagem natural em queries SQL válidas.
        
        Você tem acesso APENAS ao seguinte esquema de tabelas (Mini-Data Warehouse de Estoque):
        
        1. categorias (id UUID PK, nome_categoria VARCHAR)
        2. produtos (id UUID PK, categoria_id UUID FK, sku VARCHAR, nome_produto VARCHAR, preco_custo DECIMAL, status_produto VARCHAR)
        3. estoque_atual (id UUID PK, produto_id UUID FK, local_armazenamento VARCHAR, quantidade_disponivel INT, estoque_minimo_seguranca INT)
        4. movimentacoes_estoque (id UUID PK, produto_id UUID FK, tipo_movimento VARCHAR, quantidade_movimentada INT, data_movimento TIMESTAMP)
        
        REGRAS ABSOLUTAS (Sob pena de falha crítica):
        - Retorne EXCLUSIVAMENTE a query SQL em texto puro.
        - NÃO adicione formatação markdown (como ```sql ... 
```).
        - NÃO adicione NENHUMA explicação, cumprimento ou texto adicional.
        - Os valores possíveis para tipo_movimento são estritamente: 'ENTRADA', 'SAIDA', 'AJUSTE', 'DEVOLUCAO'.
        - O status_produto pode ser apenas 'ATIVO' ou 'DESCONTINUADO'.
        - Sempre use JOINs explícitos e aliases (ex: produtos p).
            """;
    
    public AgenteTextToSqlService(ChatClient.Builder chatClientBuilder){

        this.chatClient = chatClientBuilder
                        .defaultSystem(SYSTEM_PROMPT)
                        .build();
    }

    public String traduzirPerguntaParaSql (String perguntaUsuario){

        try {
            String sqlGerado = this.chatClient.prompt()
                                .user(perguntaUsuario)
                                .call()
                                .content();

            return sqlGerado.trim();
        } catch (Exception e) {
            throw new RuntimeException("Falha de comunicação com o Ollama: " + e.getMessage());
        }
    }
}
