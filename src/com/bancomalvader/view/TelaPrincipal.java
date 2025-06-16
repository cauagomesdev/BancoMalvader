package com.bancomalvader.view;

import com.bancomalvader.model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TelaPrincipal extends JFrame {

    private Usuario usuarioLogado; // Armazena o usuário que logou

    public TelaPrincipal(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
        setTitle("Banco Malvader - Menu Principal (" + usuarioLogado.getTipoUsuario() + ")");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Barra de status com informações do usuário
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        JLabel lblInfoUsuario = new JLabel("Usuário: " + usuarioLogado.getNome() + " | CPF: " + usuarioLogado.getCpf() + " | Cargo/Tipo: " + usuarioLogado.getTipoUsuario());
        statusBar.add(lblInfoUsuario);
        add(statusBar, BorderLayout.SOUTH);

        // Painel central para o conteúdo do menu
        JPanel mainPanel = new JPanel(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);

        // Conteúdo do menu baseado no tipo de usuário
        if ("FUNCIONARIO".equals(usuarioLogado.getTipoUsuario())) {
            setupMenuFuncionario(mainPanel);
        } else if ("CLIENTE".equals(usuarioLogado.getTipoUsuario())) {
            setupMenuCliente(mainPanel);
        }
    }

    public TelaPrincipal() {
		// TODO Auto-generated constructor stub
	}

	private void setupMenuFuncionario(JPanel parentPanel) {
        JPanel menuPanel = new JPanel(new GridLayout(4, 2, 15, 15)); // 4 linhas, 2 colunas, espaçamento
        menuPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30)); // Padding

        JButton btnAbrirConta = new JButton("Abrir Conta");
        JButton btnEncerrarConta = new JButton("Encerrar Conta");
        JButton btnConsultarDados = new JButton("Consultar Dados");
        JButton btnAlterarDados = new JButton("Alterar Dados");
        JButton btnCadastrarFuncionario = new JButton("Cadastrar Funcionário");
        JButton btnGerarRelatorios = new JButton("Gerar Relatórios");
        JButton btnSair = new JButton("Sair");

        // Ações dos botões
        btnAbrirConta.addActionListener(e -> {
            AbrirConta abrirContaDialog = new AbrirConta(this); // 'this' refere-se ao JFrame principal
            abrirContaDialog.setVisible(true);
        });

        btnSair.addActionListener(e -> {
            // Lógica de logout (auditoria)
            JOptionPane.showMessageDialog(this, "Sessão encerrada.", "Logout", JOptionPane.INFORMATION_MESSAGE);
            new LoginView().setVisible(true); // Volta para a tela de login
            dispose(); // Fecha esta janela
        });

        // Adiciona os botões ao painel do menu
        menuPanel.add(btnAbrirConta);
        menuPanel.add(btnEncerrarConta);
        menuPanel.add(btnConsultarDados);
        menuPanel.add(btnAlterarDados);
        menuPanel.add(btnCadastrarFuncionario);
        menuPanel.add(btnGerarRelatorios);
        menuPanel.add(btnSair);

        parentPanel.add(menuPanel, BorderLayout.CENTER);
    }

    private void setupMenuCliente(JPanel parentPanel) {
        JPanel menuPanel = new JPanel(new GridLayout(3, 2, 15, 15)); // Exemplo de layout para cliente
        menuPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JButton btnOperacoesConta = new JButton("Operações de Conta");
        JButton btnConsultarExtrato = new JButton("Consultar Extrato");
        JButton btnConsultarLimite = new JButton("Consultar Limite");
        JButton btnSair = new JButton("Sair");

        // Ações dos botões
        // btnOperacoesConta.addActionListener(...)
        btnSair.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Sessão encerrada.", "Logout", JOptionPane.INFORMATION_MESSAGE);
            new LoginView().setVisible(true);
            dispose();
        });

        menuPanel.add(btnOperacoesConta);
        menuPanel.add(btnConsultarExtrato);
        menuPanel.add(btnConsultarLimite);
        menuPanel.add(btnSair);

        parentPanel.add(menuPanel, BorderLayout.CENTER);
    }
}
