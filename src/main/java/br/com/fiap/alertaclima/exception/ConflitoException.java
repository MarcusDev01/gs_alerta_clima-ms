package br.com.fiap.alertaclima.exception;

/** Conflito com o estado atual dos dados, ex.: e-mail duplicado (HTTP 409). */
public class ConflitoException extends RuntimeException {
    public ConflitoException(String mensagem) {
        super(mensagem);
    }
}
