package br.com.fiap.alertaclima.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cidades")
@Getter
@Setter
@NoArgsConstructor
public class Cidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, length = 2)
    private String estado;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    private Integer populacao;

    @Column(name = "area_de_risco", nullable = false)
    private Boolean areaDeRisco = false;

    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @OneToMany(mappedBy = "cidade")
    private List<Alerta> alertas = new ArrayList<>();

    @OneToMany(mappedBy = "cidade")
    private List<Monitoramento> monitoramentos = new ArrayList<>();

    @PrePersist
    void aoCriar() {
        dataCadastro = LocalDateTime.now();
        if (areaDeRisco == null) areaDeRisco = false;
    }
}
