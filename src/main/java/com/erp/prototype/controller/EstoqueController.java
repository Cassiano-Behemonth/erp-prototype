package com.erp.prototype.controller;

import com.erp.prototype.model.Produto;
import com.erp.prototype.service.EstoqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/estoque")
public class EstoqueController {

    private final EstoqueService estoqueService;

    @Autowired
    public EstoqueController(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("produtos", estoqueService.listarTodos());
        return "estoque/list";
    }

    @GetMapping("/novo")
    public String novoForm(Model model) {
        model.addAttribute("produto", new Produto());
        return "estoque/form";
    }

    @PostMapping
    public String salvar(@ModelAttribute Produto produto, RedirectAttributes redirectAttributes) {
        try {
            estoqueService.salvar(produto);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Produto salvo com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao salvar produto: " + e.getMessage());
        }
        return "redirect:/estoque";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Long id, Model model) {
        Produto produto = estoqueService.buscarPorId(id);
        model.addAttribute("produto", produto);
        return "estoque/form";
    }

    @PostMapping("/entrada")
    public String registrarEntrada(@RequestParam Long produtoId, @RequestParam Integer quantidade, RedirectAttributes redirectAttributes) {
        try {
            estoqueService.registrarEntrada(produtoId, quantidade);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Entrada de estoque registrada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao registrar entrada: " + e.getMessage());
        }
        return "redirect:/estoque";
    }
}
