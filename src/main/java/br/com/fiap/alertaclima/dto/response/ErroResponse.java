package br.com.fiap.alertaclima.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/** Corpo padrão de todas as respostas de erro da API (sem stack trace ou detalhes internos). */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ErroResponse {
    private LocalDateTime timestamp;
    private int status;
    private String erro;
    private String mensagem;
    private String caminho;
    private List<CampoInvalido> campos;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CampoInvalido {
        private String campo;
        private String mensagem;
    }
}
