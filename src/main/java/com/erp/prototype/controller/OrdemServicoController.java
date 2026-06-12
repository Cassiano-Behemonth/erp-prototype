package com.erp.prototype.controller;

import com.erp.prototype.model.Cliente;
import com.erp.prototype.model.OrdemServico;
import com.erp.prototype.model.OSItem;
import com.erp.prototype.model.Produto;
import com.erp.prototype.model.ServicoCategoria;
import com.erp.prototype.repository.ClienteRepository;
import com.erp.prototype.repository.ProdutoRepository;
import com.erp.prototype.service.OrdemServicoService;
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
import java.time.LocalDateTime;

@Controller
@RequestMapping("/ordens")
public class OrdemServicoController {

    private final OrdemServicoService osService;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;

    @Autowired
    private com.erp.prototype.config.DataLoader dataLoader;

    @Autowired
    public OrdemServicoController(OrdemServicoService osService, ClienteRepository clienteRepository, ProdutoRepository produtoRepository) {
        this.osService = osService;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("ordens", osService.listarTodas());
        return "ordens/list";
    }

    @GetMapping("/novo")
    public String novoForm(Model model) {
        model.addAttribute("ordemServico", new OrdemServico());
        model.addAttribute("clientes", clienteRepository.findAll());
        model.addAttribute("categorias", ServicoCategoria.values());
        return "ordens/form";
    }

    @PostMapping
    public String salvar(@ModelAttribute OrdemServico os, RedirectAttributes redirectAttributes) {
        try {
            os.setData(LocalDateTime.now());
            OrdemServico salva = osService.criar(os);
            return "redirect:/ordens/" + salva.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao criar Ordem de Serviço: " + e.getMessage());
            return "redirect:/ordens/novo";
        }
    }

    @GetMapping("/{id}")
    public String visualizar(@PathVariable Long id, Model model) {
        OrdemServico os = osService.buscarPorId(id);
        model.addAttribute("ordem", os);
        model.addAttribute("produtos", produtoRepository.findAll());
        return "ordens/view";
    }

    @PostMapping("/{id}/itens")
    public String adicionarItem(@PathVariable Long id, @RequestParam Long produtoId, @RequestParam Integer quantidade, RedirectAttributes redirectAttributes) {
        try {
            OrdemServico os = osService.buscarPorId(id);
            Produto produto = produtoRepository.findById(produtoId)
                    .orElseThrow(() -> new IllegalArgumentException("Produto inválido"));
            
            OSItem item = new OSItem(produto, quantidade, produto.getPrecoVenda());
            os.addOSItem(item);
            
            // Recalcular valor total da OS
            double novoTotal = os.getValorTotal() + (item.getQuantidade() * item.getPrecoUnitario());
            os.setValorTotal(novoTotal);
            
            osService.atualizarItens(os); // Atualiza no banco sem disparar email
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Item adicionado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao adicionar item: " + e.getMessage());
        }
        return "redirect:/ordens/" + id;
    }

    @PostMapping("/{id}/itens/{itemId}/excluir")
    public String removerItem(@PathVariable Long id, @PathVariable Long itemId, RedirectAttributes redirectAttributes) {
        try {
            OrdemServico os = osService.buscarPorId(id);
            if (os.getStatus() != com.erp.prototype.model.OrdemServicoStatus.EM_ANDAMENTO) {
                throw new IllegalStateException("Só é possível alterar itens de ordens em andamento.");
            }
            
            OSItem itemARemover = os.getItensEstoqueUtilizados().stream()
                    .filter(i -> i.getId().equals(itemId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Item não encontrado na ordem."));
            
            // Subtrair o valor do item do total da OS
            double valorRemovido = itemARemover.getQuantidade() * itemARemover.getPrecoUnitario();
            os.setValorTotal(os.getValorTotal() - valorRemovido);
            
            // Remove o item da lista
            os.getItensEstoqueUtilizados().remove(itemARemover);
            
            osService.atualizarItens(os); // Atualiza no banco sem disparar email
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Item removido com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao remover item: " + e.getMessage());
        }
        return "redirect:/ordens/" + id;
    }

    @PostMapping("/{id}/concluir")
    public String concluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            osService.concluirOrdem(id);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Ordem de Serviço concluída com sucesso! Estoque atualizado.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao concluir Ordem: " + e.getMessage());
        }
        return "redirect:/ordens/" + id;
    }

    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            osService.cancelarOrdem(id);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Ordem de Serviço cancelada.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao cancelar Ordem: " + e.getMessage());
        }
        return "redirect:/ordens/" + id;
    }
}
