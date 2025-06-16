package com.bancomalvader.view;

import com.bancomalvader.controller.UsuarioController;
import com.bancomalvader.util.CpfValidator;
import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.sql.SQLException;

public class CadastrarUsuarioView extends JDialog {

    private UsuarioController controller;
    private String tipoUsuarioPadrao; 

    // Componentes para Dados do Usuário
    private JTextField txtNome;
    private JFormattedTextField txtCpf;
    private JFormattedTextField txtDataNascimento;
    private JFormattedTextField txtTelefone;
    private JPasswordField txtSenha;
    private ButtonGroup bgTipoUsuario;
    private JRadioButton rbCliente;
    private JRadioButton rbFuncionario;

    // Construtor
    public CadastrarUsuarioView(Frame owner, String tipoUsuarioPadrao) {
        super(owner, "Cadastrar Novo Usuário - Banco Malvader", true); 
        this.controller = new UsuarioController();
        this.tipoUsuarioPadrao = tipoUsuarioPadrao != null ? tipoUsuarioPadrao : "";

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(600, 500); // Tamanho um pouco maior para acomodar melhor
        setResizable(false); // Fixa o tamanho da janela para manter o layout
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Mais padding
        add(mainPanel, BorderLayout.CENTER); // Não precisa de JScrollPane para esta tela simplificada

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); // Aumentei o espaçamento entre os componentes
        gbc.fill = GridBagConstraints.HORIZONTAL; // Preenche horizontalmente
        gbc.anchor = GridBagConstraints.WEST; // Alinha à esquerda

        int row = 0;

        // --- Título da Tela ---
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 4; // Ocupa todas as colunas para o título
        gbc.anchor = GridBagConstraints.CENTER; // Centraliza o título
        mainPanel.add(new JLabel("<html><b style='font-size:18px;'>Cadastro de Novo Usuário</b></html>"), gbc);
        row++;
        gbc.anchor = GridBagConstraints.WEST; // Volta ao alinhamento padrão
        
        // --- Tipo de Usuário (se não for pré-definido) ---
        if (this.tipoUsuarioPadrao.isEmpty()) { 
            gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; // Ajusta gridwidth para o label
            mainPanel.add(new JLabel("Tipo de Usuário:"), gbc);
            
            rbCliente = new JRadioButton("Cliente");
            rbFuncionario = new JRadioButton("Funcionário");
            bgTipoUsuario = new ButtonGroup();
            bgTipoUsuario.add(rbCliente);
            bgTipoUsuario.add(rbFuncionario);
            rbCliente.setSelected(true); // Padrão
            
            JPanel panelTipoUsuarioRadio = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // Remove espaçamento extra do FlowLayout
            panelTipoUsuarioRadio.add(rbCliente);
            panelTipoUsuarioRadio.add(rbFuncionario);
            
            gbc.gridx = 1; gbc.gridy = row; gbc.gridwidth = 3; // Ocupa o restante das colunas para os radio buttons
            mainPanel.add(panelTipoUsuarioRadio, gbc);
            row++;
            gbc.gridwidth = 1; // Reseta
        } else {
            // Se o tipo é pré-definido, podemos mostrar um JLabel informativo
            gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4;
            mainPanel.add(new JLabel("<html><b>Tipo de Cadastro: " + this.tipoUsuarioPadrao + "</b></html>"), gbc);
            row++;
            gbc.gridwidth = 1; // Reseta
        }

        // --- Seção: Dados do Usuário ---
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4;
        mainPanel.add(new JSeparator(), gbc); // Adiciona uma linha separadora
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4;
        mainPanel.add(new JLabel("<html><b>Informações Pessoais:</b></html>"), gbc);
        row++; gbc.gridwidth = 1; // Reseta

        // Nome
        gbc.gridx = 0; gbc.gridy = row; mainPanel.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.gridwidth = 3; gbc.weightx = 1.0; // Expandir horizontalmente
        txtNome = new JTextField(30); mainPanel.add(txtNome, gbc);
        row++; gbc.gridwidth = 1; gbc.weightx = 0.0; // Resetar weightx

