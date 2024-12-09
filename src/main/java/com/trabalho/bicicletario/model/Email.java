package com.trabalho.bicicletario.model;

import jakarta.persistence.*;

@Entity
public class Email {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String destinatario;

    private String assunto;

    private String mensagem;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return destinatario;
    }

    public void setEmail(String email) {
        this.destinatario = email;
    }

    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
