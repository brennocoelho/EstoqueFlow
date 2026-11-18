
package br.com.estoqueflow;

import br.com.estoqueflow.dao.ProdutoDAO;
import br.com.estoqueflow.database.DatabaseManager;
import br.com.estoqueflow.model.Produto;

import java.math.BigDecimal;
import java.sql.SQLException;

public class TestePersistencia {

    public static void main(String[] args)
            throws SQLException {

        DatabaseManager.inicializar();

        ProdutoDAO dao = new ProdutoDAO();

        String codigo = "TESTE-001";

        if (dao.buscarPorCodigo(codigo).isEmpty()) {

            Produto produto = new Produto(
                    null,
                    codigo,
                    "Teclado Mecânico",
                    "Periféricos",
                    new BigDecimal("199.90"),
                    0,
                    5,
                    true
            );

            long id = dao.inserir(produto);

            System.out.println(
                    "Produto cadastrado! ID: " + id
            );

        } else {
            System.out.println(
                    "O produto já existe no banco."
            );
        }

        System.out.println("\nPRODUTOS CADASTRADOS:");

        for (Produto p : dao.listarTodos()) {
            System.out.printf(
                    "%d | %s | %s | R$ %s | Qtd: %d%n",
                    p.getId(),
                    p.getCodigo(),
                    p.getNome(),
                    p.getPreco(),
                    p.getQuantidade()
            );
        }

        System.out.println(
                "\nBanco: " + DatabaseManager.caminhoArquivo()
        );
    }
}
