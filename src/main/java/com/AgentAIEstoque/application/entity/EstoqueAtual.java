package com.AgentAIEstoque.application.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "estoque_atual")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstoqueAtual {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(name = "local_armazenamento", nullable = false)
    private String localArmazenamento;

    @Column(name = "quantidade_disponivel", nullable = false)
    private Integer quantidadeDisponivel;

    @Column(name = "estoque_minimo_seguranca", nullable = false)
    private Integer estoqueMinimoSeguranca;
}
