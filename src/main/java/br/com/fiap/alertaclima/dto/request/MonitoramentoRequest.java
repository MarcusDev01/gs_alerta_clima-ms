package br.com.fiap.alertaclima.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Vincula um usuário a uma cidade que ele quer acompanhar")
public class MonitoramentoRequest {

    @NotNull(message = "O id do usuário é obrigatório")
    @Positive(message = "O id do usuário deve ser positivo")
    @Schema(example = "1")
    private Long usuarioId;

    @NotNull(message = "O id da cidade é obrigatório")
    @Positive(message = "O id da cidade deve ser positivo")
    @Schema(example = "1")
    private Long cidadeId;

    @NotBlank(message = "O apelido é obrigatório")
    @Size(max = 50, message = "O apelido deve ter no máximo 50 caracteres")
    @Schema(example = "Casa da minha mãe")
    private String apelido;

    @Schema(example = "true")
    private Boolean favorita;

    @Schema(example = "true")
    private Boolean notificacoesAtivas;

    @Size(max = 255, message = "A observação deve ter no máximo 255 caracteres")
    @Schema(example = "Mora perto do córrego, avisar em caso de chuva forte")
    private String observacao;
}
