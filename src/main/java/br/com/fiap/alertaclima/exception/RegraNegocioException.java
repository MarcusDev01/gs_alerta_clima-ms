package br.com.fiap.alertaclima.exception;

/** Dados válidos no formato, mas que quebram uma regra de negócio (HTTP 400). */
public class RegraNegocioException extends RuntimeException {
    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
