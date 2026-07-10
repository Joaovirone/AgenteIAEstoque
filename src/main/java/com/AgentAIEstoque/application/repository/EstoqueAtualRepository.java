package com.AgentAIEstoque.application.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;

import com.AgentAIEstoque.application.entity.EstoqueAtual;

public interface EstoqueAtualRepository extends JpaRepository<EstoqueAtual, UUID> {

    Optional<EstoqueAtual> findByProdutoId(UUID produtoId);

    List<EstoqueAtual> findByProdutoIdIn(Collection<UUID> produtoIds);

    @Query("select coalesce(sum(e.quantidadeDisponivel), 0) from EstoqueAtual e")
    Long somarQuantidadeDisponivel();

    @Query("select count(e) from EstoqueAtual e where e.quantidadeDisponivel <= e.estoqueMinimoSeguranca")
    Long contarEstoqueCritico();

    @Query("select p.nomeProduto from EstoqueAtual e join e.produto p where e.quantidadeDisponivel <= e.estoqueMinimoSeguranca order by (e.estoqueMinimoSeguranca - e.quantidadeDisponivel) desc")
    List<String> listarNomesProdutosCriticos(Pageable pageable);
}
