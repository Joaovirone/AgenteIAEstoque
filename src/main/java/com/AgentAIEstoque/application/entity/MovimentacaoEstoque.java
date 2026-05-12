package com.AgentAIEstoque.application.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import com.AgentAIEstoque.application.entity.enums.TipoMovimento;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "movimentacoes_estoque")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimentacaoEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimento", nullable = false)
    private TipoMovimento tipoMovimento;

    @Column(name = "quantidade_movimentada", nullable = false)
    private Integer quantidadeMovimentada;

    @CreationTimestamp
    @Column(name = "data_movimento", nullable = false, updatable = false)
    private LocalDateTime dataMovimento;
}
