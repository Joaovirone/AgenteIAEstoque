package com.AgentAIEstoque.application.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.AgentAIEstoque.application.entity.EstoqueAtual;

public interface EstoqueAtualRepository extends JpaRepository<EstoqueAtual, UUID> {

    Optional<EstoqueAtual> findByProdutoId(UUID produtoId);

    List<EstoqueAtual> findByProdutoIdIn(Collection<UUID> produtoIds);
}
