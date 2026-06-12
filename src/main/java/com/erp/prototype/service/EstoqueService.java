package com.erp.prototype.service;

import com.erp.prototype.model.Custo;
import com.erp.prototype.model.CustoTipo;
import com.erp.prototype.model.Produto;
import com.erp.prototype.repository.CustoRepository;
import com.erp.prototype.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class EstoqueService {

    private final ProdutoRepository produtoRepository;
    private final CustoRepository custoRepository;

    @Autowired
    public EstoqueService(ProdutoRepository produtoRepository, CustoRepository custoRepository) {
        this.produtoRepository = produtoRepository;
        this.custoRepository = custoRepository;
    }

    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));
    }

    @Transactional
    public Produto salvar(Produto produto) {
        // Se for um produto novo com estoque inicial maior que zero, gera custo correspondente
        boolean isNovo = produto.getId() == null;
        Integer estoqueInicial = produto.getQuantidadeEstoque();
        
        Produto produtoSalvo = produtoRepository.save(produto);
        
        if (isNovo && estoqueInicial != null && estoqueInicial > 0) {
            registrarCustoDeEstoque(produtoSalvo, estoqueInicial);
        }
        return produtoSalvo;
    }

    @Transactional
    public void registrarEntrada(Long produtoId, Integer quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade de entrada deve ser maior que zero.");
        }
        Produto produto = buscarPorId(produtoId);
        produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() + quantidade);
        produtoRepository.save(produto);

        registrarCustoDeEstoque(produto, quantidade);
    }

    private void registrarCustoDeEstoque(Produto produto, Integer quantidade) {
        double custoTotal = quantidade * produto.getPrecoCusto();
        Custo custo = new Custo(
                "Compra de Insumo/Peça: " + produto.getNome() + " (Qtd: " + quantidade + ")",
                custoTotal,
                LocalDate.now(),
                CustoTipo.INSUMO_ESTOQUE
        );
        custoRepository.save(custo);
    }

    @Transactional
    public void registrarSaidaManualmente(Long produtoId, Integer quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade de saída deve ser maior que zero.");
        }
        Produto produto = buscarPorId(produtoId);
        if (produto.getQuantidadeEstoque() < quantidade) {
            throw new IllegalArgumentException("Estoque insuficiente para a saída.");
        }
        produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - quantidade);
        produtoRepository.save(produto);
    }
}
