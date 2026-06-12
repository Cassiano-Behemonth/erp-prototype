package com.erp.prototype.controller;

import com.erp.prototype.model.Produto;
import com.erp.prototype.model.ServicoCategoria;
import com.erp.prototype.service.FinanceiroService;
import com.erp.prototype.service.EstoqueService;
import com.erp.prototype.service.OrdemServicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    private final FinanceiroService financeiroService;
    private final EstoqueService estoqueService;
    private final OrdemServicoService osService;

    @Autowired
    public DashboardController(FinanceiroService financeiroService, EstoqueService estoqueService, OrdemServicoService osService) {
        this.financeiroService = financeiroService;
        this.estoqueService = estoqueService;
        this.osService = osService;
    }

    @GetMapping({"/", "/dashboard"})
    public String index(Model model) {
        // KPIs
        Double receita = financeiroService.obterReceitaTotal();
        Double custos = financeiroService.obterCustosTotais();
        Double lucro = financeiroService.obterLucroLiquido();
        
        model.addAttribute("receitaTotal", receita);
        model.addAttribute("custosTotais", custos);
        model.addAttribute("lucroLiquido", lucro);
        
        long totalOrdens = osService.listarTodas().size();
        long ordensConcluidas = osService.listarTodas().stream()
                .filter(os -> os.getStatus().name().equals("CONCLUIDO"))
                .count();
        
        model.addAttribute("totalOrdens", totalOrdens);
        model.addAttribute("ordensConcluidas", ordensConcluidas);

        // Alertas de estoque baixo
        List<Produto> alertasEstoque = estoqueService.listarTodos().stream()
                .filter(p -> p.getQuantidadeEstoque() <= p.getAlertaEstoqueMinimo())
                .collect(Collectors.toList());
        model.addAttribute("alertasEstoque", alertasEstoque);

        // Dados do gráfico de faturamento por categoria de serviço
        Map<ServicoCategoria, Double> receitaPorCat = financeiroService.obterReceitaPorCategoria();
        List<String> categoriasLabels = new ArrayList<>();
        List<Double> categoriasValores = new ArrayList<>();
        
        receitaPorCat.forEach((k, v) -> {
            categoriasLabels.add(k.getDescricao());
            categoriasValores.add(v);
        });
        
        model.addAttribute("categoriasLabels", categoriasLabels);
        model.addAttribute("categoriasValores", categoriasValores);
        
        boolean hasReceitaGrafico = categoriasValores.stream().anyMatch(v -> v != null && v > 0);
        model.addAttribute("hasReceitaGrafico", hasReceitaGrafico);

        // Dados do gráfico de custos por tipo
        Map<String, Double> custosPorTipo = financeiroService.obterCustosPorTipo();
        List<String> custosLabels = new ArrayList<>(custosPorTipo.keySet());
        List<Double> custosValores = new ArrayList<>(custosPorTipo.values());

        model.addAttribute("custosLabels", custosLabels);
        model.addAttribute("custosValores", custosValores);
        
        boolean hasCustosGrafico = custosValores.stream().anyMatch(v -> v != null && v > 0);
        model.addAttribute("hasCustosGrafico", hasCustosGrafico);

        return "dashboard";
    }
}
