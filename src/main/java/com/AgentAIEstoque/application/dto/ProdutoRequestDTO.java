package com.AgentAIEstoque.application.dto;

import java.math.BigDecimal;

import com.AgentAIEstoque.application.entity.Categoria;

public record ProdutoRequestDTO(


    String nome, 
    String sku, 
    BigDecimal preco, 
    Categoria categoria, 
    Integer quantidadeAtual
) {}
