package com.trabalho.bicicletario.excecoes;

public class ErrosDTO {
    private String codigo;
    private String mensagem;

    public ErrosDTO(String codigo, String mensagem) {
        this.codigo = codigo;
        this.mensagem = mensagem;
    }

    // Getters e Setters

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}