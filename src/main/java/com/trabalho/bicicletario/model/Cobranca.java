package com.trabalho.bicicletario.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Cobranca {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String status;

    private LocalDateTime horaSolicitacao;

    private LocalDateTime horaFinalizacao;

    private double valor;

    private int ciclista;


    public Integer getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getHoraSolicitacao() {
        return horaSolicitacao;
    }

    public LocalDateTime getHoraFinalizacao() {
        return horaFinalizacao;
    }

    public double getValor() {
        return valor;
    }

    public int getCiclista() {
        return ciclista;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setHoraSolicitacao(LocalDateTime horaSolicitacao) {
        this.horaSolicitacao = horaSolicitacao;
    }

    public void setHoraFinalizacao(LocalDateTime horaFinalizacao) {
        this.horaFinalizacao = horaFinalizacao;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public void setCiclista(int ciclista) {
        this.ciclista = ciclista;
    }
}