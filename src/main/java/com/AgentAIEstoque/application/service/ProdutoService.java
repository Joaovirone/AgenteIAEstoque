package com.AgentAIEstoque.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.AgentAIEstoque.application.dto.ProdutoRequestDTO;
import com.AgentAIEstoque.application.dto.ProdutoResponseDTO;
import com.AgentAIEstoque.application.dto.mapper.ProdutoMapper;
import com.AgentAIEstoque.application.entity.Produto;
import com.AgentAIEstoque.application.exception.RegraNegocioException;
import com.AgentAIEstoque.application.repository.ProdutoRepository;

import jakarta.transaction.Transactional;
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
                            .map(mapper :: toProdutoResponseDTO)
                            .toList();
    }

}
