package com.AgentAIEstoque.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import com.AgentAIEstoque.application.dto.ProdutoRequestDTO;
import com.AgentAIEstoque.application.dto.ProdutoResponseDTO;
import com.AgentAIEstoque.application.dto.mapper.ProdutoMapper;
import com.AgentAIEstoque.application.entity.Produto;
import com.AgentAIEstoque.application.exception.RegraNegocioException;
import com.AgentAIEstoque.application.repository.ProdutoRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProdutoService {
    
    private final ProdutoRepository repository;
    private final ProdutoMapper mapper;

    @Transactional
    public ProdutoResponseDTO salvarProduto(ProdutoRequestDTO request){
        if (repository.existsBySku(request.sku())){
            throw new RegraNegocioException("Já existe um produto cadastrado com o SKU: " + request.sku());
        }

        Produto novo = mapper.toEntity(request);
        Produto salvo = repository.save(novo);

        return mapper.toProdutoResponseDTO(salvo);
    }

    public List<ProdutoResponseDTO> listarTodos(){
        return repository.findAll().stream()
                            .map(mapper::toProdutoResponseDTO)
                            .toList();
    }

    public ProdutoResponseDTO buscarPorId(UUID id) {
        Produto produto = buscarProdutoOuLancarExcecao(id);
        return mapper.toProdutoResponseDTO(produto);
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
        
        return mapper.toProdutoResponseDTO(atualizado);
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
}