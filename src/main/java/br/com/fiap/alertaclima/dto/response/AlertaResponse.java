package br.com.fiap.alertaclima.dto.response;

import br.com.fiap.alertaclima.enums.NivelAlerta;
import br.com.fiap.alertaclima.enums.TipoAlerta;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlertaResponse {
    private Long id;
    private TipoAlerta tipo;
    private NivelAlerta nivel;
    private String titulo;
    private String descricao;
    private String recomendacao;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private Boolean ativo;
    private Long cidadeId;
    private String cidadeNome;
    private String cidadeEstado;
}
