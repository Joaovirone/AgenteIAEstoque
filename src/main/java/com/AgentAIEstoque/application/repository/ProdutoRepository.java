package com.AgentAIEstoque.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.AgentAIEstoque.application.entity.Produto;

import java.util.*;

public interface ProdutoRepository extends JpaRepository<Produto, UUID> {
    
    boolean existsBySku(String sku);
}
