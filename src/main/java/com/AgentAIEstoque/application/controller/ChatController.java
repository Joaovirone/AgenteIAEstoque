package com.AgentAIEstoque.application.controller;

import java.util.Map;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import com.AgentAIEstoque.application.service.AgenteTextToSqlService;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/estoque/chat")
@AllArgsConstructor
public class ChatController {

    private final AgenteTextToSqlService agenteService;

    
    @PostMapping("/gerar-sql")
    public ResponseEntity<Map<String, String>> gerarSql(@RequestBody Map<String, String> request) {
       
        String pergunta = request.get("pergunta");
        if(pergunta == null || pergunta.isBlank()){
            return ResponseEntity.badRequest().body(Map.of("erro", "A pergunta não pode estar vazia."));
        }
        
        String sql = agenteService.traduzirPerguntaParaSql(pergunta);

        return ResponseEntity.ok(Map.of("sql_gerado", sql));
    }
    
}
