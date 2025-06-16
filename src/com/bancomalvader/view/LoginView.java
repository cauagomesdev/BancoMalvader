package com.bancomalvader.view;

import com.bancomalvader.controller.LoginController;
import com.bancomalvader.model.Usuario;
import com.bancomalvader.util.CpfValidator;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class LoginView extends JFrame {

    private LoginController loginController;

    private JTextField txtCpf;
    private JPasswordField txtSenha;
    private JTextField txtOtp;
    private ButtonGroup bgTipoUsuario; // Para agrupar os radio buttons
    private JRadioButton rbFuncionario;
    private JRadioButton rbCliente;

    public LoginView() {
        loginController = new LoginController();

        setTitle("Banco Malvader - Login");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centraliza a janela

        // Painel principal com GridBagLayout
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); // Espaçamento entre componentes
        gbc.fill = GridBagConstraints.HORIZONTAL; // Preenche horizontalmente
        gbc.anchor = GridBagConstraints.WEST; // Alinha à esquerda

        int row = 0;

        // Título
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2; // Ocupa duas colunas para o título
        gbc.anchor = GridBagConstraints.CENTER; // Centraliza o título
        panel.add(new JLabel("<html><b style='font-size:16px;'>Bem-vindo ao Banco Malvader</b></html>"), gbc);
        row++;
        gbc.anchor = GridBagConstraints.WEST; // Volta ao alinhamento padrão
        gbc.gridwidth = 1; // Volta à largura de uma coluna

        // Tipo de Usuário
        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("Tipo de Usuário:"), gbc);
        JPanel panelTipoUsuario = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rbFuncionario = new JRadioButton("Funcionário");
        rbCliente = new JRadioButton("Cliente");
        bgTipoUsuario = new ButtonGroup();
        bgTipoUsuario.add(rbFuncionario);
        bgTipoUsuario.add(rbCliente);
        rbFuncionario.setSelected(true); // Seleciona Funcionário como padrão
        panelTipoUsuario.add(rbFuncionario);
        panelTipoUsuario.add(rbCliente);
        gbc.gridx = 1; gbc.gridy = row; panel.add(panelTipoUsuario, gbc);
        row++;

        // CPF
        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("CPF:"), gbc);
        try {
            MaskFormatter cpfMask = new MaskFormatter("###.###.###-##");
            txtCpf = new JFormattedTextField(cpfMask);
            txtCpf.setColumns(12); // Tamanho visual
        } catch (java.text.ParseException e) {
            txtCpf = new JFormattedTextField();
            System.err.println("Erro ao criar máscara de CPF: " + e.getMessage());
        }
        gbc.gridx = 1; gbc.gridy = row; panel.add(txtCpf, gbc);
        row++;

        // Senha
        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("Senha:"), gbc);
        gbc.gridx = 1; gbc.gridy = row; txtSenha = new JPasswordField(15); panel.add(txtSenha, gbc);
        row++;

        // OTP
        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("OTP:"), gbc);
        gbc.gridx = 1; gbc.gridy = row; txtOtp = new JTextField(6); panel.add(txtOtp, gbc);
        row++;

        // Botão Gerar OTP
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JButton btnGerarOtp = new JButton("Gerar OTP");
        btnGerarOtp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gerarOtp();
            }
        });
        panel.add(btnGerarOtp, gbc);
        row++;
        gbc.gridwidth = 1; // Reseta

        // Botões de Ação (Login e Sair)

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        JButton btnLogin = new JButton("Login");
        JButton btnSair = new JButton("Sair");
        JButton btnCadastrar = new JButton("Cadastrar-se");

        btnCadastrar.addActionListener(e -> {
            // Abre a tela de cadastro para um NOVO CLIENTE
            new CadastrarUsuarioView(this, "CLIENTE").setVisible(true);
        });
        
        gbc.gridx = 0; // Ajuste as coordenadas para seu layout
        gbc.gridy = row++; // Ajuste a linha
        gbc.gridwidth = 2; // Ocupa duas colunas
        panel.add(btnCadastrar, gbc);
        
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                realizarLogin();
            }
        });

        btnSair.addActionListener(e -> System.exit(0)); // Fecha a aplicação

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnSair);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        add(panel, BorderLayout.CENTER);
    }

    private void gerarOtp() {
        String cpf = txtCpf.getText().replaceAll("[^0-9]", "").trim();
        String tipoUsuario = rbFuncionario.isSelected() ? "FUNCIONARIO" : "CLIENTE";

        if (!CpfValidator.isValidFormat(cpf)) {
            JOptionPane.showMessageDialog(this, "CPF inválido. Por favor, insira um CPF válido.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Usuario usuario = loginController.buscarUsuarioParaOtp(cpf, tipoUsuario); // CHAMA O NOVO MÉTODO PÚBLICO
            if (usuario == null) {
                JOptionPane.showMessageDialog(this, "CPF não encontrado para o tipo de usuário selecionado.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            loginController.gerarNovoOtp(usuario.getIdUsuario());

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao gerar OTP: " + e.getMessage(), "Erro no Banco de Dados", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void realizarLogin() {
        String cpf = txtCpf.getText().replaceAll("[^0-9]", "").trim(); // Remove máscara
        String senha = new String(txtSenha.getPassword());
        String otp = txtOtp.getText().trim();
        String tipoUsuario = rbFuncionario.isSelected() ? "FUNCIONARIO" : "CLIENTE";

        if (!CpfValidator.isValidFormat(cpf) || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha o CPF e a senha.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Usuario usuarioLogado = loginController.autenticar(cpf, senha, otp, tipoUsuario);

        if (usuarioLogado != null) {
            JOptionPane.showMessageDialog(this, "Login bem-sucedido! Bem-vindo, " + usuarioLogado.getNome() + "!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            // Fechar a tela de login e abrir a tela principal do menu
            dispose(); // Fecha a tela de login
            TelaPrincipal telaPrincipal = new TelaPrincipal(usuarioLogado); // Passa o usuário logado
            telaPrincipal.setVisible(true);
        } else {
            // Mensagens de erro já são tratadas pelo Controller
            // Se o Controller retorna null, a falha já foi reportada.
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginView().setVisible(true);
        });
    }
}
