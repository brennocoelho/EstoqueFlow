package br.com.estoqueflow.controller;

import br.com.estoqueflow.dao.MovimentacaoDAO;
import br.com.estoqueflow.dao.ProdutoDAO;
import br.com.estoqueflow.model.Movimentacao;
import br.com.estoqueflow.model.Produto;
import br.com.estoqueflow.service.EstoqueService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Controller do EstoqueFlow.
 *
 * Intermedeia as ações da View, coordena os serviços
 * e os DAOs e fornece os dados necessários à interface.
 */
public class EstoqueController {

    private final ProdutoDAO produtoDAO;
    private final MovimentacaoDAO movimentacaoDAO;
    private final EstoqueService estoqueService;

    public EstoqueController() {
        this(new ProdutoDAO(), new MovimentacaoDAO(), new EstoqueService());
    }

    EstoqueController(
            ProdutoDAO produtoDAO,
            MovimentacaoDAO movimentacaoDAO,
            EstoqueService estoqueService
    ) {
        this.produtoDAO = produtoDAO;
        this.movimentacaoDAO = movimentacaoDAO;
        this.estoqueService = estoqueService;
    }

    public List<Produto> listarProdutos() throws SQLException {
        return produtoDAO.listarTodos();
    }

    public List<Produto> pesquisarProdutos(String termo)
            throws SQLException {
        return produtoDAO.pesquisar(termo);
    }

    public void salvarProduto(Produto produto) throws SQLException {
        if (produto.getId() == null) {
            produtoDAO.inserir(produto);
        } else {
            produtoDAO.atualizar(produto);
        }
    }

    public void alterarStatusProduto(long id, boolean ativo)
            throws SQLException {
        produtoDAO.alterarStatus(id, ativo);
    }

    public void registrarEntrada(
            long produtoId, int quantidade, String observacao
    ) throws SQLException {
        estoqueService.registrarEntrada(produtoId, quantidade, observacao);
    }

    public void registrarSaida(
            long produtoId, int quantidade, String observacao
    ) throws SQLException {
        estoqueService.registrarSaida(produtoId, quantidade, observacao);
    }

    /**
     * Representa uma movimentação acompanhada do nome do produto.
     */
    public record MovimentacaoDetalhe(
            Movimentacao movimentacao,
            String nomeProduto
    ) {}

    /**
     * Retorna o histórico geral, ordenado da movimentação mais recente
     * para a mais antiga.
     */
    public List<MovimentacaoDetalhe> listarHistoricoCompleto()
            throws SQLException {
        List<MovimentacaoDetalhe> historico = new ArrayList<>();

        for (Produto produto : produtoDAO.listarTodos()) {
            for (Movimentacao movimentacao :
                    movimentacaoDAO.listarPorProduto(produto.getId())) {
                historico.add(new MovimentacaoDetalhe(
                        movimentacao, produto.getNome()
                ));
            }
        }

        historico.sort(Comparator.comparing(
                (MovimentacaoDetalhe item) ->
                        item.movimentacao().getDataHora()
        ).reversed());

        return historico;
    }
}