        // CPF e Data de Nascimento (na mesma linha)
        gbc.gridx = 0; gbc.gridy = row; mainPanel.add(new JLabel("CPF:"), gbc);
        try { MaskFormatter cpfMask = new MaskFormatter("###.###.###-##"); txtCpf = new JFormattedTextField(cpfMask); txtCpf.setColumns(12); }
        catch (java.text.ParseException e) { txtCpf = new JFormattedTextField(); System.err.println("Erro CPF mask: " + e.getMessage()); }
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.5; // Dividir o espaço com a data de nascimento
        mainPanel.add(txtCpf, gbc);

        gbc.gridx = 2; gbc.gridy = row; mainPanel.add(new JLabel("Data Nasc.:"), gbc);
        try { MaskFormatter dataMask = new MaskFormatter("##/##/####"); txtDataNascimento = new JFormattedTextField(dataMask); txtDataNascimento.setColumns(10); }
        catch (java.text.ParseException e) { txtDataNascimento = new JFormattedTextField(); System.err.println("Erro Data Nasc mask: " + e.getMessage()); }
        gbc.gridx = 3; gbc.gridy = row; gbc.weightx = 0.5; // Dividir o espaço
        mainPanel.add(txtDataNascimento, gbc);
        row++; gbc.weightx = 0.0; // Resetar weightx

        // Telefone e Senha (na mesma linha)
        gbc.gridx = 0; gbc.gridy = row; mainPanel.add(new JLabel("Telefone:"), gbc);
        try { MaskFormatter telMask = new MaskFormatter("(##) #####-####"); txtTelefone = new JFormattedTextField(telMask); txtTelefone.setColumns(15); }
        catch (java.text.ParseException e) { txtTelefone = new JFormattedTextField(); System.err.println("Erro Telefone mask: " + e.getMessage()); }
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.5;
        mainPanel.add(txtTelefone, gbc);

        gbc.gridx = 2; gbc.gridy = row; mainPanel.add(new JLabel("Senha:"), gbc);
        gbc.gridx = 3; gbc.gridy = row; gbc.weightx = 0.5;
        txtSenha = new JPasswordField(15); mainPanel.add(txtSenha, gbc);
        row++; gbc.weightx = 0.0; // Resetar weightx
        
        // --- Botões de Ação ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15)); // Mais espaçamento
        JButton btnCadastrar = new JButton("Cadastrar");
        JButton btnCancelar = new JButton("Cancelar");

        btnCadastrar.addActionListener(e -> cadastrarUsuario());
        btnCancelar.addActionListener(e -> dispose());

        buttonPanel.add(btnCadastrar);
        buttonPanel.add(btnCancelar);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    // Métodos removidos: setupFuncionarioFields, toggleFuncionarioFields (não usados na versão simplificada)

    private void cadastrarUsuario() {
        String nome = txtNome.getText().trim();
        String cpf = txtCpf.getText().replaceAll("[^0-9]", "").trim(); 
        LocalDate dataNascimento = null;
        try {
            if (txtDataNascimento.getText().replaceAll("[^0-9]", "").isEmpty()) {
                throw new IllegalArgumentException("Data de Nascimento é obrigatória.");
            }
            dataNascimento = LocalDate.parse(txtDataNascimento.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Formato de Data de Nascimento inválido. Use dd/mm/aaaa.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String telefone = txtTelefone.getText().replaceAll("[^0-9]", "").trim(); 
        String senha = new String(txtSenha.getPassword());

        String tipoUsuario;
        if (this.tipoUsuarioPadrao.isEmpty()) { 
            if (rbCliente.isSelected()) tipoUsuario = "CLIENTE";
            else if (rbFuncionario.isSelected()) tipoUsuario = "FUNCIONARIO";
            else { JOptionPane.showMessageDialog(this, "Selecione o tipo de usuário.", "Erro", JOptionPane.ERROR_MESSAGE); return; }
        } else { 
            tipoUsuario = this.tipoUsuarioPadrao;
        }

        try {
            boolean sucesso = controller.cadastrarApenasUsuario(nome, cpf, dataNascimento, telefone, senha, tipoUsuario);
            if (sucesso) {
                JOptionPane.showMessageDialog(this, "Usuário cadastrado com sucesso! (Atenção: Endereço e perfil específico não foram cadastrados).", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                dispose(); 
            }
        } catch (IllegalArgumentException | SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro no cadastro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
