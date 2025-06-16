package com.bancomalvader.view;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Esta View será um JDialog para coletar dados para o encerramento de conta
public class EncerrarContaView extends JDialog {

    private JTextField txtNumeroConta;
    private JPasswordField txtSenhaAdmin;
    private JTextField txtOtp;
    private JTextArea txtMotivo;
    
    // Campo para armazenar o resultado (número da conta, senha, OTP, motivo)
    // Se o controller precisar de todos os dados de uma vez.
    private String numeroConta;
    private String senhaAdmin;
    private String otp;
    private String motivo;

    private boolean confirmacao = false; // Indica se o usuário confirmou a operação

    public EncerrarContaView(Frame owner) {
        super(owner, "Encerrar Conta Bancária", true); // Diálogo modal
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(450, 400);
        setResizable(false);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        add(panel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Número da Conta
        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("Número da Conta:"), gbc);
        gbc.gridx = 1; gbc.gridy = row; txtNumeroConta = new JTextField(20); panel.add(txtNumeroConta, gbc);
        row++;

        // Senha Administrador
        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("Senha Administrador:"), gbc);
        gbc.gridx = 1; gbc.gridy = row; txtSenhaAdmin = new JPasswordField(20); panel.add(txtSenhaAdmin, gbc);
        row++;

        // OTP
        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("OTP:"), gbc);
        gbc.gridx = 1; gbc.gridy = row; txtOtp = new JTextField(6); panel.add(txtOtp, gbc);
        row++;

        // Motivo do Encerramento
        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("Motivo do Encerramento:"), gbc);
        gbc.gridx = 1; gbc.gridy = row;
        txtMotivo = new JTextArea(4, 20); // 4 linhas, 20 colunas
        txtMotivo.setLineWrap(true);
        txtMotivo.setWrapStyleWord(true);
        JScrollPane scrollMotivo = new JScrollPane(txtMotivo);
        panel.add(scrollMotivo, gbc);
        row++;

        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnConfirmar = new JButton("Confirmar");
        JButton btnCancelar = new JButton("Cancelar");

        btnConfirmar.addActionListener(e -> {
            this.numeroConta = txtNumeroConta.getText().trim();
            this.senhaAdmin = new String(txtSenhaAdmin.getPassword());
            this.otp = txtOtp.getText().trim();
            this.motivo = txtMotivo.getText().trim();
            this.confirmacao = true; // Define que a operação foi confirmada
            dispose(); // Fecha o diálogo
        });

        btnCancelar.addActionListener(e -> {
            this.confirmacao = false; // Define que a operação foi cancelada
            dispose(); // Fecha o diálogo
        });

        buttonPanel.add(btnConfirmar);
        buttonPanel.add(btnCancelar);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Getters para os dados coletados
    public String getNumeroConta() { return numeroConta; }
    public String getSenhaAdmin() { return senhaAdmin; }
    public String getOtp() { return otp; }
    public String getMotivo() { return motivo; }
    public boolean isConfirmacao() { return confirmacao; }
}
