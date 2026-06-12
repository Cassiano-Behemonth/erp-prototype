package com.erp.prototype.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ordens_servico")
public class OrdemServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Enumerated(EnumType.STRING)
    private ServicoCategoria servicoCategoria;

    private String descricaoServico;
    private Double valorTotal;

    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OSItem> itensEstoqueUtilizados = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private OrdemServicoStatus status;

    private LocalDateTime data;

    public OrdemServico() {
    }

    public OrdemServico(Cliente cliente, ServicoCategoria servicoCategoria, String descricaoServico, Double valorTotal, OrdemServicoStatus status, LocalDateTime data) {
        this.cliente = cliente;
        this.servicoCategoria = servicoCategoria;
        this.descricaoServico = descricaoServico;
        this.valorTotal = valorTotal;
        this.status = status;
        this.data = data;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public ServicoCategoria getServicoCategoria() {
        return servicoCategoria;
    }

    public void setServicoCategoria(ServicoCategoria servicoCategoria) {
        this.servicoCategoria = servicoCategoria;
    }

    public String getDescricaoServico() {
        return descricaoServico;
    }

    public void setDescricaoServico(String descricaoServico) {
        this.descricaoServico = descricaoServico;
    }

    public Double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public List<OSItem> getItensEstoqueUtilizados() {
        return itensEstoqueUtilizados;
    }

    public void setItensEstoqueUtilizados(List<OSItem> itensEstoqueUtilizados) {
        this.itensEstoqueUtilizados = itensEstoqueUtilizados;
    }

    public void addOSItem(OSItem item) {
        itensEstoqueUtilizados.add(item);
        item.setOrdemServico(this);
    }

    public OrdemServicoStatus getStatus() {
        return status;
    }

    public void setStatus(OrdemServicoStatus status) {
        this.status = status;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public double getItensEstoqueTotal() {
        if (itensEstoqueUtilizados == null) {
            return 0.0;
        }
        return itensEstoqueUtilizados.stream()
                .mapToDouble(i -> i.getQuantidade() * i.getPrecoUnitario())
                .sum();
    }

    public double getMaoDeObraValor() {
        double total = valorTotal != null ? valorTotal : 0.0;
        return total - getItensEstoqueTotal();
    }
}
