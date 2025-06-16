package com.bancomalvader.controller;

import com.bancomalvader.dao.ContaDAO;
import com.bancomalvader.dao.EnderecoDAO;
import com.bancomalvader.dao.FuncionarioDAO;
import com.bancomalvader.dao.RelatorioDAO;
import com.bancomalvader.dao.TransacaoDAO;
import com.bancomalvader.dao.UsuarioDAO; 
import com.bancomalvader.dao.ClienteDAO; 
import com.bancomalvader.dao.AuditoriaDAO; 

import com.bancomalvader.model.Funcionario;
import com.bancomalvader.model.Usuario; 
import com.bancomalvader.model.Cliente; 
import com.bancomalvader.model.Conta; 
import com.bancomalvader.model.Auditoria; 
import com.bancomalvader.model.ContaCorrente; 
import com.bancomalvader.model.ContaPoupanca; 
import com.bancomalvader.model.ContaInvestimento; 
import com.bancomalvader.model.Endereco; 

import com.bancomalvader.model.Transacao; 

import com.bancomalvader.util.ConexaoBanco; 
import com.bancomalvader.util.CpfValidator; 
import com.bancomalvader.util.PasswordHasher; 

import com.bancomalvader.view.AbrirConta; 
import com.bancomalvader.view.CadastrarUsuarioView;
import com.bancomalvader.view.ConsultarDados;
import com.bancomalvader.view.EncerrarContaView; // Correção do nome da View
import com.bancomalvader.view.AlterarDadosView; 
import com.bancomalvader.view.GerarRelatoriosView; 
import com.bancomalvader.view.LoginView; 
import com.bancomalvader.view.TelaPrincipalFuncionarioView; 

import javax.swing.*;
import javax.swing.table.DefaultTableModel; 
import java.awt.Frame; 
import java.awt.Window;
import java.math.BigDecimal;
import java.sql.Connection; 
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime; 
import java.time.format.DateTimeFormatter; 
import java.time.format.DateTimeParseException; 
import java.util.List;
import java.util.Map; 

public class TelaPrincipalFuncionarioController {

    private final TelaPrincipalFuncionarioView view;
    private final Funcionario funcionarioLogado; 

    // DAOs
    private final ContaDAO contaDAO;
    private final FuncionarioDAO funcionarioDAO;
    private final RelatorioDAO relatorioDAO;
    private final UsuarioDAO usuarioDAO; 
    private final ClienteDAO clienteDAO; 
    private final AuditoriaDAO auditoriaDAO; 

    public TelaPrincipalFuncionarioController(TelaPrincipalFuncionarioView view, Funcionario funcionarioLogado) {
        System.out.println("TelaPrincipalFuncionarioController: Construtor iniciado.");
        this.view = view;
        this.funcionarioLogado = funcionarioLogado;

        this.contaDAO = new ContaDAO();
        this.funcionarioDAO = new FuncionarioDAO();
        this.relatorioDAO = new RelatorioDAO();
        this.usuarioDAO = new UsuarioDAO(); 
        this.clienteDAO = new ClienteDAO(); 
        this.auditoriaDAO = new AuditoriaDAO(); 

        configurarAcoes();
        System.out.println("TelaPrincipalFuncionarioController: Construtor finalizado. Ações configuradas.");
    }

    private void configurarAcoes() {
        System.out.println("TelaPrincipalFuncionarioController: Configurando ações dos botões.");
        Frame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(view); 

        view.getBtnAbrirConta().addActionListener(e -> {
            System.out.println("Botão Abrir Conta clicado.");
            new AbrirConta(parentFrame).setVisible(true);
        });

        view.getBtnEncerrarConta().addActionListener(e -> encerrarConta());

        view.getBtnConsultarDados().addActionListener(e -> consultarDados());

        view.getBtnCadastrarFuncionario().addActionListener(e -> cadastrarUsuario(parentFrame)); 

        view.getBtnRelatorios().addActionListener(e -> gerarRelatorio());
        
        view.getBtnSair().addActionListener(e -> {
            System.out.println("Botão Sair clicado.");
            Window ancestralWindow = SwingUtilities.getWindowAncestor(view); 
            if (ancestralWindow instanceof JFrame) { 
                ((JFrame) ancestralWindow).dispose(); 
            }
            new LoginView().setVisible(true); 
        });
        System.out.println("TelaPrincipalFuncionarioController: Ações dos botões configuradas com sucesso.");
    }

