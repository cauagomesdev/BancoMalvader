package com.bancomalvader.controller;

import com.bancomalvader.dao.ContaDAO;
import com.bancomalvader.dao.TransacaoDAO;
import com.bancomalvader.dao.UsuarioDAO; 
import com.bancomalvader.dao.ClienteDAO; 
import com.bancomalvader.dao.AuditoriaDAO; 
import com.bancomalvader.model.Usuario;
import com.bancomalvader.model.Conta; 
import com.bancomalvader.model.Transacao; 
import com.bancomalvader.model.ContaCorrente; 
import com.bancomalvader.model.Cliente; 
import com.bancomalvader.model.Endereco; 
import com.bancomalvader.model.Auditoria; 

import com.bancomalvader.util.ConexaoBanco; 
import com.bancomalvader.util.PasswordHasher; 
import com.bancomalvader.util.CpfValidator; 

import com.bancomalvader.view.TelaPrincipalClienteView; 
import com.bancomalvader.view.LoginView; 

import javax.swing.*;
import java.awt.Window; // Importe java.awt.Window
import java.math.BigDecimal; 
import java.sql.Connection; 
import java.sql.SQLException;
import java.time.LocalDateTime; 
import java.util.List;
import java.util.Optional; 

public class TelaPrincipalClienteController {

    private final TelaPrincipalClienteView view;
    private final Usuario usuarioLogado; 

    private final ContaDAO contaDAO;
    private final TransacaoDAO transacaoDAO;
    private final UsuarioDAO usuarioDAO; 
    private final ClienteDAO clienteDAO; 
    private final AuditoriaDAO auditoriaDAO; 

    public TelaPrincipalClienteController(TelaPrincipalClienteView view, Usuario usuarioLogado) {
        this.view = view;
        this.usuarioLogado = usuarioLogado;
        this.contaDAO = new ContaDAO();
        this.transacaoDAO = new TransacaoDAO();
        this.usuarioDAO = new UsuarioDAO(); 
        this.clienteDAO = new ClienteDAO(); 
        this.auditoriaDAO = new AuditoriaDAO(); 

        view.setInfoUsuario(usuarioLogado.getNome(), usuarioLogado.getCpf(), usuarioLogado.getTipoUsuario());

        configurarAcoes();
    }

    private Optional<Conta> getContaAtivaDoCliente() {
        try {
            Cliente cliente = clienteDAO.buscarClientePorId(usuarioLogado.getIdUsuario());
            if (cliente != null) {
                return Optional.ofNullable(contaDAO.buscarContaPrincipalAtivaPorIdCliente(cliente.getIdCliente()));
            }
        } catch (SQLException ex) {
            mostrarErro("Erro ao buscar conta ativa do cliente", ex);
        }
        return Optional.empty(); 
    }

    private void configurarAcoes() {
        view.getBtnSaldo().addActionListener(e -> mostrarSaldo());
        view.getBtnExtrato().addActionListener(e -> mostrarExtrato());
        view.getBtnDeposito().addActionListener(e -> fazerDeposito());
        view.getBtnSaque().addActionListener(e -> fazerSaque());
        view.getBtnTransferencia().addActionListener(e -> fazerTransferencia());
        view.getBtnLimite().addActionListener(e -> mostrarLimite());
        view.getBtnAlterarDados().addActionListener(e -> alterarDados());
        view.getBtnSair().addActionListener(e -> {
            // ✅ CORREÇÃO AQUI: Obtém a janela ancestral (JFrame) e a fecha.
            Window ancestralWindow = SwingUtilities.getWindowAncestor(view); // Obtém a janela (JFrame) que contém este JPanel
            if (ancestralWindow instanceof JFrame) { // Verifica se é um JFrame
                ((JFrame) ancestralWindow).dispose(); // Converte para JFrame e chama dispose()
            }
            new LoginView().setVisible(true); 
        });
    }

