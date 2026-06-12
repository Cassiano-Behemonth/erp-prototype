package com.erp.prototype.model;

public enum ServicoCategoria {
    MARCENARIA_AUTOMOTIVA("Especialização em Marcenaria Automotiva"),
    SOM_E_ACESSORIOS("Instalação de Som e Acessórios"),
    PERSONALIZACAO("Personalização em Geral"),
    REFORMA_DE_RODA("Reforma de Roda"),
    CENTRO_AUTOMOTIVO("Centro Automotivo"),
    ENVELOPAMENTO_E_INSULFILM("Envelopamento e Insulfilm"),
    PINTURA_AUTOMOTIVA("Pintura Automotiva");

    private final String descricao;

    ServicoCategoria(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
