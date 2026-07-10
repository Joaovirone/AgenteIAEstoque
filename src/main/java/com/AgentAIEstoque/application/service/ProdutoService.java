package com.AgentAIEstoque.application.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;

import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import com.AgentAIEstoque.application.dto.ProdutoRequestDTO;
import com.AgentAIEstoque.application.dto.ProdutoResponseDTO;
import com.AgentAIEstoque.application.dto.mapper.ProdutoMapper;
import com.AgentAIEstoque.application.entity.EstoqueAtual;
import com.AgentAIEstoque.application.entity.Produto;
import com.AgentAIEstoque.application.entity.enums.StatusProduto;
import com.AgentAIEstoque.application.exception.RegraNegocioException;
import com.AgentAIEstoque.application.repository.EstoqueAtualRepository;
import com.AgentAIEstoque.application.repository.ProdutoRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProdutoService {
    
    private final ProdutoRepository repository;
    private final EstoqueAtualRepository estoqueAtualRepository;
    private final ProdutoMapper mapper;

    @Transactional
    public ProdutoResponseDTO salvarProduto(ProdutoRequestDTO request){
        if (repository.existsBySku(request.sku())){
            throw new RegraNegocioException("Já existe um produto cadastrado com o SKU: " + request.sku());
        }

        Produto novo = mapper.toEntity(request);
        novo.setStatusProduto(StatusProduto.ATIVO);
        Produto salvo = repository.save(novo);

        EstoqueAtual estoqueAtual = EstoqueAtual.builder()
                .produto(salvo)
                .localArmazenamento("CD Principal")
                .quantidadeDisponivel(request.quantidadeAtual() != null ? request.quantidadeAtual() : 0)
                .estoqueMinimoSeguranca(10)
                .build();
        EstoqueAtual estoqueSalvo = estoqueAtualRepository.save(estoqueAtual);

        return toProdutoResponseDTO(salvo, estoqueSalvo);
    }

    @Transactional
    public List<ProdutoResponseDTO> listarTodos(){
        List<Produto> produtos = repository.findAllComCategoria();

        List<UUID> idsProdutos = produtos.stream()
            .map(Produto::getId)
            .toList();

        Map<UUID, EstoqueAtual> estoquePorProdutoId = estoqueAtualRepository.findByProdutoIdIn(idsProdutos)
            .stream()
            .collect(Collectors.toMap(estoque -> estoque.getProduto().getId(), Function.identity(), (atual, substituto) -> atual));

        return produtos.stream()
            .map(produto -> toProdutoResponseDTO(produto, estoquePorProdutoId.get(produto.getId())))
            .toList();
    }

        @Transactional
        public boolean existePorSku(String sku) {
        return repository.existsBySku(sku);
        }

        @Transactional
        public String gerarResumoRapidoEstoque() {
            long totalProdutos = repository.count();
            long totalItens = estoqueAtualRepository.somarQuantidadeDisponivel();
            long totalCriticos = estoqueAtualRepository.contarEstoqueCritico();

            List<String> exemplosCriticos = estoqueAtualRepository.listarNomesProdutosCriticos(PageRequest.of(0, 3));

            String base = "Resumo rapido do estoque: "
                    + "ha " + totalProdutos + " produtos cadastrados e "
                    + totalItens + " itens disponiveis no total. "
                    + "Produtos em nivel critico: " + totalCriticos + ".";

            if (exemplosCriticos.isEmpty()) {
                return base;
            }

            return base + " Exemplos de itens criticos: " + String.join(", ", exemplosCriticos) + ".";
        }

    @Transactional
    public ProdutoResponseDTO buscarPorId(UUID id) {
        Produto produto = buscarProdutoOuLancarExcecao(id);
        return toProdutoResponseDTO(produto, buscarEstoquePorProduto(produto));
    }

    @Transactional
    public ProdutoResponseDTO atualizarProduto(UUID id, ProdutoRequestDTO request) {
        Produto produtoExistente = buscarProdutoOuLancarExcecao(id);

        if (!produtoExistente.getSku().equalsIgnoreCase(request.sku()) && repository.existsBySku(request.sku())) {
            throw new RegraNegocioException("Já existe outro produto cadastrado com o SKU: " + request.sku());
        }

        produtoExistente.setNomeProduto(request.nome());
        produtoExistente.setSku(request.sku());
        produtoExistente.setPrecoCusto(request.preco());
        produtoExistente.setCategoria(request.categoria());

        Produto atualizado = repository.save(produtoExistente);

        EstoqueAtual estoqueAtual = estoqueAtualRepository.findByProdutoId(id)
                .orElseGet(() -> EstoqueAtual.builder()
                        .produto(atualizado)
                        .localArmazenamento("CD Principal")
                        .estoqueMinimoSeguranca(10)
                        .quantidadeDisponivel(0)
                        .build());

        if (request.quantidadeAtual() != null) {
            estoqueAtual.setQuantidadeDisponivel(request.quantidadeAtual());
        }

        EstoqueAtual estoqueSalvo = estoqueAtualRepository.save(estoqueAtual);
        
        return toProdutoResponseDTO(atualizado, estoqueSalvo);
    }

    @Transactional
    public void excluirProduto(UUID id) {
        Produto produto = buscarProdutoOuLancarExcecao(id);
        repository.delete(produto);
    }

    private Produto buscarProdutoOuLancarExcecao(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Produto com ID " + id + " não encontrado."));
    }

    private EstoqueAtual buscarEstoquePorProduto(Produto produto) {
        return estoqueAtualRepository.findByProdutoId(produto.getId())
                .orElse(null);
    }

    private ProdutoResponseDTO toProdutoResponseDTO(Produto produto, EstoqueAtual estoqueAtual) {
        return new ProdutoResponseDTO(
                produto.getId(),
                produto.getNomeProduto(),
                produto.getSku(),
                produto.getPrecoCusto(),
                produto.getStatusProduto().name(),
                produto.getCategoria() != null ? produto.getCategoria().getNomeCategoria() : null,
                estoqueAtual != null ? estoqueAtual.getQuantidadeDisponivel() : null,
                estoqueAtual != null ? estoqueAtual.getEstoqueMinimoSeguranca() : null,
                estoqueAtual != null ? estoqueAtual.getLocalArmazenamento() : null
        );
    }
}