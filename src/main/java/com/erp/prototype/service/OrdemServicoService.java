package com.erp.prototype.service;

import com.erp.prototype.model.OrdemServico;
import com.erp.prototype.model.OrdemServicoStatus;
import com.erp.prototype.model.OSItem;
import com.erp.prototype.model.Produto;
import com.erp.prototype.repository.OrdemServicoRepository;
import com.erp.prototype.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrdemServicoService {

    private final OrdemServicoRepository osRepository;
    private final ProdutoRepository produtoRepository;
    private final EmailService emailService;

    @Autowired
    public OrdemServicoService(OrdemServicoRepository osRepository,
                               ProdutoRepository produtoRepository,
                               EmailService emailService) {
        this.osRepository = osRepository;
        this.produtoRepository = produtoRepository;
        this.emailService = emailService;
    }

    public List<OrdemServico> listarTodas() {
        return osRepository.findAll();
    }

    public OrdemServico buscarPorId(Long id) {
        return osRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Ordem de serviço não encontrada."));
    }

    @Transactional
    public OrdemServico criar(OrdemServico os) {
        os.setStatus(OrdemServicoStatus.EM_ANDAMENTO);
        os.setData(LocalDateTime.now());
        
        // Recalcular valor total baseado nos itens se não especificado
        calcularValorTotal(os);
        
        // Salva os itens atrelados
        for (OSItem item : os.getItensEstoqueUtilizados()) {
            item.setOrdemServico(os);
        }
        
        OrdemServico osSalva = osRepository.save(os);

        // Enviar email de abertura da OS (erro não bloqueia o fluxo)
        try {
            emailService.sendEmailAbertura(osSalva);
        } catch (Exception e) {
            System.err.println("[OS] Falha ao enviar email de abertura: " + e.getMessage());
        }

        return osSalva;
    }

    /**
     * Atualiza uma OS já existente (ao adicionar/remover itens) sem disparar email.
     */
    @Transactional
    public OrdemServico atualizarItens(OrdemServico os) {
        for (OSItem item : os.getItensEstoqueUtilizados()) {
            item.setOrdemServico(os);
        }
        return osRepository.save(os);
    }

    @Transactional
    public void concluirOrdem(Long id) {
        OrdemServico os = buscarPorId(id);
        if (os.getStatus() == OrdemServicoStatus.CONCLUIDO) {
            throw new IllegalStateException("Esta ordem de serviço já está concluída.");
        }
        if (os.getStatus() == OrdemServicoStatus.CANCELADO) {
            throw new IllegalStateException("Uma ordem de serviço cancelada não pode ser concluída.");
        }

        // Dar baixa no estoque
        for (OSItem item : os.getItensEstoqueUtilizados()) {
            Produto produto = item.getProduto();
            if (produto.getQuantidadeEstoque() < item.getQuantidade()) {
                throw new IllegalArgumentException("Estoque insuficiente para o produto: " + produto.getNome() + 
                        ". Disponível: " + produto.getQuantidadeEstoque() + ", Solicitado: " + item.getQuantidade());
            }
            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - item.getQuantidade());
            produtoRepository.save(produto);
        }

        os.setStatus(OrdemServicoStatus.CONCLUIDO);
        osRepository.save(os);

        // Enviar email de conclusão da OS (erro não bloqueia o fluxo)
        try {
            emailService.sendEmailConclusao(os);
        } catch (Exception e) {
            System.err.println("[OS] Falha ao enviar email de conclusão: " + e.getMessage());
        }
    }

    @Transactional
    public void cancelarOrdem(Long id) {
        OrdemServico os = buscarPorId(id);
        if (os.getStatus() == OrdemServicoStatus.CONCLUIDO) {
            throw new IllegalStateException("Não é possível cancelar uma ordem de serviço concluída.");
        }
        os.setStatus(OrdemServicoStatus.CANCELADO);
        osRepository.save(os);
    }

    private void calcularValorTotal(OrdemServico os) {
        double totalItens = 0;
        for (OSItem item : os.getItensEstoqueUtilizados()) {
            if (item.getPrecoUnitario() == null) {
                item.setPrecoUnitario(item.getProduto().getPrecoVenda());
            }
            totalItens += item.getQuantidade() * item.getPrecoUnitario();
        }
        if (os.getValorTotal() == null || os.getValorTotal() == 0) {
            os.setValorTotal(totalItens);
        }
    }

    @Transactional
    public void resetarTodas() {
        osRepository.deleteAll();
    }
}
