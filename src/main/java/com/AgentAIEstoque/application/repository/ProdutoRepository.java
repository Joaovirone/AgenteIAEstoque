package com.AgentAIEstoque.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;

import com.AgentAIEstoque.application.entity.Produto;

import java.util.*;

public interface ProdutoRepository extends JpaRepository<Produto, UUID> {
    
    boolean existsBySku(String sku);

    @EntityGraph(attributePaths = "categoria")
    @Query("select p from Produto p")
    List<Produto> findAllComCategoria();
}
