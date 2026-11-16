package br.com.fiap.alertaclima.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "Dados para cadastrar/atualizar uma cidade monitorada")
public class CidadeRequest {

    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres")
    @Schema(example = "São Paulo")
    private String nome;

    @NotBlank(message = "O estado é obrigatório")
    @Pattern(regexp = "^[A-Za-z]{2}$", message = "O estado deve ser a sigla da UF com 2 letras")
    @Schema(example = "SP")
    private String estado;

    @NotNull(message = "A latitude é obrigatória")
    @DecimalMin(value = "-90.0", message = "Latitude mínima é -90")
    @DecimalMax(value = "90.0", message = "Latitude máxima é 90")
    @Schema(example = "-23.5505")
    private Double latitude;

    @NotNull(message = "A longitude é obrigatória")
    @DecimalMin(value = "-180.0", message = "Longitude mínima é -180")
    @DecimalMax(value = "180.0", message = "Longitude máxima é 180")
    @Schema(example = "-46.6333")
    private Double longitude;

    @PositiveOrZero(message = "A população não pode ser negativa")
    @Schema(example = "11451245")
    private Integer populacao;

    @Schema(example = "true", description = "Indica se a cidade tem áreas de risco mapeadas pela Defesa Civil")
    private Boolean areaDeRisco;
}
