
package br.com.estoqueflow.service;

import br.com.estoqueflow.dao.MovimentacaoDAO;
import br.com.estoqueflow.dao.ProdutoDAO;
import br.com.estoqueflow.database.DatabaseManager;
import br.com.estoqueflow.model.Produto;
import br.com.estoqueflow.model.TipoMovimentacao;

import java.sql.Connection;
import java.sql.SQLException;

public class EstoqueService {

    private final ProdutoDAO produtoDAO = new ProdutoDAO();
    private final MovimentacaoDAO movimentacaoDAO =
            new MovimentacaoDAO();

    public void registrarEntrada(
            long produtoId,
            int quantidade,
            String observacao
    ) throws SQLException {
        movimentar(produtoId, TipoMovimentacao.ENTRADA,
                quantidade, observacao);
    }

    public void registrarSaida(
            long produtoId,
            int quantidade,
            String observacao
    ) throws SQLException {
        movimentar(produtoId, TipoMovimentacao.SAIDA,
                quantidade, observacao);
    }

    private void movimentar(
            long produtoId,
            TipoMovimentacao tipo,
            int quantidade,
            String observacao
    ) throws SQLException {

        if (quantidade <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade deve ser maior que zero."
            );
        }

        try (Connection conexao =
                     DatabaseManager.conectar()) {

            conexao.setAutoCommit(false);

            try {
                Produto produto = produtoDAO
                        .buscarPorIdParaAtualizacao(
                                conexao, produtoId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Produto não encontrado."
                                )
                        );

                if (!produto.isAtivo()) {
                    throw new IllegalStateException(
                            "Produto inativo."
                    );
                }

                int saldoAtual = produto.getQuantidade();
                int novoSaldo;

                if (tipo == TipoMovimentacao.ENTRADA) {
                    novoSaldo = Math.addExact(
                            saldoAtual, quantidade
                    );
                } else {
                    if (quantidade > saldoAtual) {
                        throw new IllegalArgumentException(
                                "Estoque insuficiente. " +
                                        "Disponível: " + saldoAtual
                        );
                    }

                    novoSaldo = saldoAtual - quantidade;
                }

                produtoDAO.atualizarQuantidade(
                        conexao, produtoId, novoSaldo
                );

                movimentacaoDAO.registrar(
                        conexao,
                        produtoId,
                        tipo,
                        quantidade,
                        observacao
                );

                conexao.commit();

            } catch (SQLException | RuntimeException e) {
                try {
                    conexao.rollback();
                } catch (SQLException erroRollback) {
                    e.addSuppressed(erroRollback);
                }

                throw e;
            }
        }
    }
}
