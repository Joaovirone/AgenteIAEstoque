package com.AgentAIEstoque.application.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import com.AgentAIEstoque.application.service.AgenteTextToSqlService;
import com.AgentAIEstoque.application.service.DatabaseExecutionService;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.AgentAIEstoque.application.service.OrquestradorHibridoService;
import com.AgentAIEstoque.application.service.RagIngestionService;


@RestController
@RequestMapping("/api/estoque/chat")
@AllArgsConstructor
public class ChatController {

    private final AgenteTextToSqlService agenteService;
    private final DatabaseExecutionService databaseService;

    private final RagIngestionService ragIngestionService;
    private final OrquestradorHibridoService orquestradorService;

    @PostMapping("/perguntar")
    public ResponseEntity<Map<String, String>> perguntarAoAgente(@RequestBody Map<String, String> request) {
       
        String pergunta = request.get("pergunta");
        if(pergunta == null || pergunta.isBlank()){
            return ResponseEntity.badRequest().body(Map.of("erro", "A pergunta não pode estar vazia."));
        }

        try {

            String respostaFinal = orquestradorService.responderPergunta(pergunta);

            return ResponseEntity.ok(Map.of("resposta", respostaFinal));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("erro_processamento", e.getMessage()));
        }
    }

    @PostMapping("/ingerir-documentos")
    public ResponseEntity<Map<String, String>> iniciarIngestaoDocumentos() {
        try {
            ragIngestionService.processarESalvarDocumento();
            return ResponseEntity.ok(Map.of("status", "Sucesso", "mensagem", "Manuais vetorizados no PostgreSQL."));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("erro", "Falha na ingestão: " + e.getMessage()));
        }
    }
    
    
}
