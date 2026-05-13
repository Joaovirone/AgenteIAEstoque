package com.AgentAIEstoque.application.dto.mapper;

import org.modelmapper.ModelMapper;

import com.AgentAIEstoque.application.dto.ProdutoRequestDTO;
import com.AgentAIEstoque.application.dto.ProdutoResponseDTO;
import com.AgentAIEstoque.application.entity.Produto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access= AccessLevel.PRIVATE)
public class ProdutoMapper {
    
    public static Produto toEntity(ProdutoRequestDTO produtoRequestDTO){

        return new ModelMapper().map(produtoRequestDTO, Produto.class);
    }

    public static ProdutoResponseDTO toProdutoResponseDTO(Produto produto){
        
        return new ModelMapper().map(produto, ProdutoResponseDTO.class);
    }
}
