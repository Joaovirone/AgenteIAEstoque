package com.AgentAIEstoque.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProdutoResponseDTO (

    UUID id,
    String nome,
    String sku,
    BigDecimal preco,
    String status,
    String categoria,
    Integer quantidadeAtual,
    Integer estoqueMinimoSeguranca,
    String localArmazenamento
) {}
