package com.bancomalvader.view;

import javax.swing.*;
import java.awt.*;

// Esta classe representa o PAINEL de conteúdo do menu do cliente.
// Ela estende JPanel e contém os botões e a barra de status.
public class TelaPrincipalClienteView extends JPanel { 

    // Declare seus botões como campos privados
    private JButton btnSaldo;
    private JButton btnExtrato;
    private JButton btnDeposito;
    private JButton btnSaque;
    private JButton btnTransferencia;
    private JButton btnLimite;
    private JButton btnAlterarDados;
    private JButton btnSair;

    // ✅ CORREÇÃO AQUI: Declarar statusBar e lblInfoUsuario como membros da classe
    private JPanel statusBar;
    private JLabel lblInfoUsuario;

    public TelaPrincipalClienteView() {
        // JPanel não tem título, setSize, setDefaultCloseOperation.
        // Essas configurações são para o JFrame pai (TelaPrincipal).
        setLayout(new BorderLayout()); // O layout do JPanel

        // Painel para exibir as informações do usuário
        statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT)); // ✅ Inicialização
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        
        lblInfoUsuario = new JLabel("Carregando informações do usuário..."); // ✅ Inicialização
        statusBar.add(lblInfoUsuario); // Adiciona o JLabel ao JPanel
        
        add(statusBar, BorderLayout.SOUTH); // Adiciona o JPanel da barra de status a este JPanel

        // Painel para os botões do menu
        JPanel menuPanel = new JPanel(new GridLayout(4, 2, 20, 20)); // Layout de 4x2 com espaçamento
        menuPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50)); // Padding ao redor

        // Inicialize seus botões
        btnSaldo = new JButton("Saldo");
        btnExtrato = new JButton("Consultar Extrato");
        btnDeposito = new JButton("Fazer Depósito");
        btnSaque = new JButton("Fazer Saque");
        btnTransferencia = new JButton("Fazer Transferência");
        btnLimite = new JButton("Consultar Limite");
        btnAlterarDados = new JButton("Alterar Dados");
        btnSair = new JButton("Sair");

        // Adicione os botões ao painel do menu
        menuPanel.add(btnSaldo);
        menuPanel.add(btnExtrato);
        menuPanel.add(btnDeposito);
        menuPanel.add(btnSaque);
        menuPanel.add(btnTransferencia);
        menuPanel.add(btnLimite);
        menuPanel.add(btnAlterarDados);
        menuPanel.add(btnSair);

        add(menuPanel, BorderLayout.CENTER); // Adiciona o painel de botões a este JPanel
    }

    // Métodos Getters para os botões - ESSENCIAIS para o Controller acessar
    public JButton getBtnSaldo() {
        return btnSaldo;
    }

    public JButton getBtnExtrato() {
        return btnExtrato;
    }

    public JButton getBtnDeposito() {
        return btnDeposito;
    }

    public JButton getBtnSaque() {
        return btnSaque;
    }

    public JButton getBtnTransferencia() {
        return btnTransferencia;
    }

    public JButton getBtnLimite() {
        return btnLimite;
    }

    public JButton getBtnAlterarDados() {
        return btnAlterarDados;
    }

    public JButton getBtnSair() {
        return btnSair;
    }

    /**
     * Método para exibir informações do usuário na barra de status.
     * O Controller deve chamar este método após o login.
     * @param nome Nome do usuário.
     * @param cpf CPF do usuário.
     * @param tipo Tipo de usuário (CLIENTE/FUNCIONARIO).
     */
    public void setInfoUsuario(String nome, String cpf, String tipo) {
        // ✅ CORREÇÃO AQUI: Acessar diretamente o lblInfoUsuario que já é um membro da classe
        if (lblInfoUsuario != null) { // Verificação para garantir que o JLabel foi inicializado
            lblInfoUsuario.setText("Usuário: " + nome + " | CPF: " + cpf + " | Cargo/Tipo: " + tipo);
        }
    }
}