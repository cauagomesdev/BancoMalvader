package com.bancomalvader.view;

import com.bancomalvader.controller.ContaController;
import com.bancomalvader.util.CpfValidator; 
import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal; // Importado para BigDecimal
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects; 

public class AbrirConta extends JDialog { 

    private ContaController controller;

    // Componentes para Dados do Cliente
    private JTextField txtNome;
    private JFormattedTextField txtCpf;
    private JFormattedTextField txtDataNascimento;
    private JFormattedTextField txtTelefone;
    private JPasswordField txtSenha;

    // Componentes para Endereço
    private JFormattedTextField txtCep;
    private JTextField txtLogradouro;
    private JTextField txtNumeroCasa; // Mantido como JTextField para leitura flexível
    private JTextField txtBairro;
    private JTextField txtCidade;
    private JComboBox<String> cmbEstado;
    private JTextField txtComplemento;

    // Componentes para Agência
    private JTextField txtAgencia; // Código da agência

    // Componentes para Tipo de Conta
    private JRadioButton rbPoupanca;
    private JRadioButton rbCorrente;
    private JRadioButton rbInvestimento;
    private ButtonGroup bgTipoConta;
    private JPanel panelTipoContaCampos; // Painel para campos específicos

    // Campos Específicos para Poupança
    private JTextField txtTaxaRendimentoCP;

    // Campos Específicos para Corrente
    private JTextField txtLimiteCC; // Leitura de string, será convertido para BigDecimal
    private JFormattedTextField txtDataVencimentoCC;
    private JTextField txtTaxaManutencaoCC; // Leitura de string, será convertido para BigDecimal

    // Campos Específicos para Investimento
    private JComboBox<String> cmbPerfilRiscoCI;
    private JTextField txtValorMinimoCI; // Leitura de string, será convertido para BigDecimal
    private JTextField txtTaxaRendimentoBaseCI; // Leitura de string, será convertido para BigDecimal

    // Este é o ID do funcionário que está abrindo a conta (para auditoria)
    // Em um sistema real, este valor viria do objeto Funcionario logado.
    private int idFuncionarioResponsavel = 1; 