    private void encerrarConta() {
        System.out.println("TelaPrincipalFuncionarioController: Executando encerrarConta()."); 
        Frame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(view); 

        EncerrarContaView encerrarView = new EncerrarContaView(parentFrame);
        encerrarView.setVisible(true); 

        if (!encerrarView.isConfirmacao()) { 
            System.out.println("Encerramento de conta cancelado pelo usuário.");
            return;
        }

        String numeroConta = encerrarView.getNumeroConta();
        String senhaAdmin = encerrarView.getSenhaAdmin();
        String otp = encerrarView.getOtp();
        String motivo = encerrarView.getMotivo();

        if (numeroConta.isEmpty() || senhaAdmin.isEmpty() || otp.isEmpty() || motivo.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Todos os campos são obrigatórios para encerrar a conta.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!"admin123".equals(senhaAdmin)) { 
            JOptionPane.showMessageDialog(view, "Senha de administrador incorreta.", "Acesso negado", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Connection conn = null; 
        try {
            conn = ConexaoBanco.getConnection();
            conn.setAutoCommit(false); 

            Conta conta = contaDAO.buscarContaPorNumero(numeroConta.trim());
            if (conta == null) {
                throw new SQLException("Conta com número " + numeroConta + " não encontrada.");
            }
            if (!"ATIVA".equals(conta.getStatus())) { 
                throw new SQLException("Conta " + numeroConta + " não está ATIVA. Status atual: " + conta.getStatus());
            }
            if (conta.getSaldo() != 0.0) { 
                throw new SQLException("Conta " + numeroConta + " possui saldo diferente de zero. Saldo atual: R$" + String.format("%.2f", conta.getSaldo()));
            }

            auditoriaDAO.registrarAuditoria(new Auditoria(funcionarioLogado.getIdUsuario(), "ENCERRAR_CONTA", LocalDateTime.now(), motivo), conn); // Usando o motivo para auditoria

            conn.commit(); 
            JOptionPane.showMessageDialog(view, "Conta " + numeroConta + " encerrada com sucesso!", "Encerramento", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback(); 
                } catch (SQLException rollbackEx) {
                    System.err.println("Erro ao fazer rollback: " + rollbackEx.getMessage());
                }
            }
            mostrarErro("Erro ao encerrar conta", ex); 
        } finally {
            ConexaoBanco.closeConnection(conn); 
        }
    }

    private void consultarDados() {
        System.out.println("TelaPrincipalFuncionarioController: Executando consultarDados().");
        Frame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(view);

        ConsultarDados consultarView = new ConsultarDados(parentFrame); // Use ConsultarDadosView
        consultarView.setVisible(true);

        consultarView.getBtnBuscar().addActionListener(e -> {
            String tipoConsulta = (String) consultarView.getCmbTipoConsulta().getSelectedItem();
            String criterio = consultarView.getTxtCriterioBusca().getText().trim();

            if (criterio.isEmpty()) {
                JOptionPane.showMessageDialog(consultarView, "Por favor, digite um critério de busca.", "Erro", JOptionPane.WARNING_MESSAGE);
                return;
            }

            consultarView.getTableModel().setColumnCount(0);
            consultarView.getTableModel().setRowCount(0);

            try {
                switch (tipoConsulta) {
                    case "Conta":
                        Conta conta = contaDAO.buscarContaPorNumero(criterio);
                        if (conta != null) {
                            consultarView.getTableModel().setColumnIdentifiers(new String[]{"ID Conta", "Número", "Tipo", "Saldo", "Status", "Cliente ID", "Agência ID", "Data Abertura"});
                            consultarView.getTableModel().addRow(new Object[]{
                                conta.getIdConta(), conta.getNumeroConta(), conta.getTipoConta(),
                                String.format("R$ %.2f", conta.getSaldo()), conta.getStatus(),
                                conta.getIdCliente(), conta.getIdAgencia(), conta.getDataAbertura().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                            });

                            if ("POUPANCA".equals(conta.getTipoConta())) {
                                ContaPoupanca cp = contaDAO.buscarContaPoupancaPorIdConta(conta.getIdConta());
                                if (cp != null) {
                                    consultarView.getTableModel().addRow(new Object[]{"Taxa Rendimento CP:", String.format("%.2f%%", cp.getTaxaRendimento()), "Último Rendimento:", cp.getUltimoRendimento() != null ? cp.getUltimoRendimento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "N/A"});
                                }
                            } else if ("CORRENTE".equals(conta.getTipoConta())) {
                                ContaCorrente cc = contaDAO.buscarContaCorrentePorIdConta(conta.getIdConta());
                                if (cc != null) {
                                    consultarView.getTableModel().addRow(new Object[]{"Limite CC:", String.format("R$ %.2f", cc.getLimite()), "Data Venc. CC:", cc.getDataVencimento() != null ? cc.getDataVencimento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A", "Taxa Manut. CC:", String.format("R$ %.2f", cc.getTaxaManutencao())});
                                }
                            } else if ("INVESTIMENTO".equals(conta.getTipoConta())) {
                                ContaInvestimento ci = contaDAO.buscarContaInvestimentoPorIdConta(conta.getIdConta());
                                if (ci != null) {
                                    consultarView.getTableModel().addRow(new Object[]{"Perfil Risco CI:", ci.getPerfilRisco(), "Valor Mínimo CI:", String.format("R$ %.2f", ci.getValorMinimo()), "Taxa Rendimento Base CI:", String.format("%.2f%%", ci.getTaxaRendimentoBase())});
                                }
                            }
                            TransacaoDAO transacaoDAO = new TransacaoDAO();
                            List<Transacao> extratoTransacoes = transacaoDAO.buscarUltimasTransacoesPorConta(conta.getIdConta(), 10); 
                            if (!extratoTransacoes.isEmpty()) {
                                consultarView.getTableModel().addRow(new Object[]{"", "", "", "", "", "", "", ""}); 
                                consultarView.getTableModel().addRow(new Object[]{"Extrato Recente:", "", "", "", "", "", "", ""});
                                consultarView.getTableModel().addRow(new String[]{"Data", "Tipo", "Valor", "Descrição", "Origem", "Destino", "", ""}); 
                                for (Transacao t : extratoTransacoes) {
                                    consultarView.getTableModel().addRow(new Object[]{
                                        t.getDataHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                                        t.getTipoTransacao(),
                                        String.format("R$ %.2f", t.getValor()),
                                        t.getDescricao(),
                                        t.getIdContaOrigem(),
                                        t.getIdContaDestino() != null ? t.getIdContaDestino() : "N/A"
                                    });
                                }
                            }

                        } else {
                            JOptionPane.showMessageDialog(consultarView, "Conta não encontrada.", "Resultado", JOptionPane.INFORMATION_MESSAGE);
                        }
                        break;

                    case "Funcionário":
                        Funcionario funcionario = funcionarioDAO.buscarFuncionarioPorCodigo(criterio);
                        if (funcionario == null) {
                            if (CpfValidator.isValidFormat(criterio)) {
                                Usuario usuarioFunc = usuarioDAO.buscarUsuarioPorCpfETipo(criterio, "FUNCIONARIO");
                                if (usuarioFunc != null) {
                                    funcionario = funcionarioDAO.buscarFuncionarioPorUsuarioId(usuarioFunc.getIdUsuario());
                                }
                            }
                        }

                        if (funcionario != null) {
                            Usuario usuarioAssociado = usuarioDAO.buscarUsuarioPorId(funcionario.getIdUsuario());
                            consultarView.getTableModel().setColumnIdentifiers(new String[]{"Campo", "Valor"});
                            consultarView.getTableModel().addRow(new Object[]{"ID Funcionário:", funcionario.getIdFuncionario()});
                            consultarView.getTableModel().addRow(new Object[]{"Código:", funcionario.getCodigoFuncionario()});
                            consultarView.getTableModel().addRow(new Object[]{"Cargo:", funcionario.getCargo()});
                            consultarView.getTableModel().addRow(new Object[]{"Supervisor ID:", funcionario.getIdSupervisor() != null ? funcionario.getIdSupervisor() : "N/A"});
                            if (usuarioAssociado != null) {
                                consultarView.getTableModel().addRow(new Object[]{"Nome:", usuarioAssociado.getNome()});
                                consultarView.getTableModel().addRow(new Object[]{"CPF:", usuarioAssociado.getCpf()});
                                consultarView.getTableModel().addRow(new Object[]{"Telefone:", usuarioAssociado.getTelefone()});
                                consultarView.getTableModel().addRow(new Object[]{"Data Nasc.:", usuarioAssociado.getDataNascimento() != null ? usuarioAssociado.getDataNascimento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A"});
                            }
                        } else {
                            JOptionPane.showMessageDialog(consultarView, "Funcionário não encontrado.", "Resultado", JOptionPane.INFORMATION_MESSAGE);
                        }
                        break;

                    case "Cliente":
                        Cliente cliente = null;
                        Usuario usuarioCliente = null;
                        if (CpfValidator.isValidFormat(criterio)) {
                            usuarioCliente = usuarioDAO.buscarUsuarioPorCpfETipo(criterio, "CLIENTE");
                            if (usuarioCliente != null) cliente = clienteDAO.buscarClientePorId(usuarioCliente.getIdUsuario());
                        } else {
                            try { 
                                int idClienteBusca = Integer.parseInt(criterio);
                                cliente = clienteDAO.buscarClientePorId(idClienteBusca); 
                                if (cliente != null) usuarioCliente = usuarioDAO.buscarUsuarioPorId(cliente.getIdUsuario());
                            } catch (NumberFormatException nfe) {
                            }
                        }
                        
                        if (cliente != null && usuarioCliente != null) {
                            consultarView.getTableModel().setColumnIdentifiers(new String[]{"Campo", "Valor"});
                            consultarView.getTableModel().addRow(new Object[]{"ID Cliente:", cliente.getIdCliente()});
                            consultarView.getTableModel().addRow(new Object[]{"Score de Crédito:", cliente.getScoreCredito()});
                            consultarView.getTableModel().addRow(new Object[]{"Nome:", usuarioCliente.getNome()});
                            consultarView.getTableModel().addRow(new Object[]{"CPF:", usuarioCliente.getCpf()});
                            consultarView.getTableModel().addRow(new Object[]{"Telefone:", usuarioCliente.getTelefone()});
                            consultarView.getTableModel().addRow(new Object[]{"Data Nasc.:", usuarioCliente.getDataNascimento() != null ? usuarioCliente.getDataNascimento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A"});
                            EnderecoDAO enderecoDAO = new EnderecoDAO();
                            Endereco enderecoCliente = enderecoDAO.buscarEnderecoPorUsuarioId(usuarioCliente.getIdUsuario());
                            if (enderecoCliente != null) {
                                consultarView.getTableModel().addRow(new Object[]{"Endereço:", enderecoCliente.getLogradouro() + ", " + enderecoCliente.getNumeroCasa()});
                                consultarView.getTableModel().addRow(new Object[]{"Bairro:", enderecoCliente.getBairro()});
                                consultarView.getTableModel().addRow(new Object[]{"Cidade/Estado:", enderecoCliente.getCidade() + "/" + enderecoCliente.getEstado()});
                                consultarView.getTableModel().addRow(new Object[]{"CEP:", enderecoCliente.getCep()});
                            }
                            
                            List<Conta> contasCliente = contaDAO.buscarContasPorClienteId(cliente.getIdCliente()); 
                            if (!contasCliente.isEmpty()) {
                                consultarView.getTableModel().addRow(new Object[]{"", "", "", "", "", "", "", ""}); 
                                consultarView.getTableModel().addRow(new Object[]{"Contas do Cliente:", "", "", "", "", "", "", ""});
                                consultarView.getTableModel().addRow(new String[]{"Número da Conta", "Tipo", "Saldo", "Status", "Data Abertura", "", "", ""});
                                for (Conta c : contasCliente) {
                                    consultarView.getTableModel().addRow(new Object[]{
                                        c.getNumeroConta(), c.getTipoConta(), String.format("R$ %.2f", c.getSaldo()),
                                        c.getStatus(), c.getDataAbertura().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), "", "", ""
                                    });
                                }
                            }
                        } else {
                            JOptionPane.showMessageDialog(consultarView, "Cliente não encontrado.", "Resultado", JOptionPane.INFORMATION_MESSAGE);
                        }
                        break;
                }
            } catch (SQLException ex) {
                mostrarErro("Erro na consulta de dados", ex);
            }
        });
    }

    private void cadastrarUsuario(Frame parentFrame) {
        System.out.println("TelaPrincipalFuncionarioController: Executando cadastrarUsuario()."); 
        String senhaAdmin = JOptionPane.showInputDialog(view, "Digite a senha de administrador:");
        if (senhaAdmin == null || senhaAdmin.isBlank()) {
            return;
        }
        if ("admin123".equals(senhaAdmin)) { 
            new CadastrarUsuarioView(parentFrame, "FUNCIONARIO").setVisible(true); 
        } else {
            JOptionPane.showMessageDialog(view, "Senha incorreta.", "Acesso negado", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void gerarRelatorio() {
        System.out.println("TelaPrincipalFuncionarioController: Executando gerarRelatorio()."); 
        Frame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(view);

        GerarRelatoriosView relatoriosView = new GerarRelatoriosView(parentFrame);
        relatoriosView.setVisible(true);

        relatoriosView.getBtnGerar().addActionListener(e -> {
            String tipoRelatorio = (String) relatoriosView.getCmbTipoRelatorio().getSelectedItem();
            LocalDate dataInicio = null;
            LocalDate dataFim = null;
            String tipoTransacao = null;
            int codigoAgencia = -1; 

            relatoriosView.getTableModel().setColumnCount(0);
            relatoriosView.getTableModel().setRowCount(0);

            try {
                switch (tipoRelatorio) {
                    case "Movimentações por Período":
                        String dataInicioStr = relatoriosView.getTxtDataInicio().getText().trim();
                        String dataFimStr = relatoriosView.getTxtDataFim().getText().trim();
                        tipoTransacao = relatoriosView.getTxtTipoTransacao().getText().trim();
                        String codAgenciaStr = relatoriosView.getTxtCodigoAgencia().getText().trim();

                        if (dataInicioStr.isEmpty() || dataFimStr.isEmpty()) {
                            JOptionPane.showMessageDialog(relatoriosView, "Datas de início e fim são obrigatórias para este relatório.", "Erro", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        try {
                            dataInicio = LocalDate.parse(dataInicioStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                            dataFim = LocalDate.parse(dataFimStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        } catch (DateTimeParseException ex) {
                            JOptionPane.showMessageDialog(relatoriosView, "Formato de data inválido. Use dd/mm/aaaa.", "Erro", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        if (!codAgenciaStr.isEmpty()) {
                            codigoAgencia = Integer.parseInt(codAgenciaStr);
                        }
                        
                        List<Map<String, Object>> movimentacoes = relatorioDAO.gerarRelatorioMovimentacoes(dataInicio, dataFim, tipoTransacao, codigoAgencia);
                        popularTabelaComRelatorio(relatoriosView.getTableModel(), movimentacoes);
                        JOptionPane.showMessageDialog(relatoriosView, "Relatório de movimentações gerado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                        break;

                    case "Clientes Inadimplentes":
                        List<Map<String, Object>> inadimplentes = relatorioDAO.gerarRelatorioInadimplencia();
                        popularTabelaComRelatorio(relatoriosView.getTableModel(), inadimplentes);
                        JOptionPane.showMessageDialog(relatoriosView, "Relatório de clientes inadimplentes gerado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                        break;

                    case "Desempenho de Funcionários":
                        JOptionPane.showMessageDialog(relatoriosView, "Funcionalidade de Relatório de Desempenho de Funcionários não implementada.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                        break;
                }
            } catch (SQLException | NumberFormatException ex) {
                mostrarErro("Erro ao gerar relatório", ex);
            }
        });

        relatoriosView.getBtnExportarExcel().addActionListener(e -> JOptionPane.showMessageDialog(relatoriosView, "Exportar para Excel (Não implementado)", "Aviso", JOptionPane.INFORMATION_MESSAGE));
        relatoriosView.getBtnExportarPDF().addActionListener(e -> JOptionPane.showMessageDialog(relatoriosView, "Exportar para PDF (Não implementado)", "Aviso", JOptionPane.INFORMATION_MESSAGE));
    }

    private void popularTabelaComRelatorio(DefaultTableModel model, List<Map<String, Object>> data) {
        if (data == null || data.isEmpty()) {
            model.setColumnCount(1);
            model.setColumnIdentifiers(new String[]{"Resultado"});
            model.addRow(new Object[]{"Nenhum dado encontrado para o relatório."});
            return;
        }

        Map<String, Object> firstRow = data.get(0);
        String[] columnNames = firstRow.keySet().toArray(new String[0]);
        
        model.setColumnIdentifiers(columnNames);

        for (Map<String, Object> rowMap : data) {
            Object[] rowData = new Object[columnNames.length];
            for (int i = 0; i < columnNames.length; i++) {
                rowData[i] = rowMap.get(columnNames[i]);
            }
            model.addRow(rowData);
        }
    }

    private void mostrarErro(String titulo, Exception ex) {
        JOptionPane.showMessageDialog(view, titulo + ": " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace(); 
    }
}
