package br.com.estoqueflow;

import br.com.estoqueflow.controller.EstoqueController;
import br.com.estoqueflow.database.DatabaseManager;
import br.com.estoqueflow.view.TelaPrincipal;

import javax.swing.*;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try {
            DatabaseManager.inicializar();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Erro ao inicializar o banco:\n" + e.getMessage(),
                    "EstoqueFlow", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                        UIManager.getCrossPlatformLookAndFeelClassName()
                );
            } catch (Exception ignored) {
                // Mantém o Look and Feel padrão caso o tema do sistema não esteja disponível.
            }
            new TelaPrincipal(new EstoqueController()).setVisible(true);
        });
    }
}
