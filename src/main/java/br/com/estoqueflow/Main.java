
package br.com.estoqueflow;

import br.com.estoqueflow.database.DatabaseManager;

import javax.swing.*;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {

        try {
            DatabaseManager.inicializar();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Erro ao inicializar o banco:\n" + e.getMessage(),
                    "Erro no EstoqueFlow",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        SwingUtilities.invokeLater(() -> {
            JFrame janela = new JFrame("EstoqueFlow");

            janela.setSize(900, 600);
            janela.setLocationRelativeTo(null);
            janela.setDefaultCloseOperation(
                    JFrame.EXIT_ON_CLOSE
            );

            JLabel titulo = new JLabel(
                    "Bem-vindo ao EstoqueFlow",
                    SwingConstants.CENTER
            );

            janela.add(titulo);
            janela.setVisible(true);
        });
    }
}
