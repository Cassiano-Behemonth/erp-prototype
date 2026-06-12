package com.erp.prototype.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String telefone;
    private String email;
    private String veiculoModelo;
    private String veiculoPlaca;
    private String veiculoAno;

    public Cliente() {
    }

    public Cliente(String nome, String telefone, String email, String veiculoModelo, String veiculoPlaca, String veiculoAno) {
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
        this.veiculoModelo = veiculoModelo;
        this.veiculoPlaca = veiculoPlaca;
        this.veiculoAno = veiculoAno;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVeiculoModelo() {
        return veiculoModelo;
    }

    public void setVeiculoModelo(String veiculoModelo) {
        this.veiculoModelo = veiculoModelo;
    }

    public String getVeiculoPlaca() {
        return veiculoPlaca;
    }

    public void setVeiculoPlaca(String veiculoPlaca) {
        this.veiculoPlaca = veiculoPlaca;
    }

    public String getVeiculoAno() {
        return veiculoAno;
    }

    public void setVeiculoAno(String veiculoAno) {
        this.veiculoAno = veiculoAno;
    }
}
