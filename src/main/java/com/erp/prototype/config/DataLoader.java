package com.erp.prototype.config;

import com.erp.prototype.model.*;
import com.erp.prototype.repository.*;
import com.erp.prototype.service.OrdemServicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataLoader implements CommandLineRunner {

    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final CustoRepository custoRepository;
    private final OrdemServicoService osService;
    private final OrdemServicoRepository osRepository;

    @Autowired
    public DataLoader(ClienteRepository clienteRepository,
                      ProdutoRepository produtoRepository,
                      CustoRepository custoRepository,
                      OrdemServicoService osService,
                      OrdemServicoRepository osRepository) {
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
        this.custoRepository = custoRepository;
        this.osService = osService;
        this.osRepository = osRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Apenas inicializa se o banco estiver vazio
        if (clienteRepository.count() > 0) {
            return;
        }

        // 1. Cadastrar Clientes & Veículos
        Cliente c1 = new Cliente("Ricardo Senna", "(11) 98888-7777", "ricardo.senna@email.com", "Audi A3 Sedan Cinza", "SEN-2A23", "2022");
        Cliente c2 = new Cliente("Bruna Albuquerque", "(21) 97777-6666", "bruna.alb@email.com", "Porsche Macan Preto", "BRU-9P11", "2023");
        Cliente c3 = new Cliente("Marcos Oliveira", "(31) 96666-5555", "marcos.oliver@email.com", "Vectra Challenge Azul", "CHL-0010", "2002");
        
        clienteRepository.save(c1);
        clienteRepository.save(c2);
        clienteRepository.save(c3);

        // 2. Cadastrar Insumos & Peças
        Produto p1 = new Produto("Película Carbono G20 (Bobina 10m)", "Película de insulfilm de alta performance térmica", 5, 250.0, 750.0, 2);
        Produto p2 = new Produto("Chapa MDF Naval 15mm (2.44x1.83)", "Material resistente a umidade para marcenaria acústica", 12, 110.0, 290.0, 4);
        Produto p3 = new Produto("Tinta Automotiva Orange Candy (L)", "Tinta especial de alta cobertura e brilho", 8, 180.0, 480.0, 3);
        Produto p4 = new Produto("Kit Alto-falantes Coaxial JBL 6\"", "Par de falantes de alta fidelidade", 15, 140.0, 320.0, 5);
        Produto p5 = new Produto("Rolo Wrap Vinyl Preto Fosco (25m)", "Envelopamento automotivo premium", 3, 600.0, 2200.0, 1);

        produtoRepository.save(p1);
        produtoRepository.save(p2);
        produtoRepository.save(p3);
        produtoRepository.save(p4);
        produtoRepository.save(p5);

        // 3. Cadastrar Custos Operacionais Manuais
        Custo custoAluguel = new Custo("Aluguel do Galpão (Junho)", 3200.0, LocalDate.now().minusDays(5), CustoTipo.OPERACIONAL);
        Custo custoLuz = new Custo("Conta de Energia Trifásica", 850.0, LocalDate.now().minusDays(2), CustoTipo.OPERACIONAL);
        Custo custoFerramentas = new Custo("Lixas e Discos de Diamantação", 350.0, LocalDate.now().minusDays(10), CustoTipo.OUTROS);

        custoRepository.save(custoAluguel);
        custoRepository.save(custoLuz);
        custoRepository.save(custoFerramentas);

        // 4. Nenhuma Ordem de Serviço inicial é criada para iniciar com o banco de OS zerado.
    }

    private Cliente obterOuCriarCliente(String nome, String telefone, String email, String veiculoModelo, String veiculoPlaca, String veiculoAno) {
        return clienteRepository.findAll().stream()
                .filter(c -> c.getNome().equalsIgnoreCase(nome))
                .findFirst()
                .orElseGet(() -> clienteRepository.save(new Cliente(nome, telefone, email, veiculoModelo, veiculoPlaca, veiculoAno)));
    }

    private Produto obterOuCriarProduto(String nome, String descricao, Integer quantidadeEstoque, Double precoCusto, Double precoVenda, Integer alertaEstoqueMinimo) {
        return produtoRepository.findAll().stream()
                .filter(p -> p.getNome().equalsIgnoreCase(nome))
                .findFirst()
                .orElseGet(() -> produtoRepository.save(new Produto(nome, descricao, quantidadeEstoque, precoCusto, precoVenda, alertaEstoqueMinimo)));
    }

}