    public AbrirConta(Frame owner) { 
        super(owner, "Abrir Nova Conta - Banco Malvader", true); 
        controller = new ContaController(); 

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(700, 800); 
        setLocationRelativeTo(owner); 
        setLayout(new BorderLayout()); 

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(mainPanel); 
        add(scrollPane, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); 
        gbc.fill = GridBagConstraints.HORIZONTAL; 
        gbc.anchor = GridBagConstraints.WEST; 

        int row = 0; 

        // --- Título "Tipo de Conta" ---
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 4; 
        mainPanel.add(new JLabel("<html><b>Tipo de Conta:</b></html>"), gbc);
        row++; 

        // --- Radio Buttons de Tipo de Conta ---
        rbPoupanca = new JRadioButton("Poupança");
        rbCorrente = new JRadioButton("Corrente");
        rbInvestimento = new JRadioButton("Investimento");
        bgTipoConta = new ButtonGroup();
        bgTipoConta.add(rbPoupanca);
        bgTipoConta.add(rbCorrente);
        bgTipoConta.add(rbInvestimento);
        rbPoupanca.setSelected(true); 

        JPanel panelTipoContaRadio = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTipoContaRadio.add(rbPoupanca);
        panelTipoContaRadio.add(rbCorrente);
        panelTipoContaRadio.add(rbInvestimento);
        
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 4; 
        mainPanel.add(panelTipoContaRadio, gbc);
        row++; 
        gbc.gridwidth = 1; 

        rbPoupanca.addActionListener(e -> atualizarCamposTipoConta("POUPANCA"));
        rbCorrente.addActionListener(e -> atualizarCamposTipoConta("CORRENTE"));
        rbInvestimento.addActionListener(e -> atualizarCamposTipoConta("INVESTIMENTO"));


        // --- Dados do Cliente ---
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 4;
        mainPanel.add(new JLabel("<html><b>Dados do Cliente:</b></html>"), gbc);
        row++; 
        gbc.gridwidth = 1; 

        gbc.gridx = 0; gbc.gridy = row; mainPanel.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.gridwidth = 3; txtNome = new JTextField(30); mainPanel.add(txtNome, gbc);
        row++; gbc.gridwidth = 1; 

        gbc.gridx = 0; gbc.gridy = row; mainPanel.add(new JLabel("CPF:"), gbc);
        try {
            MaskFormatter cpfMask = new MaskFormatter("###.###.###-##");
            txtCpf = new JFormattedTextField(cpfMask);
            txtCpf.setColumns(12); 
        } catch (java.text.ParseException e) {
            txtCpf = new JFormattedTextField();
            System.err.println("Erro ao criar máscara de CPF: " + e.getMessage());
        }
        gbc.gridx = 1; gbc.gridy = row; mainPanel.add(txtCpf, gbc);

        gbc.gridx = 2; gbc.gridy = row; mainPanel.add(new JLabel("Data Nasc. (dd/mm/aaaa):"), gbc);
        try {
            MaskFormatter dataMask = new MaskFormatter("##/##/####");
            txtDataNascimento = new JFormattedTextField(dataMask);
            txtDataNascimento.setColumns(10);
        } catch (java.text.ParseException e) {
            txtDataNascimento = new JFormattedTextField();
            System.err.println("Erro ao criar máscara de Data de Nascimento: " + e.getMessage());
        }
        gbc.gridx = 3; gbc.gridy = row; mainPanel.add(txtDataNascimento, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; mainPanel.add(new JLabel("Telefone (xx) xxxxx-xxxx:"), gbc);
        try {
            MaskFormatter telMask = new MaskFormatter("(##) #####-####");
            txtTelefone = new JFormattedTextField(telMask);
            txtTelefone.setColumns(15);
        } catch (java.text.ParseException e) {
            txtTelefone = new JFormattedTextField();
            System.err.println("Erro ao criar máscara de Telefone: " + e.getMessage());
        }
        gbc.gridx = 1; gbc.gridy = row; mainPanel.add(txtTelefone, gbc);

        gbc.gridx = 2; gbc.gridy = row; mainPanel.add(new JLabel("Senha:"), gbc);
        gbc.gridx = 3; gbc.gridy = row; txtSenha = new JPasswordField(15); mainPanel.add(txtSenha, gbc);
        row++;


        // --- Endereço ---
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 4;
        mainPanel.add(new JLabel("<html><b>Endereço:</b></html>"), gbc);
        row++; 
        gbc.gridwidth = 1; 

        gbc.gridx = 0; gbc.gridy = row; mainPanel.add(new JLabel("CEP (xxxxx-xxx):"), gbc);
        try {
            MaskFormatter cepMask = new MaskFormatter("#####-###");
            txtCep = new JFormattedTextField(cepMask);
            txtCep.setColumns(9);
        } catch (java.text.ParseException e) {
            txtCep = new JFormattedTextField();
            System.err.println("Erro ao criar máscara de CEP: " + e.getMessage());
        }
        gbc.gridx = 1; gbc.gridy = row; mainPanel.add(txtCep, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; mainPanel.add(new JLabel("Logradouro:"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.gridwidth = 3; txtLogradouro = new JTextField(30); mainPanel.add(txtLogradouro, gbc);
        row++; gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = row; mainPanel.add(new JLabel("Número:"), gbc);
        gbc.gridx = 1; gbc.gridy = row; txtNumeroCasa = new JTextField(10); mainPanel.add(txtNumeroCasa, gbc);

        gbc.gridx = 2; gbc.gridy = row; mainPanel.add(new JLabel("Bairro:"), gbc);
        gbc.gridx = 3; gbc.gridy = row; txtBairro = new JTextField(15); mainPanel.add(txtBairro, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; mainPanel.add(new JLabel("Cidade:"), gbc);
        gbc.gridx = 1; gbc.gridy = row; txtCidade = new JTextField(20); mainPanel.add(txtCidade, gbc);

        gbc.gridx = 2; gbc.gridy = row; mainPanel.add(new JLabel("Estado:"), gbc);
        String[] estados = {"AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI", "RJ", "RN", "RO", "RR", "RS", "SC", "SP", "SE", "TO"};
        cmbEstado = new JComboBox<>(estados);
        gbc.gridx = 3; gbc.gridy = row; mainPanel.add(cmbEstado, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; mainPanel.add(new JLabel("Complemento:"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.gridwidth = 3; txtComplemento = new JTextField(30); mainPanel.add(txtComplemento, gbc);
        row++; gbc.gridwidth = 1;


        // --- Dados da Agência ---
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 4;
        mainPanel.add(new JLabel("<html><b>Dados da Agência:</b></html>"), gbc);
        row++; 
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = row; mainPanel.add(new JLabel("Código da Agência:"), gbc);
        gbc.gridx = 1; gbc.gridy = row; txtAgencia = new JTextField(15); mainPanel.add(txtAgencia, gbc);
        row++;


        // --- Painel de Campos Específicos do Tipo de Conta ---
        panelTipoContaCampos = new JPanel(new GridBagLayout()); 
        panelTipoContaCampos.setBorder(BorderFactory.createTitledBorder("Dados Específicos do Tipo de Conta")); 
        
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 4;
        gbc.weightx = 1.0; 
        mainPanel.add(panelTipoContaCampos, gbc);
        row++; 

        atualizarCamposTipoConta("POUPANCA");

        // --- Botões de Ação ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10)); 
        JButton btnAbrirConta = new JButton("Abrir Conta");
        JButton btnCancelar = new JButton("Cancelar");

        btnAbrirConta.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirConta();
            }
        });

        btnCancelar.addActionListener(e -> dispose()); 

        buttonPanel.add(btnAbrirConta);
        buttonPanel.add(btnCancelar);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void atualizarCamposTipoConta(String tipo) {
        panelTipoContaCampos.removeAll(); 
        GridBagConstraints gbcLocal = new GridBagConstraints(); 
        gbcLocal.insets = new Insets(5, 5, 5, 5);
        gbcLocal.fill = GridBagConstraints.HORIZONTAL;
        gbcLocal.weightx = 1.0; 

        int subRow = 0; 

        switch (tipo) {
            case "POUPANCA":
                gbcLocal.gridx = 0; gbcLocal.gridy = subRow; panelTipoContaCampos.add(new JLabel("Taxa de Rendimento (%):"), gbcLocal);
                gbcLocal.gridx = 1; gbcLocal.gridy = subRow; txtTaxaRendimentoCP = new JTextField(10); panelTipoContaCampos.add(txtTaxaRendimentoCP, gbcLocal);
                subRow++;
                break;
            case "CORRENTE":
                gbcLocal.gridx = 0; gbcLocal.gridy = subRow; panelTipoContaCampos.add(new JLabel("Limite (R$):"), gbcLocal);
                gbcLocal.gridx = 1; gbcLocal.gridy = subRow; txtLimiteCC = new JTextField(10); panelTipoContaCampos.add(txtLimiteCC, gbcLocal);
                subRow++;

                gbcLocal.gridx = 0; gbcLocal.gridy = subRow; panelTipoContaCampos.add(new JLabel("Data Vencimento (dd/mm/aaaa):"), gbcLocal);
                try {
                    MaskFormatter dataMask = new MaskFormatter("##/##/####");
                    txtDataVencimentoCC = new JFormattedTextField(dataMask);
                    txtDataVencimentoCC.setColumns(10);
                } catch (java.text.ParseException e) {
                    txtDataVencimentoCC = new JFormattedTextField();
                    System.err.println("Erro ao criar máscara de Data de Vencimento: " + e.getMessage());
                }
                gbcLocal.gridx = 1; gbcLocal.gridy = subRow; panelTipoContaCampos.add(txtDataVencimentoCC, gbcLocal);
                subRow++;

                gbcLocal.gridx = 0; gbcLocal.gridy = subRow; panelTipoContaCampos.add(new JLabel("Taxa Manutenção (R$):"), gbcLocal);
                gbcLocal.gridx = 1; gbcLocal.gridy = subRow; txtTaxaManutencaoCC = new JTextField(10); panelTipoContaCampos.add(txtTaxaManutencaoCC, gbcLocal);
                subRow++;
                break;
            case "INVESTIMENTO":
                gbcLocal.gridx = 0; gbcLocal.gridy = subRow; panelTipoContaCampos.add(new JLabel("Perfil de Risco:"), gbcLocal);
                String[] perfis = {"BAIXO", "MEDIO", "ALTO"};
                cmbPerfilRiscoCI = new JComboBox<>(perfis);
                gbcLocal.gridx = 1; gbcLocal.gridy = subRow; panelTipoContaCampos.add(cmbPerfilRiscoCI, gbcLocal);
                subRow++;

                gbcLocal.gridx = 0; gbcLocal.gridy = subRow; panelTipoContaCampos.add(new JLabel("Valor Mínimo (R$):"), gbcLocal);
                gbcLocal.gridx = 1; gbcLocal.gridy = subRow; txtValorMinimoCI = new JTextField(10); panelTipoContaCampos.add(txtValorMinimoCI, gbcLocal);
                subRow++;

                gbcLocal.gridx = 0; gbcLocal.gridy = subRow; panelTipoContaCampos.add(new JLabel("Taxa Rendimento Base (%):"), gbcLocal);
                gbcLocal.gridx = 1; gbcLocal.gridy = subRow; txtTaxaRendimentoBaseCI = new JTextField(10); panelTipoContaCampos.add(txtTaxaRendimentoBaseCI, gbcLocal);
                subRow++;
                break;
        }
        panelTipoContaCampos.revalidate(); 
        panelTipoContaCampos.repaint(); 
    }

    private void abrirConta() {
        String tipoConta = "";
        if (rbPoupanca.isSelected()) tipoConta = "POUPANCA";
        else if (rbCorrente.isSelected()) tipoConta = "CORRENTE";
        else if (rbInvestimento.isSelected()) tipoConta = "INVESTIMENTO";

        String nome = txtNome.getText().trim();
        String cpf = txtCpf.getText().replaceAll("[^0-9]", "").trim();
        LocalDate dataNascimento = null;
        try {
            if (txtDataNascimento.getText().replaceAll("[^0-9]", "").isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, preencha a Data de Nascimento.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                return;
            }
            dataNascimento = LocalDate.parse(txtDataNascimento.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Formato de Data de Nascimento inválido. Use dd/mm/aaaa.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String telefone = txtTelefone.getText().replaceAll("[^0-9]", "").trim(); 
        String senha = new String(txtSenha.getPassword());

        String cep = txtCep.getText().replaceAll("[^0-9]", "").trim(); 
        String logradouro = txtLogradouro.getText().trim();

        String bairro = txtBairro.getText().trim();
        String cidade = txtCidade.getText().trim();
        String estado = (String) cmbEstado.getSelectedItem();
        String complemento = txtComplemento.getText().trim();

        String codigoAgencia = txtAgencia.getText().trim(); // Usar codigoAgencia

     
        int numeroCasa = 0;
        double taxaRendimentoCP = 0.0;
        BigDecimal limiteCC = BigDecimal.ZERO; // ✅ CORRIGIDO: Inicialização de BigDecimal
        LocalDate dataVencimentoCC = null;
        double taxaManutencaoCC = 0.0; // Será convertida para BigDecimal antes de passar ao Controller
        String perfilRiscoCI = "";
        double valorMinimoCI = 0.0; // Será convertido para BigDecimal antes de passar ao Controller
        double taxaRendimentoBaseCI = 0.0; // Será convertido para BigDecimal antes de passar ao Controller

        try {
            switch (tipoConta) {
                case "POUPANCA":
                    if (txtTaxaRendimentoCP.getText().isEmpty()) {
                         JOptionPane.showMessageDialog(this, "Por favor, preencha a Taxa de Rendimento.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
                         return;
                    }
                    taxaRendimentoCP = Double.parseDouble(txtTaxaRendimentoCP.getText().replace(",", "."));
                    break;
                case "CORRENTE":
                    if (txtLimiteCC.getText().isEmpty()) {
                         JOptionPane.showMessageDialog(this, "Por favor, preencha o Limite.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
                         return;
                    }
                    // ✅ CORRIGIDO: Leitura de Limite como BigDecimal
                    limiteCC = new BigDecimal(txtLimiteCC.getText().replace(",", "."));
                    if (txtDataVencimentoCC.getText().replaceAll("[^0-9]", "").isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Por favor, preencha a Data de Vencimento.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    try {
                        dataVencimentoCC = LocalDate.parse(txtDataVencimentoCC.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    } catch (DateTimeParseException e) {
                        JOptionPane.showMessageDialog(this, "Formato de Data de Vencimento inválido. Use dd/mm/aaaa.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (txtTaxaManutencaoCC.getText().isEmpty()) {
                         JOptionPane.showMessageDialog(this, "Por favor, preencha a Taxa de Manutenção.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
                         return;
                    }
                    taxaManutencaoCC = Double.parseDouble(txtTaxaManutencaoCC.getText().replace(",", ".")); // Passado como double para o Controller
                    break;
                case "INVESTIMENTO":
                    perfilRiscoCI = (String) Objects.requireNonNull(cmbPerfilRiscoCI.getSelectedItem());
                    if (txtValorMinimoCI.getText().isEmpty()) {
                         JOptionPane.showMessageDialog(this, "Por favor, preencha o Valor Mínimo.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
                         return;
                    }
                    // ✅ CORRIGIDO: Leitura de Valor Mínimo como double
                    valorMinimoCI = Double.parseDouble(txtValorMinimoCI.getText().replace(",", "."));
                    if (txtTaxaRendimentoBaseCI.getText().isEmpty()) {
                         JOptionPane.showMessageDialog(this, "Por favor, preencha a Taxa de Rendimento Base.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
                         return;
                    }
                    // ✅ CORRIGIDO: Leitura de Taxa Rendimento Base como double
                    taxaRendimentoBaseCI = Double.parseDouble(txtTaxaRendimentoBaseCI.getText().replace(",", "."));
                    break;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Valores numéricos inválidos nos campos específicos da conta. Por favor, use apenas números e ponto/vírgula para decimais.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // ✅ CORREÇÃO FINAL DOS PARÂMETROS PARA O CONTROLLER
        boolean sucesso = controller.abrirConta(nome, cpf, dataNascimento, telefone, senha, cep, logradouro,
        									   numeroCasa, bairro, cidade, estado, complemento, codigoAgencia, tipoConta,
                                               taxaRendimentoCP, limiteCC, dataVencimentoCC, taxaManutencaoCC, // limiteCC já é BigDecimal
                                               perfilRiscoCI, valorMinimoCI, taxaRendimentoBaseCI, idFuncionarioResponsavel);

        if (sucesso) {
            dispose(); 
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame(); 
            frame.setTitle("Main Window Test");
            frame.setSize(400, 300);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            AbrirConta dialog = new AbrirConta(frame); 
            dialog.setVisible(true); 
        });
    }
}
