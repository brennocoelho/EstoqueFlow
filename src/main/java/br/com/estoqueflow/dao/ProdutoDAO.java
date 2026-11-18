
package br.com.estoqueflow.dao;

import br.com.estoqueflow.database.DatabaseManager;
import br.com.estoqueflow.model.Produto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class ProdutoDAO {

    public long inserir(Produto produto) throws SQLException {
        String sql = """
            INSERT INTO produtos (
                codigo, nome, categoria, preco,
                quantidade, estoque_minimo, ativo
            ) VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (
                Connection conexao = DatabaseManager.conectar();
                PreparedStatement stmt = conexao.prepareStatement(
                        sql, Statement.RETURN_GENERATED_KEYS
                )
        ) {
            stmt.setString(1, produto.getCodigo());
            stmt.setString(2, produto.getNome());
            stmt.setString(3, produto.getCategoria());
            stmt.setBigDecimal(4, produto.getPreco());
            stmt.setInt(5, produto.getQuantidade());
            stmt.setInt(6, produto.getEstoqueMinimo());
            stmt.setBoolean(7, produto.isAtivo());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }

            throw new SQLException(
                    "Não foi possível recuperar o ID gerado."
            );
        }
    }

    public Optional<Produto> buscarPorCodigo(
            String codigo
    ) throws SQLException {

        String sql = """
            SELECT * FROM produtos WHERE codigo = ?
            """;

        try (
                Connection conexao = DatabaseManager.conectar();
                PreparedStatement stmt =
                        conexao.prepareStatement(sql)
        ) {
            stmt.setString(
                    1, codigo.strip().toUpperCase(Locale.ROOT)
            );

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearProduto(rs));
                }
            }
        }

        return Optional.empty();
    }

    public List<Produto> listarTodos() throws SQLException {
        String sql = """
            SELECT * FROM produtos ORDER BY nome
            """;

        List<Produto> produtos = new ArrayList<>();

        try (
                Connection conexao = DatabaseManager.conectar();
                PreparedStatement stmt =
                        conexao.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                produtos.add(mapearProduto(rs));
            }
        }

        return produtos;
    }


    /**
     * Pesquisa produtos por código, nome ou categoria.
     * Uma pesquisa vazia retorna todos os produtos.
     */
    public List<Produto> pesquisar(String termo) throws SQLException {
        String filtro = termo == null
                ? ""
                : termo.strip().toUpperCase(Locale.ROOT);

        if (filtro.isEmpty()) {
            return listarTodos();
        }

        String sql = """
            SELECT * FROM produtos
            WHERE UPPER(codigo) LIKE ?
               OR UPPER(nome) LIKE ?
               OR UPPER(categoria) LIKE ?
            ORDER BY nome
            """;

        List<Produto> produtos = new ArrayList<>();
        String parametro = "%" + filtro + "%";

        try (
                Connection conexao = DatabaseManager.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {
            stmt.setString(1, parametro);
            stmt.setString(2, parametro);
            stmt.setString(3, parametro);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    produtos.add(mapearProduto(rs));
                }
            }
        }

        return produtos;
    }

    /**
     * Atualiza os dados cadastrais sem sobrescrever o saldo de estoque.
     * A quantidade continua sendo controlada pelo EstoqueService.
     */
    public void atualizar(Produto produto) throws SQLException {
        String sql = """
            UPDATE produtos
            SET codigo = ?, nome = ?, categoria = ?, preco = ?,
                estoque_minimo = ?, ativo = ?
            WHERE id = ?
            """;

        try (
                Connection conexao = DatabaseManager.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {
            stmt.setString(
                    1, produto.getCodigo().strip().toUpperCase(Locale.ROOT)
            );
            stmt.setString(2, produto.getNome().strip());
            stmt.setString(3, produto.getCategoria().strip());
            stmt.setBigDecimal(4, produto.getPreco());
            stmt.setInt(5, produto.getEstoqueMinimo());
            stmt.setBoolean(6, produto.isAtivo());
            stmt.setLong(7, produto.getId());

            if (stmt.executeUpdate() != 1) {
                throw new SQLException(
                        "Produto não encontrado para atualização."
                );
            }
        }
    }

    /**
     * Ativa ou desativa um produto sem apagar seu histórico.
     */
    public void alterarStatus(long id, boolean ativo) throws SQLException {
        String sql = "UPDATE produtos SET ativo = ? WHERE id = ?";

        try (
                Connection conexao = DatabaseManager.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {
            stmt.setBoolean(1, ativo);
            stmt.setLong(2, id);

            if (stmt.executeUpdate() != 1) {
                throw new SQLException(
                        "Produto não encontrado para alteração de status."
                );
            }
        }
    }

    public Optional<Produto> buscarPorIdParaAtualizacao(
            Connection conexao, long id
    ) throws SQLException {

        String sql = """
        SELECT * FROM produtos
        WHERE id = ?
        FOR UPDATE
        """;

        try (PreparedStatement stmt =
                     conexao.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearProduto(rs));
                }
            }
        }

        return Optional.empty();
    }

    public void atualizarQuantidade(
            Connection conexao, long id, int quantidade
    ) throws SQLException {

        String sql = """
        UPDATE produtos
        SET quantidade = ?
        WHERE id = ? AND ativo = TRUE
        """;

        try (PreparedStatement stmt =
                     conexao.prepareStatement(sql)) {

            stmt.setInt(1, quantidade);
            stmt.setLong(2, id);

            if (stmt.executeUpdate() != 1) {
                throw new SQLException(
                        "Não foi possível atualizar o estoque."
                );
            }
        }
    }


    private Produto mapearProduto(ResultSet rs)
            throws SQLException {

        return new Produto(
                rs.getLong("id"),
                rs.getString("codigo"),
                rs.getString("nome"),
                rs.getString("categoria"),
                rs.getBigDecimal("preco"),
                rs.getInt("quantidade"),
                rs.getInt("estoque_minimo"),
                rs.getBoolean("ativo")
        );
    }
}
