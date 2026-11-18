
package br.com.estoqueflow;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
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
