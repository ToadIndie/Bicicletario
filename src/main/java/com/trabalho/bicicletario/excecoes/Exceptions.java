package com.trabalho.bicicletario.excecoes;

public class Exceptions extends RuntimeException {
    private final Erros erro;

    public Exceptions(Erros erro) {
        super(erro.getMensagem());
        this.erro = erro;
    }

    public Erros getErro() {
        return erro;
    }
}
