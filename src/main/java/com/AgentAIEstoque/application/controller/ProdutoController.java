package com.AgentAIEstoque.application.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.AgentAIEstoque.application.dto.ProdutoRequestDTO;
import com.AgentAIEstoque.application.dto.ProdutoResponseDTO;
import com.AgentAIEstoque.application.entity.Produto;
import com.AgentAIEstoque.application.service.ProdutoService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/produtos")
@AllArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;
    
    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> cadastrarProduto(@RequestBody ProdutoRequestDTO request) {
      
        ProdutoResponseDTO response = produtoService.salvarProduto(request);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        
    }

    @GetMapping
    public ResponseEntity<List<ProdutoResponseDTO>> listarProdutos() {
        return ResponseEntity.ok(produtoService.listarTodos());
    }
}
