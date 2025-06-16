package com.bancomalvader.view;

import javax.swing.*;
import javax.swing.text.MaskFormatter;

import com.bancomalvader.model.Cliente;
import com.bancomalvader.model.Conta;
import com.bancomalvader.model.ContaCorrente;
import com.bancomalvader.model.ContaInvestimento;
import com.bancomalvader.model.ContaPoupanca;
import com.bancomalvader.model.Funcionario;
import com.bancomalvader.model.Usuario;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.math.BigDecimal;
import java.util.Objects;

// Esta View será um JDialog para coletar dados para alteração
public class AlterarDadosView extends JDialog {

    private JComboBox<String> cmbTipoAlteracao; // Conta, Funcionário, Cliente
    private JTextField txtCriterioBusca; // CPF, Número da Conta, Código Funcionário
    private JButton btnBuscar;

    // Painel para exibir os campos de alteração específicos (dinâmico)
    private JPanel panelCamposAlteracao;

    // Campos de Alteração (declarados aqui para serem acessíveis pelos getters)
    // Campos de Usuário/Cliente/Funcionário
    private JTextField txtNomeUsuario; // Apenas para funcionário/cliente - nome não é alterável via SRS
    private JTextField txtTelefoneUsuario;
    private JPasswordField txtNovaSenhaUsuario;
    private JFormattedTextField txtCepUsuario;
    private JTextField txtLogradouroUsuario;
    private JTextField txtNumeroCasaUsuario;
    private JTextField txtBairroUsuario;
    private JTextField txtCidadeUsuario;
    private JComboBox<String> cmbEstadoUsuario;
    private JTextField txtComplementoUsuario;

    // Campos de Funcionário específico
    private JTextField txtCargoFuncionario;
    private JTextField txtCodigoFuncionario; // Não alterável, apenas para exibição
    private JTextField txtIdSupervisorFuncionario; // Não alterável, apenas para exibição ou para buscar um novo

    // Campos de Conta específico
    private JTextField txtSaldoConta; // Não alterável, apenas exibição
    private JTextField txtLimiteCC;
    private JFormattedTextField txtDataVencimentoCC;
    private JTextField txtTaxaManutencaoCC;
    private JTextField txtTaxaRendimentoCP;
    private JTextField txtValorMinimoCI;
    private JTextField txtTaxaRendimentoBaseCI;
    private JComboBox<String> cmbPerfilRiscoCI;
    private JLabel lblTipoConta; // Para exibir o tipo de conta da conta selecionada

    private JButton btnSalvar;
    private JButton btnCancelar;

    private String tipoAlteracaoSelecionado; // Para o Controller saber qual tipo de objeto alterar
    private int idObjetoParaAlterar; // ID do objeto (conta, funcionario, cliente) que está sendo alterado

