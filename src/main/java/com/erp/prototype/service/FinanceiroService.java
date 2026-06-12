package com.erp.prototype.service;

import com.erp.prototype.model.Custo;
import com.erp.prototype.model.OrdemServico;
import com.erp.prototype.model.OrdemServicoStatus;
import com.erp.prototype.model.ServicoCategoria;
import com.erp.prototype.repository.CustoRepository;
import com.erp.prototype.repository.OrdemServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FinanceiroService {

    private final OrdemServicoRepository osRepository;
    private final CustoRepository custoRepository;

    @Autowired
    public FinanceiroService(OrdemServicoRepository osRepository, CustoRepository custoRepository) {
        this.osRepository = osRepository;
        this.custoRepository = custoRepository;
    }

    public List<Custo> listarTodosCustos() {
        return custoRepository.findAll();
    }

    public Custo salvarCusto(Custo custo) {
        return custoRepository.save(custo);
    }

    public void excluirCusto(Long id) {
        custoRepository.deleteById(id);
    }

    public Double obterReceitaTotal() {
        return osRepository.findAll().stream()
                .filter(os -> os.getStatus() == OrdemServicoStatus.CONCLUIDO)
                .mapToDouble(OrdemServico::getValorTotal)
                .sum();
    }

    public Double obterCustosTotais() {
        return custoRepository.findAll().stream()
                .mapToDouble(Custo::getValor)
                .sum();
    }

    public Double obterLucroLiquido() {
        return obterReceitaTotal() - obterCustosTotais();
    }

    public Map<ServicoCategoria, Double> obterReceitaPorCategoria() {
        Map<ServicoCategoria, Double> receitaPorCategoria = new HashMap<>();
        
        // Inicializar todas as categorias com zero
        for (ServicoCategoria cat : ServicoCategoria.values()) {
            receitaPorCategoria.put(cat, 0.0);
        }

        // Somar receitas
        osRepository.findAll().stream()
                .filter(os -> os.getStatus() == OrdemServicoStatus.CONCLUIDO)
                .forEach(os -> {
                    Double valorAtual = receitaPorCategoria.getOrDefault(os.getServicoCategoria(), 0.0);
                    receitaPorCategoria.put(os.getServicoCategoria(), valorAtual + os.getValorTotal());
                });

        return receitaPorCategoria;
    }

    public Map<String, Double> obterCustosPorTipo() {
        Map<String, Double> custosPorTipo = new HashMap<>();
        custoRepository.findAll().forEach(custo -> {
            String tipoStr = custo.getTipo().getDescricao();
            Double valorAtual = custosPorTipo.getOrDefault(tipoStr, 0.0);
            custosPorTipo.put(tipoStr, valorAtual + custo.getValor());
        });
        return custosPorTipo;
    }
}
