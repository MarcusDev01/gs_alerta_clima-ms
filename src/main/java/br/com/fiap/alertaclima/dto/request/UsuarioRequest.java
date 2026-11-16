package br.com.fiap.alertaclima.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Dados para cadastrar/atualizar um usuário que recebe alertas")
public class UsuarioRequest {

    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 3, max = 120, message = "O nome deve ter entre 3 e 120 caracteres")
    @Schema(example = "Maria Oliveira")
    private String nome;

    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "E-mail inválido")
    @Size(max = 150)
    @Schema(example = "maria.oliveira@email.com")
    private String email;

    @NotBlank(message = "O telefone é obrigatório")
    @Pattern(regexp = "^\\+?[0-9]{10,14}$", message = "Telefone deve ter de 10 a 14 dígitos (ex: 11987654321)")
    @Schema(example = "11987654321")
    private String telefone;

    @Schema(example = "true", description = "Se o usuário quer receber os alertas por SMS")
    private Boolean receberSms;
}
