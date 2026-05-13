package com.AgentAIEstoque.application.controller;

import java.util.*;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import com.AgentAIEstoque.application.service.AgenteTextToSqlService;
import com.AgentAIEstoque.application.service.DatabaseExecutionService;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/estoque/chat")
@AllArgsConstructor
public class ChatController {

    private final AgenteTextToSqlService agenteService;
    private final DatabaseExecutionService databaseService;

    
    @PostMapping("/perguntar")
    public ResponseEntity<Map<String, Object>> perguntarAoAgente(@RequestBody Map<String, String> request) {
       
        String pergunta = request.get("pergunta");
        if(pergunta == null || pergunta.isBlank()){
            return ResponseEntity.badRequest().body(Map.of("erro", "A pergunta não pode estar vazia."));
        }

        Map<String, Object> respostaFinal = new HashMap<>();

        try {
            String sqlGerado = agenteService.traduzirPerguntaParaSql(pergunta);
            respostaFinal.put("sql_utilizado", sqlGerado);

            List<Map<String, Object>> dados = databaseService.executarSqlDinamico(sqlGerado);
            respostaFinal.put("dados", dados);

            return ResponseEntity.ok(respostaFinal);
        } catch (SecurityException se) {

            return ResponseEntity.status(403).body(Map.of("erro_seguranca", se.getMessage()));
        } catch (Exception e) {
            
            return ResponseEntity.internalServerError().body(Map.of("erro_processamento", e.getMessage()));
        }
        
    }
    
}
