package com.erp.prototype.model;

public enum CustoTipo {
    OPERACIONAL("Operacional (Aluguel, Luz, Salários, etc.)"),
    INSUMO_ESTOQUE("Aquisição de Insumo/Estoque"),
    OUTROS("Outras Despesas");

    private final String descricao;

    CustoTipo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
