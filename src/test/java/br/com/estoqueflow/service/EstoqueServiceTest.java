package br.com.estoqueflow.service;

import br.com.estoqueflow.dao.MovimentacaoDAO;
import br.com.estoqueflow.dao.ProdutoDAO;
import br.com.estoqueflow.database.DatabaseManager;
import br.com.estoqueflow.model.Produto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

class EstoqueServiceTest {
    private static String previousUserHome;
    private static Path testHome;

    private final ProdutoDAO produtos = new ProdutoDAO();
    private final MovimentacaoDAO movimentacoes = new MovimentacaoDAO();
    private final EstoqueService estoque = new EstoqueService();

    @BeforeAll
    static void prepararBancoIsolado() throws Exception {
        previousUserHome = System.getProperty("user.home");
        testHome = Files.createTempDirectory("estoqueflow-test-");
        System.setProperty("user.home", testHome.toString());
        DatabaseManager.inicializar();
    }

    @AfterAll
    static void removerBancoIsolado() throws Exception {
        if (previousUserHome == null) {
            System.clearProperty("user.home");
        } else {
            System.setProperty("user.home", previousUserHome);
        }

        if (testHome != null && Files.exists(testHome)) {
            try (var paths = Files.walk(testHome)) {
                for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                    Files.deleteIfExists(path);
                }
            }
        }
    }

    @BeforeEach
    void limparDados() throws Exception {
        try (Connection c = DatabaseManager.conectar();
             Statement s = c.createStatement()) {
            s.executeUpdate("DELETE FROM movimentacoes");
            s.executeUpdate("DELETE FROM produtos");
        }
    }

    @Test
    void registraEntradaESaidaEAtualizaHistorico() throws Exception {
        long id = cadastrar("SKU-001", 0, true);

        estoque.registrarEntrada(id, 20, "Compra");
        estoque.registrarSaida(id, 5, "Venda");

        assertEquals(15, produtos.buscarPorCodigo("SKU-001")
                .orElseThrow().getQuantidade());
        assertEquals(2, movimentacoes.listarPorProduto(id).size());
    }

    @Test
    void rejeitaSaidaMaiorQueSaldoSemAlterarEstoque() throws Exception {
        long id = cadastrar("SKU-002", 0, true);
        estoque.registrarEntrada(id, 3, "Reposição");

        assertThrows(IllegalArgumentException.class,
                () -> estoque.registrarSaida(id, 4, "Saída inválida"));

        assertEquals(3, produtos.buscarPorCodigo("SKU-002")
                .orElseThrow().getQuantidade());
        assertEquals(1, movimentacoes.listarPorProduto(id).size());
    }

    @Test
    void rejeitaQuantidadeZeroOuNegativa() throws Exception {
        long id = cadastrar("SKU-003", 0, true);

        assertThrows(IllegalArgumentException.class,
                () -> estoque.registrarEntrada(id, 0, ""));
        assertThrows(IllegalArgumentException.class,
                () -> estoque.registrarSaida(id, -2, ""));

        assertEquals(0, movimentacoes.listarPorProduto(id).size());
    }

    @Test
    void impedeMovimentacaoDeProdutoInativo() throws Exception {
        long id = cadastrar("SKU-004", 0, true);
        produtos.alterarStatus(id, false);

        assertThrows(IllegalStateException.class,
                () -> estoque.registrarEntrada(id, 10, "Teste"));

        assertEquals(0, movimentacoes.listarPorProduto(id).size());
    }

    @Test
    void impedeEstouroNumericoSemPerderSaldo() throws Exception {
        long id = cadastrar("SKU-005", Integer.MAX_VALUE, true);

        assertThrows(ArithmeticException.class,
                () -> estoque.registrarEntrada(id, 1, "Estouro"));

        assertEquals(Integer.MAX_VALUE, produtos.buscarPorCodigo("SKU-005")
                .orElseThrow().getQuantidade());
        assertEquals(0, movimentacoes.listarPorProduto(id).size());
    }

    private long cadastrar(String codigo, int quantidade, boolean ativo)
            throws Exception {
        Produto produto = new Produto(
                null, codigo, "Produto " + codigo, "Geral",
                new BigDecimal("12.50"), quantidade, 2, ativo
        );
        return produtos.inserir(produto);
    }
}
