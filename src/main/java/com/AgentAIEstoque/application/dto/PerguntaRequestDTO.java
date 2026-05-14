package com.AgentAIEstoque.application.dto;
import jakarta.validation.constraints.NotBlank;

public record PerguntaRequestDTO(

    @NotBlank(message= "A pergunta não pode estar vazia!")
    String pergunta
) {}
