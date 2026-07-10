package com.AgentAIEstoque.application.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.AgentAIEstoque.application.dto.ChatResponseDTO;
import com.AgentAIEstoque.application.dto.MensagemResponseDTO;
import com.AgentAIEstoque.application.dto.PerguntaRequestDTO;
import com.AgentAIEstoque.application.service.OrquestradorHibridoService;
import com.AgentAIEstoque.application.service.RagIngestionService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;


@RestController
@RequestMapping("/api/estoque/chat")
@AllArgsConstructor
public class ChatController {


    private final RagIngestionService ragIngestionService;
    private final OrquestradorHibridoService orquestradorService;

    @PostMapping("/perguntar")
    public ResponseEntity<ChatResponseDTO> perguntarAoAgente(@RequestBody @Valid PerguntaRequestDTO request) {
        try {
            String respostaFinal = orquestradorService.responderPergunta(request.pergunta());
            return ResponseEntity.ok(new ChatResponseDTO(respostaFinal));
        } catch (RuntimeException ex) {
            String mensagem = ex.getMessage() == null ? "" : ex.getMessage();
            if (mensagem.contains("model") && mensagem.contains("not found")) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body(new ChatResponseDTO("Os modelos de IA ainda nao foram carregados no Ollama. Aguarde a inicializacao e tente novamente."));
            }
            throw ex;
        }
    }

    @PostMapping("/ingerir-documentos")
    public ResponseEntity<MensagemResponseDTO> iniciarIngestaoDocumentos() {
       
        ragIngestionService.processarESalvarDocumento();

        return ResponseEntity.ok( new MensagemResponseDTO("Sucesso", "Manuais foram vetorizados no Banco de Dados"));
    }
    
    
}
