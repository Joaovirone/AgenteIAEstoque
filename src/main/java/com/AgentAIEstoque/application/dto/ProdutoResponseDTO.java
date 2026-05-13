package com.AgentAIEstoque.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.AgentAIEstoque.application.entity.Categoria;

public record ProdutoResponseDTO (

    UUID id,
    String nome,
    String sku,
    BigDecimal preco,
    Categoria categoria,
    Integer quantidadeAtual
) {}
