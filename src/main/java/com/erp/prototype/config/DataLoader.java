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

        // 4. Cadastrar Custos de Aquisição Automáticos (Simulando compras passadas de insumos)
        custoRepository.save(new Custo("Compra de Insumo/Peça: Película Carbono G20 (Bobina 10m) (Qtd: 5)", 1250.0, LocalDate.now().minusDays(20), CustoTipo.INSUMO_ESTOQUE));
        custoRepository.save(new Custo("Compra de Insumo/Peça: Chapa MDF Naval 15mm (Qtd: 12)", 1320.0, LocalDate.now().minusDays(18), CustoTipo.INSUMO_ESTOQUE));
        custoRepository.save(new Custo("Compra de Insumo/Peça: Rolo Wrap Vinyl Preto Fosco (Qtd: 3)", 1800.0, LocalDate.now().minusDays(15), CustoTipo.INSUMO_ESTOQUE));

        // 5. Nenhuma Ordem de Serviço inicial é criada para iniciar com o banco de OS zerado.
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

    @Transactional
    public void carregarOrdensMock() {
        // Se já existirem ordens, não adiciona para evitar duplicados
        if (osRepository.count() > 0) {
            return;
        }

        Cliente c1 = obterOuCriarCliente("Ricardo Senna", "(11) 98888-7777", "ricardo.senna@email.com", "Audi A3 Sedan Cinza", "SEN-2A23", "2022");
        Cliente c2 = obterOuCriarCliente("Bruna Albuquerque", "(21) 97777-6666", "bruna.alb@email.com", "Porsche Macan Preto", "BRU-9P11", "2023");
        Cliente c3 = obterOuCriarCliente("Marcos Oliveira", "(31) 96666-5555", "marcos.oliver@email.com", "Vectra Challenge Azul", "CHL-0010", "2002");

        Produto p1 = obterOuCriarProduto("Película Carbono G20 (Bobina 10m)", "Película de insulfilm de alta performance térmica", 5, 250.0, 750.0, 2);
        Produto p2 = obterOuCriarProduto("Chapa MDF Naval 15mm (2.44x1.83)", "Material resistente a umidade para marcenaria acústica", 12, 110.0, 290.0, 4);
        Produto p3 = obterOuCriarProduto("Tinta Automotiva Orange Candy (L)", "Tinta especial de alta cobertura e brilho", 8, 180.0, 480.0, 3);
        Produto p4 = obterOuCriarProduto("Kit Alto-falantes Coaxial JBL 6\"", "Par de falantes de alta fidelidade", 15, 140.0, 320.0, 5);
        Produto p5 = obterOuCriarProduto("Rolo Wrap Vinyl Preto Fosco (25m)", "Envelopamento automotivo premium", 3, 600.0, 2200.0, 1);

        // Resetar estoques para quantidades iniciais seguras antes de simular
        p1.setQuantidadeEstoque(5);
        p2.setQuantidadeEstoque(12);
        p3.setQuantidadeEstoque(8);
        p4.setQuantidadeEstoque(15);
        p5.setQuantidadeEstoque(3);
        produtoRepository.saveAll(List.of(p1, p2, p3, p4, p5));

        // Ordem 1: Envelopamento do Porsche de Bruna
        OrdemServico os1 = new OrdemServico(c2, ServicoCategoria.ENVELOPAMENTO_E_INSULFILM, 
                "Envelopamento completo em vinil Preto Fosco e aplicação de película solar G20 nos vidros.", 
                3500.0, OrdemServicoStatus.EM_ANDAMENTO, LocalDateTime.now().minusDays(6));
        os1.addOSItem(new OSItem(p5, 1, p5.getPrecoVenda()));
        os1.addOSItem(new OSItem(p1, 1, p1.getPrecoVenda()));
        osService.criar(os1);
        osService.concluirOrdem(os1.getId());

        // Ordem 2: Marcenaria Acústica + Som no Vectra de Marcos
        OrdemServico os2 = new OrdemServico(c3, ServicoCategoria.MARCENARIA_AUTOMOTIVA, 
                "Fabricação de caixa selada personalizada em MDF naval no porta-malas e instalação de som coaxial.", 
                1500.0, OrdemServicoStatus.EM_ANDAMENTO, LocalDateTime.now().minusDays(3));
        os2.addOSItem(new OSItem(p2, 2, p2.getPrecoVenda()));
        os2.addOSItem(new OSItem(p4, 2, p4.getPrecoVenda()));
        osService.criar(os2);
        osService.concluirOrdem(os2.getId());

        // Ordem 3: Reforma de Rodas no A3 de Ricardo (Em Andamento)
        OrdemServico os3 = new OrdemServico(c1, ServicoCategoria.REFORMA_DE_RODA, 
                "Pintura das pinças de freio em Orange Candy e diamantação das 4 rodas de liga leve.", 
                900.0, OrdemServicoStatus.EM_ANDAMENTO, LocalDateTime.now().minusDays(1));
        os3.addOSItem(new OSItem(p3, 1, p3.getPrecoVenda()));
        osService.criar(os3);
    }
}