    private void mostrarSaldo() {
        Optional<Conta> contaOptional = getContaAtivaDoCliente();
        if (contaOptional.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Nenhuma conta ativa encontrada para este cliente.", "Saldo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Conta conta = contaOptional.get();

        try {
            BigDecimal saldo = contaDAO.obterSaldo(conta.getIdConta()); 
            JOptionPane.showMessageDialog(view,
                    "Seu saldo atual é: R$ " + saldo.setScale(2, BigDecimal.ROUND_HALF_UP).toString(), 
                    "Saldo", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            mostrarErro("Erro ao consultar saldo", ex);
        }
    }

    private void mostrarExtrato() {
        Optional<Conta> contaOptional = getContaAtivaDoCliente();
        if (contaOptional.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Nenhuma conta ativa encontrada para este cliente.", "Extrato", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Conta conta = contaOptional.get();

        try {
            List<Transacao> ultimasTransacoes = transacaoDAO.buscarUltimasTransacoesPorConta(conta.getIdConta(), 50); 
            StringBuilder extrato = new StringBuilder("Extrato das Últimas 50 Transações:\n\n");
            if (ultimasTransacoes.isEmpty()) {
                extrato.append("Nenhuma transação recente.");
            } else {
                for (Transacao linha : ultimasTransacoes) {
                    extrato.append("Data: ").append(linha.getDataHora().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
                           .append(" | Tipo: ").append(linha.getTipoTransacao())
                           .append(" | Valor: R$ ").append(String.format("%.2f", linha.getValor())) 
                           .append(" | Descrição: ").append(linha.getDescricao());
                    if (linha.getTipoTransacao().equals("TRANSFERENCIA") && linha.getIdContaDestino() != null) {
                        extrato.append(" | Destino ID: ").append(linha.getIdContaDestino());
                    }
                    extrato.append("\n");
                }
            }
            JOptionPane.showMessageDialog(view, extrato.toString(), "Extrato", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            mostrarErro("Erro ao consultar extrato", ex);
        }
    }

    private void fazerDeposito() {
        Optional<Conta> contaOptional = getContaAtivaDoCliente();
        if (contaOptional.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Nenhuma conta ativa encontrada para este cliente para depósito.", "Depósito", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Conta conta = contaOptional.get();

        try {
            String valorStr = JOptionPane.showInputDialog(view, "Informe o valor do depósito:");
            if (valorStr == null || valorStr.isBlank()) {
                return; 
            }
            BigDecimal valor = new BigDecimal(valorStr.trim().replace(",", ".")); 
            if (valor.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("O valor do depósito deve ser positivo.");
            }

            Transacao transacao = new Transacao(conta.getIdConta(), conta.getIdConta(), "DEPOSITO", valor.doubleValue(), null, "Depósito em conta"); 
            int idTransacao = transacaoDAO.inserirTransacao(transacao);

            if (idTransacao != -1) {
                auditoriaDAO.registrarAuditoria(new Auditoria(usuarioLogado.getIdUsuario(), "DEPOSITO", LocalDateTime.now(), "Depósito de R$" + valor + " na conta " + conta.getNumeroConta()), null); 
                JOptionPane.showMessageDialog(view, "Depósito de R$ " + valor.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + " realizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(view, "Falha ao registrar o depósito.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view, "Valor inválido. Digite um número.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(view, ex.getMessage(), "Erro de Validação", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("Limite diário de depósito excedido")) {
                JOptionPane.showMessageDialog(view, "Erro: Limite diário de depósito excedido (R$ 10.000).", "Limite Excedido", JOptionPane.WARNING_MESSAGE);
            } else {
                mostrarErro("Erro ao realizar depósito", ex);
            }
        }
    }

    private void fazerSaque() {
        Optional<Conta> contaOptional = getContaAtivaDoCliente();
        if (contaOptional.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Nenhuma conta ativa encontrada para este cliente para saque.", "Saque", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Conta conta = contaOptional.get();

        try {
            String valorStr = JOptionPane.showInputDialog(view, "Informe o valor do saque:");
            if (valorStr == null || valorStr.isBlank()) {
                return; 
            }
            BigDecimal valor = new BigDecimal(valorStr.trim().replace(",", "."));
            if (valor.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("O valor do saque deve ser positivo.");
            }

            BigDecimal saldoAtual = contaDAO.getSaldoAtual(conta.getIdConta());
            BigDecimal saldoDisponivel = saldoAtual; 

            if ("CORRENTE".equals(conta.getTipoConta())) {
                ContaCorrente cc = contaDAO.buscarContaCorrentePorIdConta(conta.getIdConta());
                if (cc != null) {
                    saldoDisponivel = saldoAtual.add(cc.getLimite()); 
                }
            }

            if (valor.compareTo(saldoDisponivel) > 0) {
                JOptionPane.showMessageDialog(view, "Saldo insuficiente para o saque. Saldo disponível: R$ " + saldoDisponivel.setScale(2, BigDecimal.ROUND_HALF_UP).toString(), "Saldo Insuficiente", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double taxaSaque = 0.0;
            int saquesNoMes = transacaoDAO.contarSaquesNomes(conta.getIdConta()); 
            if (saquesNoMes >= 5) { 
                taxaSaque = 5.00; 
                JOptionPane.showMessageDialog(view, "Você já realizou " + saquesNoMes + " saques este mês. Uma taxa de R$ " + String.format("%.2f", taxaSaque) + " será aplicada.", "Aviso de Taxa", JOptionPane.INFORMATION_MESSAGE);
            }

            Transacao transacaoSaque = new Transacao(conta.getIdConta(), null, "SAQUE", valor.doubleValue(), null, "Saque em dinheiro");
            int idSaque = transacaoDAO.inserirTransacao(transacaoSaque);

            if (idSaque != -1 && taxaSaque > 0) {
                Transacao transacaoTaxa = new Transacao(conta.getIdConta(), null, "TAXA", taxaSaque, null, "Taxa por saque excessivo");
                transacaoDAO.inserirTransacao(transacaoTaxa); 
            }

            auditoriaDAO.registrarAuditoria(new Auditoria(usuarioLogado.getIdUsuario(), "SAQUE", LocalDateTime.now(), "Saque de R$" + valor + " da conta " + conta.getNumeroConta() + (taxaSaque > 0 ? " com taxa." : ".")), null); 
            JOptionPane.showMessageDialog(view, "Saque de R$ " + valor.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + " realizado com sucesso!" + (taxaSaque > 0 ? "\nTaxa de R$ " + String.format("%.2f", taxaSaque) + " aplicada." : ""), "Sucesso", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view, "Valor inválido. Digite um número.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(view, ex.getMessage(), "Erro de Validação", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            mostrarErro("Erro ao realizar saque", ex);
        }
    }

    private void fazerTransferencia() {
        Optional<Conta> contaOptional = getContaAtivaDoCliente();
        if (contaOptional.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Nenhuma conta ativa encontrada para este cliente para transferência.", "Transferência", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Conta contaOrigem = contaOptional.get();

        try {
            String numeroContaDestinoStr = JOptionPane.showInputDialog(view, "Digite o número da conta destino:");
            String valorStr = JOptionPane.showInputDialog(view, "Digite o valor da transferência:");
            
            if (numeroContaDestinoStr == null || numeroContaDestinoStr.isBlank() || valorStr == null || valorStr.isBlank()) {
                return; 
            }

            BigDecimal valor = new BigDecimal(valorStr.trim().replace(",", "."));
            if (valor.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("O valor da transferência deve ser positivo.");
            }

            BigDecimal saldoAtualOrigem = contaDAO.getSaldoAtual(contaOrigem.getIdConta());
            BigDecimal saldoDisponivelOrigem = saldoAtualOrigem;

            if ("CORRENTE".equals(contaOrigem.getTipoConta())) {
                ContaCorrente ccOrigem = contaDAO.buscarContaCorrentePorIdConta(contaOrigem.getIdConta());
                if (ccOrigem != null) {
                    saldoDisponivelOrigem = saldoAtualOrigem.add(ccOrigem.getLimite());
                }
            }

            if (valor.compareTo(saldoDisponivelOrigem) > 0) {
                JOptionPane.showMessageDialog(view, "Saldo insuficiente para a transferência. Saldo disponível: R$ " + saldoDisponivelOrigem.setScale(2, BigDecimal.ROUND_HALF_UP).toString(), "Saldo Insuficiente", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Conta contaDestino = contaDAO.buscarContaPorNumero(numeroContaDestinoStr.trim());
            if (contaDestino == null || !contaDestino.getStatus().equals("ATIVA")) {
                throw new SQLException("Conta destino não encontrada ou não está ativa.");
            }
            if (contaDestino.getIdConta() == contaOrigem.getIdConta()) {
                throw new IllegalArgumentException("Não é possível transferir para a mesma conta de origem.");
            }

            Transacao transacao = new Transacao(contaOrigem.getIdConta(), contaDestino.getIdConta(), "TRANSFERENCIA", valor.doubleValue(), null, "Transferência para conta " + contaDestino.getNumeroConta());
            int idTransacao = transacaoDAO.inserirTransacao(transacao);

            if (idTransacao != -1) {
                auditoriaDAO.registrarAuditoria(new Auditoria(usuarioLogado.getIdUsuario(), "TRANSFERENCIA", LocalDateTime.now(), "Transferência de R$" + valor + " da conta " + contaOrigem.getNumeroConta() + " para " + contaDestino.getNumeroConta()), null); 
                JOptionPane.showMessageDialog(view, "Transferência de R$ " + valor.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + " realizada com sucesso para " + contaDestino.getNumeroConta() + "!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(view, "Falha ao registrar a transferência.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view, "Valor inválido. Digite um número.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(view, ex.getMessage(), "Erro de Validação", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            mostrarErro("Erro ao realizar transferência", ex);
        }
    }

    private void mostrarLimite() {
        Optional<Conta> contaOptional = getContaAtivaDoCliente();
        if (contaOptional.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Nenhuma conta ativa encontrada para este cliente.", "Limite e Score", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Conta conta = contaOptional.get();

        BigDecimal limite = BigDecimal.ZERO;
        if ("CORRENTE".equals(conta.getTipoConta())) {
            try {
                limite = contaDAO.getLimiteContaCorrente(conta.getIdConta()); 
            } catch (SQLException ex) {
                mostrarErro("Erro ao consultar limite da conta corrente", ex);
            }
        } else {
            JOptionPane.showMessageDialog(view, "O limite é aplicável apenas a contas correntes. Sua conta é do tipo " + conta.getTipoConta() + ".", "Limite", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        BigDecimal score = BigDecimal.ZERO;
        try {
            Cliente cliente = clienteDAO.buscarClientePorId(usuarioLogado.getIdUsuario()); 
            if (cliente != null) {
                score = clienteDAO.getScoreCredito(cliente.getIdCliente()); 
            } else {
                mostrarErro("Erro", new Exception("Cliente associado ao usuário logado não encontrado para consultar score."));
            }
        } catch (SQLException ex) {
            mostrarErro("Erro ao consultar score de crédito", ex);
        }

        JOptionPane.showMessageDialog(view,
                "Limite atual: R$ " + limite.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\nScore de crédito: " + score.setScale(2, BigDecimal.ROUND_HALF_UP).toString(),
                "Limite e Score", JOptionPane.INFORMATION_MESSAGE);
    }

    private void alterarDados() {
        String novoTelefoneStr = JOptionPane.showInputDialog(view, "Novo telefone (deixe em branco para não alterar):");
        String novaSenhaStr = JOptionPane.showInputDialog(view, "Nova senha (deixe em branco para não alterar):");
        
        Connection conn = null;
        try {
            conn = ConexaoBanco.getConnection();
            conn.setAutoCommit(false); 

            boolean alteracaoRealizada = false;
            StringBuilder detalhesAuditoria = new StringBuilder("Alteração de dados do cliente ID: " + usuarioLogado.getIdUsuario());
            
            if (novoTelefoneStr != null && !novoTelefoneStr.trim().isEmpty()) {
                usuarioDAO.atualizarTelefone(usuarioLogado.getIdUsuario(), novoTelefoneStr.trim(), conn);
                alteracaoRealizada = true;
                detalhesAuditoria.append(" - Telefone atualizado para: ").append(novoTelefoneStr.trim());
            }

            if (novaSenhaStr != null && !novaSenhaStr.trim().isEmpty()) {
                if (novaSenhaStr.length() < 8 || !novaSenhaStr.matches(".*[A-Z].*") ||
                    !novaSenhaStr.matches(".*\\d.*") || !novaSenhaStr.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
                    throw new IllegalArgumentException("A nova senha deve ter pelo menos 8 caracteres, 1 letra maiúscula, 1 número e 1 caractere especial.");
                }
                String novaSenhaHash = PasswordHasher.hashPasswordMD5(novaSenhaStr);
                usuarioDAO.atualizarSenha(usuarioLogado.getIdUsuario(), novaSenhaHash, conn);
                alteracaoRealizada = true;
                detalhesAuditoria.append(" - Senha atualizada.");
            }
            
            if (alteracaoRealizada) {
                auditoriaDAO.registrarAuditoria(new Auditoria(usuarioLogado.getIdUsuario(), "ALTERACAO_DADOS_CLIENTE", LocalDateTime.now(), detalhesAuditoria.toString()), conn);
                conn.commit();
                JOptionPane.showMessageDialog(view, "Dados atualizados com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                conn.rollback(); 
                JOptionPane.showMessageDialog(view, "Nenhuma alteração solicitada ou realizada.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }

        } catch (NumberFormatException ex) { 
            JOptionPane.showMessageDialog(view, "Valor inválido. Digite um número.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException | SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Erro ao fazer rollback: " + rollbackEx.getMessage());
                }
            }
            mostrarErro("Erro ao atualizar dados", ex);
        } finally {
            ConexaoBanco.closeConnection(conn);
        }
    }

    private void mostrarErro(String titulo, Exception ex) {
        JOptionPane.showMessageDialog(view,
                titulo + ": " + ex.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace(); 
    }
}
