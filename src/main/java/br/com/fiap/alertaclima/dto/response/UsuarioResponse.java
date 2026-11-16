package br.com.fiap.alertaclima.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UsuarioResponse {
    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private Boolean receberSms;
    private LocalDateTime dataCadastro;
}