    public AlterarDadosView(Frame owner) {
        super(owner, "Alterar Dados - Banco Malvader", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(800, 700);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // Painel de entrada de busca
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        cmbTipoAlteracao = new JComboBox<>(new String[]{"Conta", "Funcionário", "Cliente"});
        txtCriterioBusca = new JTextField(20);
        btnBuscar = new JButton("Buscar");

        searchPanel.add(new JLabel("Alterar dados de:"));
        searchPanel.add(cmbTipoAlteracao);
        searchPanel.add(new JLabel("Critério (CPF/Número/Código):"));
        searchPanel.add(txtCriterioBusca);
        searchPanel.add(btnBuscar);
        add(searchPanel, BorderLayout.NORTH);

        // Painel central para os campos de alteração dinâmicos
        panelCamposAlteracao = new JPanel(new GridBagLayout());
        panelCamposAlteracao.setBorder(BorderFactory.createTitledBorder("Dados para Alteração"));
        JScrollPane scrollPane = new JScrollPane(panelCamposAlteracao);
        add(scrollPane, BorderLayout.CENTER);

        // Painel de botões Salvar/Cancelar
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnSalvar = new JButton("Salvar Alterações");
        btnCancelar = new JButton("Cancelar");
        buttonPanel.add(btnSalvar);
        buttonPanel.add(btnCancelar);
        add(buttonPanel, BorderLayout.SOUTH);

        // Ações dos botões serão configuradas pelo Controller
        // btnBuscar.addActionListener(...);
        // btnSalvar.addActionListener(...);
        // btnCancelar.addActionListener(...);
    }

    // Métodos para o Controller definir os campos de alteração
    public void setupCamposAlteracao(String tipo, Object dados) {
        panelCamposAlteracao.removeAll(); // Limpa painel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        int row = 0;

        // Common fields for all types:
        if (dados instanceof Usuario) { // For Client/Employee
            Usuario user = (Usuario) dados;
            // SRS: Cliente: Alterar telefone e senha
            // SRS: Funcionário: Alterar telefone
            txtTelefoneUsuario = new JTextField(user.getTelefone());
            txtNovaSenhaUsuario = new JPasswordField(""); // Senha não exibida

            gbc.gridx = 0; gbc.gridy = row; panelCamposAlteracao.add(new JLabel("Telefone:"), gbc);
            gbc.gridx = 1; gbc.gridy = row; panelCamposAlteracao.add(txtTelefoneUsuario, gbc);
            row++;
            
            gbc.gridx = 0; gbc.gridy = row; panelCamposAlteracao.add(new JLabel("Nova Senha:"), gbc);
            gbc.gridx = 1; gbc.gridy = row; panelCamposAlteracao.add(txtNovaSenhaUsuario, gbc);
            row++;

            // SRS: Cliente: Alterar endereço. Funcionário também tem endereço.
            // Para isso, você precisará carregar o Endereco associado ao Usuario.
            // Aqui, apenas placeholders para os campos do Endereço
            txtCepUsuario = new JFormattedTextField(); try { MaskFormatter cepMask = new MaskFormatter("#####-###"); txtCepUsuario = new JFormattedTextField(cepMask); } catch (java.text.ParseException e) {}
            txtLogradouroUsuario = new JTextField();
            txtNumeroCasaUsuario = new JTextField();
            txtBairroUsuario = new JTextField();
            txtCidadeUsuario = new JTextField();
            cmbEstadoUsuario = new JComboBox<>(new String[]{"AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI", "RJ", "RN", "RO", "RR", "RS", "SC", "SP", "SE", "TO"});
            txtComplementoUsuario = new JTextField();

            if (dados instanceof Cliente || dados instanceof Funcionario) { // Assuming address is linked via Usuario ID
                // You'd need to load the Endereco object separately in the controller
                // and then populate these fields. For now, they are empty.
                gbc.gridwidth = 2; // Span across two columns for titles
                panelCamposAlteracao.add(new JSeparator(), gbc);
                row++;
                panelCamposAlteracao.add(new JLabel("<html><b>Endereço:</b></html>"), gbc);
                row++;
                gbc.gridwidth = 1; // Reset width

                gbc.gridx = 0; gbc.gridy = row; panelCamposAlteracao.add(new JLabel("CEP:"), gbc);
                gbc.gridx = 1; gbc.gridy = row; panelCamposAlteracao.add(txtCepUsuario, gbc);
                row++;
                gbc.gridx = 0; gbc.gridy = row; panelCamposAlteracao.add(new JLabel("Logradouro:"), gbc);
                gbc.gridx = 1; gbc.gridy = row; panelCamposAlteracao.add(txtLogradouroUsuario, gbc);
                row++;
                gbc.gridx = 0; gbc.gridy = row; panelCamposAlteracao.add(new JLabel("Número:"), gbc);
                gbc.gridx = 1; gbc.gridy = row; panelCamposAlteracao.add(txtNumeroCasaUsuario, gbc);
                row++;
                gbc.gridx = 0; gbc.gridy = row; panelCamposAlteracao.add(new JLabel("Bairro:"), gbc);
                gbc.gridx = 1; gbc.gridy = row; panelCamposAlteracao.add(txtBairroUsuario, gbc);
                row++;
                gbc.gridx = 0; gbc.gridy = row; panelCamposAlteracao.add(new JLabel("Cidade:"), gbc);
                gbc.gridx = 1; gbc.gridy = row; panelCamposAlteracao.add(txtCidadeUsuario, gbc);
                row++;
                gbc.gridx = 0; gbc.gridy = row; panelCamposAlteracao.add(new JLabel("Estado:"), gbc);
                gbc.gridx = 1; gbc.gridy = row; panelCamposAlteracao.add(cmbEstadoUsuario, gbc);
                row++;
                gbc.gridx = 0; gbc.gridy = row; panelCamposAlteracao.add(new JLabel("Complemento:"), gbc);
                gbc.gridx = 1; gbc.gridy = row; panelCamposAlteracao.add(txtComplementoUsuario, gbc);
                row++;
            }
        }
        
        if (tipo.equals("Funcionário") && dados instanceof Funcionario) {
            Funcionario func = (Funcionario) dados;
            // SRS: Funcionário: Alterar cargo (com restrição de nível hierárquico)
            txtCargoFuncionario = new JTextField(func.getCargo());
            // txtIdSupervisorFuncionario = new JTextField(func.getIdSupervisor() != null ? String.valueOf(func.getIdSupervisor()) : "");

            gbc.gridwidth = 2;
            panelCamposAlteracao.add(new JSeparator(), gbc);
            row++;
            panelCamposAlteracao.add(new JLabel("<html><b>Dados do Funcionário:</b></html>"), gbc);
            row++;
            gbc.gridwidth = 1;

            gbc.gridx = 0; gbc.gridy = row; panelCamposAlteracao.add(new JLabel("Cargo:"), gbc);
            gbc.gridx = 1; gbc.gridy = row; panelCamposAlteracao.add(txtCargoFuncionario, gbc);
            row++;
            // TODO: Adicionar campo para alterar supervisor, talvez com ComboBox de funcionários existentes
        } else if (tipo.equals("Conta") && dados instanceof Conta) {
            Conta conta = (Conta) dados;
            // SRS: Conta: Alterar limite (com validação de score de crédito), data de vencimento e taxa de rendimento/manutenção.
            lblTipoConta = new JLabel("Tipo de Conta: " + conta.getTipoConta());
            txtSaldoConta = new JTextField(String.format("%.2f", conta.getSaldo()));
            txtSaldoConta.setEditable(false); // Saldo não é alterável diretamente

            gbc.gridwidth = 2;
            panelCamposAlteracao.add(new JSeparator(), gbc);
            row++;
            panelCamposAlteracao.add(new JLabel("<html><b>Dados da Conta:</b></html>"), gbc);
            row++;
            gbc.gridwidth = 1;

            gbc.gridx = 0; gbc.gridy = row; panelCamposAlteracao.add(new JLabel("Tipo de Conta:"), gbc);
            gbc.gridx = 1; gbc.gridy = row; panelCamposAlteracao.add(lblTipoConta, gbc);
            row++;
            gbc.gridx = 0; gbc.gridy = row; panelCamposAlteracao.add(new JLabel("Saldo Atual:"), gbc);
            gbc.gridx = 1; gbc.gridy = row; panelCamposAlteracao.add(txtSaldoConta, gbc);
            row++;

            if ("CORRENTE".equals(conta.getTipoConta())) {
                // Supondo que você passou ContaCorrente em 'dados' ou buscou aqui
                ContaCorrente cc = null;
                if (dados instanceof ContaCorrente) {
                    cc = (ContaCorrente) dados;
                }
                txtLimiteCC = new JTextField(cc != null ? String.format("%.2f", cc.getLimite()) : "");
                txtDataVencimentoCC = new JFormattedTextField();
                try { MaskFormatter dataMask = new MaskFormatter("##/##/####"); txtDataVencimentoCC = new JFormattedTextField(dataMask); } catch (java.text.ParseException e) {}
                if (cc != null && cc.getDataVencimento() != null) {
                    txtDataVencimentoCC.setText(cc.getDataVencimento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                }
                txtTaxaManutencaoCC = new JTextField(cc != null ? String.format("%.2f", cc.getTaxaManutencao()) : "");

                gbc.gridx = 0; gbc.gridy = row; panelCamposAlteracao.add(new JLabel("Limite (R$):"), gbc);
                gbc.gridx = 1; gbc.gridy = row; panelCamposAlteracao.add(txtLimiteCC, gbc);
                row++;
                gbc.gridx = 0; gbc.gridy = row; panelCamposAlteracao.add(new JLabel("Data Vencimento:"), gbc);
                gbc.gridx = 1; gbc.gridy = row; panelCamposAlteracao.add(txtDataVencimentoCC, gbc);
                row++;
                gbc.gridx = 0; gbc.gridy = row; panelCamposAlteracao.add(new JLabel("Taxa Manutenção (R$):"), gbc);
                gbc.gridx = 1; gbc.gridy = row; panelCamposAlteracao.add(txtTaxaManutencaoCC, gbc);
                row++;

            } else if ("POUPANCA".equals(conta.getTipoConta())) {
                ContaPoupanca cp = null;
                if (dados instanceof ContaPoupanca) {
                    cp = (ContaPoupanca) dados;
                }
                txtTaxaRendimentoCP = new JTextField(cp != null ? String.format("%.2f", cp.getTaxaRendimento()) : "");
                
                gbc.gridx = 0; gbc.gridy = row; panelCamposAlteracao.add(new JLabel("Taxa Rendimento (%):"), gbc);
                gbc.gridx = 1; gbc.gridy = row; panelCamposAlteracao.add(txtTaxaRendimentoCP, gbc);
                row++;

            } else if ("INVESTIMENTO".equals(conta.getTipoConta())) {
                ContaInvestimento ci = null;
                if (dados instanceof ContaInvestimento) {
                    ci = (ContaInvestimento) dados;
                }
                cmbPerfilRiscoCI = new JComboBox<>(new String[]{"BAIXO", "MEDIO", "ALTO"});
                if (ci != null && ci.getPerfilRisco() != null) {
                    cmbPerfilRiscoCI.setSelectedItem(ci.getPerfilRisco());
                }
                txtValorMinimoCI = new JTextField(ci != null ? String.format("%.2f", ci.getValorMinimo()) : "");
                txtTaxaRendimentoBaseCI = new JTextField(ci != null ? String.format("%.2f", ci.getTaxaRendimentoBase()) : "");

                gbc.gridx = 0; gbc.gridy = row; panelCamposAlteracao.add(new JLabel("Perfil de Risco:"), gbc);
                gbc.gridx = 1; gbc.gridy = row; panelCamposAlteracao.add(cmbPerfilRiscoCI, gbc);
                row++;
                gbc.gridx = 0; gbc.gridy = row; panelCamposAlteracao.add(new JLabel("Valor Mínimo (R$):"), gbc);
                gbc.gridx = 1; gbc.gridy = row; panelCamposAlteracao.add(txtValorMinimoCI, gbc);
                row++;
                gbc.gridx = 0; gbc.gridy = row; panelCamposAlteracao.add(new JLabel("Taxa Rendimento Base (%):"), gbc);
                gbc.gridx = 1; gbc.gridy = row; panelCamposAlteracao.add(txtTaxaRendimentoBaseCI, gbc);
                row++;
            }
        }
        panelCamposAlteracao.revalidate();
        panelCamposAlteracao.repaint();
    }

    // Getters para os campos de alteração
    // (O Controller vai ler esses valores quando o botão Salvar for clicado)
    public JTextField getTxtTelefoneUsuario() { return txtTelefoneUsuario; }
    public JPasswordField getTxtNovaSenhaUsuario() { return txtNovaSenhaUsuario; }
    public JFormattedTextField getTxtCepUsuario() { return txtCepUsuario; }
    public JTextField getTxtLogradouroUsuario() { return txtLogradouroUsuario; }
    public JTextField getTxtNumeroCasaUsuario() { return txtNumeroCasaUsuario; }
    public JTextField getTxtBairroUsuario() { return txtBairroUsuario; }
    public JTextField getTxtCidadeUsuario() { return txtCidadeUsuario; }
    public JComboBox<String> getCmbEstadoUsuario() { return cmbEstadoUsuario; }
    public JTextField getTxtComplementoUsuario() { return txtComplementoUsuario; }
    public JTextField getTxtCargoFuncionario() { return txtCargoFuncionario; }
    public JTextField getTxtLimiteCC() { return txtLimiteCC; }
    public JFormattedTextField getTxtDataVencimentoCC() { return txtDataVencimentoCC; }
    public JTextField getTxtTaxaManutencaoCC() { return txtTaxaManutencaoCC; }
    public JTextField getTxtTaxaRendimentoCP() { return txtTaxaRendimentoCP; }
    public JTextField getTxtValorMinimoCI() { return txtValorMinimoCI; }
    public JTextField getTxtTaxaRendimentoBaseCI() { return txtTaxaRendimentoBaseCI; }
    public JComboBox<String> getCmbPerfilRiscoCI() { return cmbPerfilRiscoCI; }

    // Getters para os componentes de controle
    public JComboBox<String> getCmbTipoAlteracao() { return cmbTipoAlteracao; }
    public JTextField getTxtCriterioBusca() { return txtCriterioBusca; }
    public JButton getBtnBuscar() { return btnBuscar; }
    public JButton getBtnSalvar() { return btnSalvar; }
    public JButton getBtnCancelar() { return btnCancelar; }

    public void setIdObjetoParaAlterar(int id) { this.idObjetoParaAlterar = id; }
    public int getIdObjetoParaAlterar() { return idObjetoParaAlterar; }

    public void setTipoAlteracaoSelecionado(String tipo) { this.tipoAlteracaoSelecionado = tipo; }
    public String getTipoAlteracaoSelecionado() { return tipoAlteracaoSelecionado; }
}
