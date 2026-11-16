package br.com.fiap.alertaclima.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/** Cidade que um usuário acompanha (onde mora, trabalha ou onde vivem familiares). */
@Entity
@Table(name = "monitoramentos",
        uniqueConstraints = @UniqueConstraint(name = "uk_monitoramento_usuario_cidade",
                columnNames = {"usuario_id", "cidade_id"}))
@Getter
@Setter
@NoArgsConstructor
public class Monitoramento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String apelido;

    @Column(nullable = false)
    private Boolean favorita = false;

    @Column(name = "notificacoes_ativas", nullable = false)
    private Boolean notificacoesAtivas = true;

    @Column(length = 255)
    private String observacao;

    @Column(name = "data_inicio", nullable = false, updatable = false)
    private LocalDateTime dataInicio;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "cidade_id", nullable = false)
    private Cidade cidade;

    @PrePersist
    void aoCriar() {
        dataInicio = LocalDateTime.now();
        if (favorita == null) favorita = false;
        if (notificacoesAtivas == null) notificacoesAtivas = true;
    }
}
