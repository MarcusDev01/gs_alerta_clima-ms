package br.com.fiap.alertaclima.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CidadeResponse {
    private Long id;
    private String nome;
    private String estado;
    private Double latitude;
    private Double longitude;
    private Integer populacao;
    private Boolean areaDeRisco;
    private LocalDateTime dataCadastro;
}
