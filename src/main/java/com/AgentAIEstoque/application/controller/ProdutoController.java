package com.AgentAIEstoque.application.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.AgentAIEstoque.application.dto.ProdutoRequestDTO;
import com.AgentAIEstoque.application.entity.Produto;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/produtos")
@AllArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;
    
    @PostMapping
    public ResponseEntity<?> cadastrarProduto(@RequestBody ProdutoRequestDTO request) {
        try {
            Produto produtoSalvo = produtoService.salvarProduto(request);
            return ResponseEntity.ok(produtoSalvo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Produto>> listarProdutos() {
        return ResponseEntity.ok(produtoService.listarTodos());
    }
}
