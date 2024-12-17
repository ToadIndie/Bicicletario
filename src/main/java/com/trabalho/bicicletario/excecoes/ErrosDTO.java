package com.trabalho.bicicletario.excecoes;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErrosDTO {
    private String codigo;
    private String mensagem;

    public ErrosDTO(String codigo, String mensagem) {
        this.codigo = codigo;
        this.mensagem = mensagem;
    }
}
