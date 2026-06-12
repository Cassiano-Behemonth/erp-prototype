package com.erp.prototype;

import com.erp.prototype.model.*;
import com.erp.prototype.repository.*;
import com.erp.prototype.service.EstoqueService;
import com.erp.prototype.service.OrdemServicoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PrototypeApplicationTests {

	@Autowired
	private EstoqueService estoqueService;

	@Autowired
	private OrdemServicoService osService;

	@Autowired
	private ProdutoRepository produtoRepository;

	@Autowired
	private CustoRepository custoRepository;

	@Autowired
	private ClienteRepository clienteRepository;

	@Autowired
	private OrdemServicoRepository osRepository;

	private Cliente cliente;
	private Produto produto;

	@BeforeEach
	void setUp() {
		// Limpa tabelas na ordem correta para evitar erros de FK
		osRepository.deleteAll();
		custoRepository.deleteAll();
		produtoRepository.deleteAll();
		clienteRepository.deleteAll();

		// Cria entidades básicas
		cliente = new Cliente("Test Client", "12345678", "test@email.com", "Gol 1.0", "ABC-1234", "2015");
		cliente = clienteRepository.save(cliente);

		produto = new Produto("Película G20", "Insumo teste", 10, 50.0, 150.0, 2);
		produto = produtoRepository.save(produto);
	}

	@Test
	void contextLoads() {
		assertNotNull(cliente.getId());
		assertNotNull(produto.getId());
	}

	@Test
	void testRegistrarEntradaDeEstoqueGeraCusto() {
		long countAntes = custoRepository.count();

		// Registra entrada de 5 itens
		estoqueService.registrarEntrada(produto.getId(), 5);

		// Verifica se quantidade de estoque foi incrementada
		Produto produtoAtualizado = produtoRepository.findById(produto.getId()).orElseThrow();
		assertEquals(15, produtoAtualizado.getQuantidadeEstoque());

		// Verifica se gerou custo no banco
		long countDepois = custoRepository.count();
		assertEquals(countAntes + 1, countDepois);

		// Valida o custo gerado
		Custo custoGerado = custoRepository.findAll().stream()
				.filter(c -> c.getTipo() == CustoTipo.INSUMO_ESTOQUE)
				.findFirst()
				.orElseThrow();
		
		assertEquals(250.0, custoGerado.getValor()); // 5 * R$50.0
		assertTrue(custoGerado.getDescricao().contains(produto.getNome()));
	}

	@Test
	void testConcluirOrdemServicoBaixaEstoque() {
		// Cria OS
		OrdemServico os = new OrdemServico(cliente, ServicoCategoria.ENVELOPAMENTO_E_INSULFILM, "Serviço teste", 200.0, OrdemServicoStatus.EM_ANDAMENTO, LocalDateTime.now());
		OSItem item = new OSItem(produto, 3, produto.getPrecoVenda()); // usa 3 itens do estoque
		os.addOSItem(item);

		OrdemServico osCriada = osService.criar(os);
		assertEquals(OrdemServicoStatus.EM_ANDAMENTO, osCriada.getStatus());
		
		// Conclui a ordem
		osService.concluirOrdem(osCriada.getId());

		// Verifica se o status mudou e se o estoque diminuiu de 10 para 7
		OrdemServico osConcluida = osService.buscarPorId(osCriada.getId());
		assertEquals(OrdemServicoStatus.CONCLUIDO, osConcluida.getStatus());

		Produto produtoAtualizado = produtoRepository.findById(produto.getId()).orElseThrow();
		assertEquals(7, produtoAtualizado.getQuantidadeEstoque());
	}

	@Test
	void testConcluirOrdemServicoComEstoqueInsuficienteFalha() {
		// Cria OS solicitando 12 itens (temos apenas 10 no estoque)
		OrdemServico os = new OrdemServico(cliente, ServicoCategoria.ENVELOPAMENTO_E_INSULFILM, "Serviço teste", 200.0, OrdemServicoStatus.EM_ANDAMENTO, LocalDateTime.now());
		OSItem item = new OSItem(produto, 12, produto.getPrecoVenda());
		os.addOSItem(item);

		OrdemServico osCriada = osService.criar(os);

		// Deve lançar exceção ao tentar concluir
		assertThrows(IllegalArgumentException.class, () -> {
			osService.concluirOrdem(osCriada.getId());
		});

		// Verifica que o estoque continuou sendo 10
		Produto produtoAtualizado = produtoRepository.findById(produto.getId()).orElseThrow();
		assertEquals(10, produtoAtualizado.getQuantidadeEstoque());
	}
}
