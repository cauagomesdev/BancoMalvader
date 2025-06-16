package com.bancomalvader; // Pacote raiz do seu projeto

import com.bancomalvader.view.LoginView; // Importa a classe da tela de login

import javax.swing.SwingUtilities; // Importa SwingUtilities para garantir a execução na EDT

public class Main {

    /**
     * Método principal (entry point) da aplicação.
     * Garante que a interface gráfica (Swing) seja inicializada na Event Dispatch Thread (EDT).
     * @param args Argumentos de linha de comando (não utilizados nesta aplicação).
     */
    public static void main(String[] args) {
        // SwingUtilities.invokeLater() é essencial para aplicações Swing.
        // Ele garante que todo o código que manipula a UI seja executado na Event Dispatch Thread (EDT).
        // A EDT é a única thread segura para interagir com componentes Swing, prevenindo deadlocks e inconsistências.
        SwingUtilities.invokeLater(() -> {
            // Cria uma nova instância da LoginView (a tela de login inicial)
            LoginView loginView = new LoginView();
            // Torna a tela de login visível para o usuário
            loginView.setVisible(true);
        });
    }
}
