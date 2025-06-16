package com.bancomalvader.view;

import javax.swing.*;
import java.awt.*;

// ✅ CORRIGIDO: Esta classe agora estende JPanel, não JFrame.
// Ela representa o PAINEL de conteúdo do menu do funcionário.
public class TelaPrincipalFuncionarioView extends JPanel { // Mude de JFrame para JPanel

    private JButton btnAbrirConta;
    private JButton btnEncerrarConta;
    private JButton btnConsultarDados;
    private JButton btnAlterarDados;
    private JButton btnCadastrarFuncionario;
    private JButton btnRelatorios;
    private JButton btnSair;

    private JLabel lblInfoUsuario; // Se for usar uma barra de status interna

    public TelaPrincipalFuncionarioView() {
        // JPanel não tem título, setSize, setDefaultCloseOperation.
        // Essas configurações são para o JFrame pai (TelaPrincipal).

        setLayout(new BorderLayout()); // Layout para o JPanel

        // ✅ REVISADO: Barra de status movida para TelaPrincipal.java,
        // pois ela exibe informações gerais do usuário logado no JFrame principal.
        /*
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        lblInfoUsuario = new JLabel("Carregando informações do usuário...");
        statusBar.add(lblInfoUsuario);
        add(statusBar, BorderLayout.SOUTH);
        */

        JPanel menuPanel = new JPanel(new GridLayout(4, 2, 15, 15));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        btnAbrirConta = new JButton("Abrir Conta");
        btnEncerrarConta = new JButton("Encerrar Conta");
        btnConsultarDados = new JButton("Consultar Dados");
        btnAlterarDados = new JButton("Alterar Dados");
        btnCadastrarFuncionario = new JButton("Cadastrar Funcionário");
        btnRelatorios = new JButton("Gerar Relatórios");
        btnSair = new JButton("Sair");

        menuPanel.add(btnAbrirConta);
        menuPanel.add(btnEncerrarConta);
        menuPanel.add(btnConsultarDados);
        menuPanel.add(btnAlterarDados);
        menuPanel.add(btnCadastrarFuncionario);
        menuPanel.add(btnRelatorios);
        menuPanel.add(btnSair);

        add(menuPanel, BorderLayout.CENTER); // Adiciona o painel de botões a este JPanel
    }

    public JButton getBtnAbrirConta() { return btnAbrirConta; }
    public JButton getBtnEncerrarConta() { return btnEncerrarConta; }
    public JButton getBtnConsultarDados() { return btnConsultarDados; }
    public JButton getBtnAlterarDados() { return btnAlterarDados; }
    public JButton getBtnCadastrarFuncionario() { return btnCadastrarFuncionario; }
    public JButton getBtnRelatorios() { return btnRelatorios; }
    public JButton getBtnSair() { return btnSair; }

    // ✅ REVISADO: setInfoUsuario agora é chamado no JFrame TelaPrincipal.
    /*
    public void setInfoUsuario(String nome, String cpf, String tipo) {
        if (lblInfoUsuario != null) {
            lblInfoUsuario.setText("Usuário: " + nome + " | CPF: " + cpf + " | Cargo/Tipo: " + tipo);
        }
    }
    */
}
