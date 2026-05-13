package com.AgentAIEstoque.application.exception;
import java.util.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<Map<String, String>> lidarComRegraDeNegocio(RegraNegocioException ex){

        return ResponseEntity.badRequest().body(Map.of("erro", ex.getMessage()));
    }
    
}
