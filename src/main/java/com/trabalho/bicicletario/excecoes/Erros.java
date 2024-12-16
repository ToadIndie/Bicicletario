package com.trabalho.bicicletario.excecoes;

public enum Erros {
    COBRANCA_FEITA(200, "Cobrança solicitada"),
    DADOS_INVALIDOS(422, "Dados inválidos"),
    EMAIL_ENVIADO(200, "Externo solicitado"),
    EMAIL_INEXISTENTE(404, "E-mail não existe"),
    EMAIL_INVALIDO(422, "E-mail com formato inválido"),
    PROCESSAMENTO_CONCLUIDO(200, "Processamento concluído com sucesso"),
    COBRANCA_INCLUIDA(200, "Cobrança incluída"),
    COBRANCA(200, "Cobrança"),
    NAO_ENCONTRADO(404, "Não encontrado"),
    DADOS_ATUALIZADOS(200, "Dados atualizados");

    private final int codigo;
    private final String mensagem;

    Erros(int codigo, String mensagem) {
        this.codigo = codigo;
        this.mensagem = mensagem;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getMensagem() {
        return mensagem;
    }
}
