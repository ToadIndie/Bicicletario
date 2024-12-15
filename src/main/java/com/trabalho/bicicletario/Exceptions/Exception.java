package com.trabalho.bicicletario.Exceptions;

public class Exception extends RuntimeException {
    private final Erros erro;

    public Exception(Erros erro) {
        super(erro.getMensagem());
        this.erro = erro;
    }

    public Erros getErro() {
        return erro;
    }
}
