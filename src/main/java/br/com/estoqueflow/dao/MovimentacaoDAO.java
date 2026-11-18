
package br.com.estoqueflow.dao;

import br.com.estoqueflow.database.DatabaseManager;
import br.com.estoqueflow.model.Movimentacao;
import br.com.estoqueflow.model.TipoMovimentacao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovimentacaoDAO {

    public void registrar(
            Connection conexao,
            long produtoId,
            TipoMovimentacao tipo,
            int quantidade,
            String observacao
    ) throws SQLException {

        String sql = """
            INSERT INTO movimentacoes (
                produto_id, tipo, quantidade,
                data_hora, observacao
            )
            VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?)
            """;

        try (PreparedStatement stmt =
                     conexao.prepareStatement(sql)) {

            stmt.setLong(1, produtoId);
            stmt.setString(2, tipo.name());
            stmt.setInt(3, quantidade);
            stmt.setString(4, observacao);

            stmt.executeUpdate();
        }
    }

    public List<Movimentacao> listarPorProduto(
            long produtoId
    ) throws SQLException {

        String sql = """
            SELECT * FROM movimentacoes
            WHERE produto_id = ?
            ORDER BY data_hora DESC, id DESC
            """;

        List<Movimentacao> lista = new ArrayList<>();

        try (
                Connection conexao = DatabaseManager.conectar();
                PreparedStatement stmt =
                        conexao.prepareStatement(sql)
        ) {
            stmt.setLong(1, produtoId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Movimentacao(
                            rs.getLong("id"),
                            rs.getLong("produto_id"),
                            TipoMovimentacao.valueOf(
                                    rs.getString("tipo")
                            ),
                            rs.getInt("quantidade"),
                            rs.getTimestamp("data_hora")
                                    .toLocalDateTime(),
                            rs.getString("observacao")
                    ));
                }
            }
        }

        return lista;
    }
}
