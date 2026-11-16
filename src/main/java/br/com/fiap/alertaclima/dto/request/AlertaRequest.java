package br.com.fiap.alertaclima.dto.request;

import br.com.fiap.alertaclima.enums.NivelAlerta;
import br.com.fiap.alertaclima.enums.TipoAlerta;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Dados para emitir/atualizar um alerta climático para uma cidade")
public class AlertaRequest {

    @NotNull(message = "O id da cidade é obrigatório")
    @Positive(message = "O id da cidade deve ser positivo")
    @Schema(example = "1")
    private Long cidadeId;

    @NotNull(message = "O tipo do alerta é obrigatório")
    @Schema(example = "CHUVA_FORTE")
    private TipoAlerta tipo;

    @NotNull(message = "O nível do alerta é obrigatório")
    @Schema(example = "PERIGO")
    private NivelAlerta nivel;

    @NotBlank(message = "O título é obrigatório")
    @Size(max = 120, message = "O título deve ter no máximo 120 caracteres")
    @Schema(example = "Chuva forte prevista para a tarde")
    private String titulo;

    @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
    @Schema(example = "Acumulado de 60 mm em 24h, com risco de alagamentos e deslizamentos.")
    private String descricao;

    @Size(max = 500, message = "A recomendação deve ter no máximo 500 caracteres")
    @Schema(example = "Não atravesse ruas alagadas e desligue a energia se a água entrar em casa.")
    private String recomendacao;

    @NotNull(message = "A data de início é obrigatória")
    @Schema(example = "2026-11-17T12:00:00")
    private LocalDateTime dataInicio;

    @Schema(example = "2026-11-18T06:00:00")
    private LocalDateTime dataFim;

    @Schema(example = "true")
    private Boolean ativo;
}
