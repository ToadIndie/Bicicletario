package com.trabalho.bicicletario.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
public class CartaoDeCredito {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String numero;

    private String titular;

    private LocalDate validade;

    private String cvv;

    public String getNumero() {
        return numero;
    }

    public String getTitular() {
        return titular;
    }

    public LocalDate getValidade() {
        return validade;
    }

    public String getCvv() {
        return cvv;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public void setValidade(LocalDate validade) {
        this.validade = validade;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }
}
