package br.com.fiap.alertaclima.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MonitoramentoResponse {
    private Long id;
    private String apelido;
    private Boolean favorita;
    private Boolean notificacoesAtivas;
    private String observacao;
    private LocalDateTime dataInicio;
    private Long usuarioId;
    private String usuarioNome;
    private Long cidadeId;
    private String cidadeNome;
    private String cidadeEstado;
}
