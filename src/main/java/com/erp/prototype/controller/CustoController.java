package com.erp.prototype.controller;

import com.erp.prototype.model.Custo;
import com.erp.prototype.model.CustoTipo;
import com.erp.prototype.service.FinanceiroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.time.LocalDate;

@Controller
@RequestMapping("/custos")
public class CustoController {

    private final FinanceiroService financeiroService;

    @Autowired
    public CustoController(FinanceiroService financeiroService) {
        this.financeiroService = financeiroService;
    }

    @GetMapping
    public String listar(Model model) {
        java.util.List<Custo> custos = financeiroService.listarTodosCustos();
        double totalCustos = custos.stream().mapToDouble(Custo::getValor).sum();
        model.addAttribute("custos", custos);
        model.addAttribute("totalCustos", totalCustos);
        model.addAttribute("tipos", CustoTipo.values());
        return "custos/list";
    }

    @GetMapping("/novo")
    public String novoForm(Model model) {
        Custo custo = new Custo();
        custo.setData(LocalDate.now());
        model.addAttribute("custo", custo);
        model.addAttribute("tipos", CustoTipo.values());
        return "custos/form";
    }

    @PostMapping
    public String salvar(@ModelAttribute Custo custo) {
        if (custo.getData() == null) {
            custo.setData(LocalDate.now());
        }
        financeiroService.salvarCusto(custo);
        return "redirect:/custos";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        financeiroService.excluirCusto(id);
        return "redirect:/custos";
    }
}
