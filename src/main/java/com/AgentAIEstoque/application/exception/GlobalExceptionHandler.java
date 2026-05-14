package com.AgentAIEstoque.application.exception;
import java.util.*;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<Map<String, String>> lidarComRegraDeNegocio(RegraNegocioException ex){

        return ResponseEntity.badRequest().body(Map.of("erro", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Map<String, String>> lidarComErrosDeValidacao(MethodArgumentNotValidException ex) {
            Map<String, String> erros = new HashMap<>();
            
            for (FieldError erro : ex.getBindingResult().getFieldErrors()) {
                erros.put(erro.getField(), erro.getDefaultMessage());
            }
            
            return ResponseEntity.badRequest().body(erros);
        }

    
}
