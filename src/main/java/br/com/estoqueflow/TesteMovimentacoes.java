
package br.com.estoqueflow;

import br.com.estoqueflow.dao.ProdutoDAO;
import br.com.estoqueflow.dao.MovimentacaoDAO;
import br.com.estoqueflow.database.DatabaseManager;
import br.com.estoqueflow.model.Produto;
import br.com.estoqueflow.model.Movimentacao;
import br.com.estoqueflow.service.EstoqueService;

public class TesteMovimentacoes {

    public static void main(String[] args) throws Exception {

        DatabaseManager.inicializar();

        ProdutoDAO produtoDAO = new ProdutoDAO();
        EstoqueService service = new EstoqueService();
        MovimentacaoDAO movimentacaoDAO =
                new MovimentacaoDAO();

        Produto produto = produtoDAO
                .buscarPorCodigo("TESTE-001")
                .orElseThrow();

        long id = produto.getId();

        System.out.println("Saldo inicial: " +
                produto.getQuantidade());

        service.registrarEntrada(
                id, 20, "Compra de mercadorias"
        );

        service.registrarSaida(
                id, 5, "Venda de mercadorias"
        );

        try {
            service.registrarSaida(
                    id, 99999, "Saida invalida"
            );
        } catch (IllegalArgumentException e) {
            System.out.println(
                    "Operacao rejeitada: " + e.getMessage()
            );
        }

        Produto atualizado = produtoDAO
                .buscarPorCodigo("TESTE-001")
                .orElseThrow();

        System.out.println("Saldo final: " +
                atualizado.getQuantidade());

        System.out.println("\nHISTORICO:");

        for (Movimentacao mov :
                movimentacaoDAO.listarPorProduto(id)) {

            System.out.printf(
                    "%s | %d unidades | %s%n",
                    mov.getTipo(),
                    mov.getQuantidade(),
                    mov.getObservacao()
            );
        }
    }
}
