package com.bancomalvader.dao;

import com.bancomalvader.model.Conta;
import com.bancomalvader.model.ContaCorrente;
import com.bancomalvader.model.ContaInvestimento;
import com.bancomalvader.model.ContaPoupanca;
import com.bancomalvader.util.ConexaoBanco;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter; // Para formatar LocalDate para String SQL
import java.util.ArrayList;
import java.util.List;

public class ContaDAO {

    
    public int inserirConta(Conta conta, Connection conn) throws SQLException {
        String sql = "INSERT INTO conta (numero_conta, id_agencia, saldo, tipo_conta, id_cliente, data_abertura, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        int idContaGerado = -1;

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, conta.getNumeroConta());
            stmt.setInt(2, conta.getIdAgencia());
            stmt.setDouble(3, conta.getSaldo());
            stmt.setString(4, conta.getTipoConta());
            stmt.setInt(5, conta.getIdCliente());
            stmt.setTimestamp(6, java.sql.Timestamp.valueOf(conta.getDataAbertura())); 
            stmt.setString(7, conta.getStatus());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        idContaGerado = rs.getInt(1);
                        conta.setIdConta(idContaGerado);
                    }
                }
            }
        }
        return idContaGerado;
    }

    public void inserirContaPoupanca(ContaPoupanca poupanca, Connection conn) throws SQLException {
        String sql = "INSERT INTO conta_poupanca (id_conta, taxa_rendimento, ultimo_rendimento) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, poupanca.getIdConta());
            stmt.setDouble(2, poupanca.getTaxaRendimento());
            stmt.setTimestamp(3, poupanca.getUltimoRendimento() != null ? java.sql.Timestamp.valueOf(poupanca.getUltimoRendimento()) : null);
            stmt.executeUpdate();
        }
    }
 
    public void inserirContaCorrente(ContaCorrente corrente, Connection conn) throws SQLException {
        String sql = "INSERT INTO conta_corrente (id_conta, limite, data_vencimento, taxa_manutencao) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, corrente.getIdConta());
            stmt.setBigDecimal(2, corrente.getLimite());
            stmt.setString(3, corrente.getDataVencimento().format(DateTimeFormatter.ISO_DATE)); 
            stmt.setDouble(4, corrente.getTaxaManutencao());
            stmt.executeUpdate();
        }
    }
  
    public void inserirContaInvestimento(ContaInvestimento investimento, Connection conn) throws SQLException {
        String sql = "INSERT INTO conta_investimento (id_conta, perfil_risco, valor_minimo, taxa_rendimento_base) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, investimento.getIdConta());
            stmt.setString(2, investimento.getPerfilRisco());
            stmt.setDouble(3, investimento.getValorMinimo());
            stmt.setDouble(4, investimento.getTaxaRendimentoBase());
            stmt.executeUpdate();
        }
    }

    public Conta buscarContaPorNumero(String numeroConta) throws SQLException {
        String sql = "SELECT id_conta, numero_conta, id_agencia, saldo, tipo_conta, id_cliente, data_abertura, status FROM conta WHERE numero_conta = ?";
        try (Connection conn = ConexaoBanco.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, numeroConta);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int idConta = rs.getInt("id_conta");
                    int idAgencia = rs.getInt("id_agencia");
                    double saldo = rs.getDouble("saldo");
                    String tipoConta = rs.getString("tipo_conta");
                    int idCliente = rs.getInt("id_cliente");
                    java.sql.Timestamp dataAberturaTs = rs.getTimestamp("data_abertura");
                    String status = rs.getString("status");
                    return new Conta(idConta, numeroConta, idAgencia, saldo, tipoConta, idCliente, dataAberturaTs.toLocalDateTime(), status);
                }
            }
        }
        return null;
    }
    public void atualizarStatusConta(int idConta, String novoStatus, Connection conn) throws SQLException {
        String sql = "UPDATE conta SET status = ? WHERE id_conta = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, novoStatus);
            stmt.setInt(2, idConta);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Nenhuma conta encontrada ou status atualizado para o ID: " + idConta);
            }
        }
    }
    public void atualizarContaPoupanca(ContaPoupanca poupanca, Connection conn) throws SQLException {
        String sql = "UPDATE conta_poupanca SET taxa_rendimento = ?, ultimo_rendimento = ? WHERE id_conta = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, poupanca.getTaxaRendimento());
            stmt.setTimestamp(2, poupanca.getUltimoRendimento() != null ? java.sql.Timestamp.valueOf(poupanca.getUltimoRendimento()) : null);
            stmt.setInt(3, poupanca.getIdConta());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Nenhuma conta poupança encontrada ou atualizada com ID de conta: " + poupanca.getIdConta());
            }
        }
    }
    public void atualizarContaCorrente(ContaCorrente corrente, Connection conn) throws SQLException {
        String sql = "UPDATE conta_corrente SET limite = ?, data_vencimento = ?, taxa_manutencao = ? WHERE id_conta = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBigDecimal(1, corrente.getLimite());
            stmt.setString(2, corrente.getDataVencimento().format(DateTimeFormatter.ISO_DATE)); 
            stmt.setDouble(3, corrente.getTaxaManutencao());
            stmt.setInt(4, corrente.getIdConta());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Nenhuma conta corrente encontrada ou atualizada com ID de conta: " + corrente.getIdConta());
            }
        }
    }
    public void atualizarContaInvestimento(ContaInvestimento investimento, Connection conn) throws SQLException {
        String sql = "UPDATE conta_investimento SET perfil_risco = ?, valor_minimo = ?, taxa_rendimento_base = ? WHERE id_conta = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, investimento.getPerfilRisco());
            stmt.setDouble(2, investimento.getValorMinimo());
            stmt.setDouble(3, investimento.getTaxaRendimentoBase());
            stmt.setInt(4, investimento.getIdConta());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Nenhuma conta investimento encontrada ou atualizada com ID de conta: " + investimento.getIdConta());
            }
        }
    }
    public ContaPoupanca buscarContaPoupancaPorIdConta(int idConta) throws SQLException {
        String sql = "SELECT id_conta_poupanca, id_conta, taxa_rendimento, ultimo_rendimento FROM conta_poupanca WHERE id_conta = ?";
        try (Connection conn = ConexaoBanco.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idConta);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int idCp = rs.getInt("id_conta_poupanca");
                    double taxaRendimento = rs.getDouble("taxa_rendimento");
                    LocalDateTime ultimoRendimento = rs.getTimestamp("ultimo_rendimento") != null ? rs.getTimestamp("ultimo_rendimento").toLocalDateTime() : null;
                    return new ContaPoupanca(idCp, idConta, taxaRendimento, ultimoRendimento);
                }
            }
        }
        return null;
    }
    public ContaCorrente buscarContaCorrentePorIdConta(int idConta) throws SQLException {
        String sql = "SELECT id_conta_corrente, id_conta, limite, data_vencimento, taxa_manutencao FROM conta_corrente WHERE id_conta = ?";
        try (Connection conn = ConexaoBanco.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idConta);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int idCc = rs.getInt("id_conta_corrente");
                    BigDecimal limite = rs.getBigDecimal("limite");
                    LocalDate dataVencimento = rs.getDate("data_vencimento").toLocalDate();
                    double taxaManutencao = rs.getDouble("taxa_manutencao");
                    return new ContaCorrente(idCc, idConta, limite, dataVencimento, taxaManutencao);
                }
            }
        }
        return null;
    }
    public ContaInvestimento buscarContaInvestimentoPorIdConta(int idConta) throws SQLException {
        String sql = "SELECT id_conta_investimento, id_conta, perfil_risco, valor_minimo, taxa_rendimento_base FROM conta_investimento WHERE id_conta = ?";
        try (Connection conn = ConexaoBanco.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idConta);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int idCi = rs.getInt("id_conta_investimento");
                    String perfilRisco = rs.getString("perfil_risco");
                    double valorMinimo = rs.getDouble("valor_minimo");
                    double taxaRendimentoBase = rs.getDouble("taxa_rendimento_base");
                    return new ContaInvestimento(idCi, idConta, perfilRisco, valorMinimo, taxaRendimentoBase);
                }
            }
        }
        return null;
    }
    public BigDecimal obterSaldo(int idConta) throws SQLException {
        String sql = "SELECT saldo FROM conta WHERE id_conta = ?";
        try (Connection conn = ConexaoBanco.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idConta);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("saldo"); // Usar getBigDecimal para precisão monetária
                }
            }
        }
        return BigDecimal.ZERO; // Retorna zero se a conta não for encontrada
    }
    public BigDecimal getSaldoAtual(int idConta) throws SQLException {
        return obterSaldo(idConta);
    }
    public Conta buscarContaPrincipalAtivaPorIdCliente(int idCliente) throws SQLException {
        String sql = "SELECT id_conta, numero_conta, id_agencia, saldo, tipo_conta, id_cliente, data_abertura, status " +
                     "FROM conta WHERE id_cliente = ? AND status = 'ATIVA' LIMIT 1";
        try (Connection conn = ConexaoBanco.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int idConta = rs.getInt("id_conta");
                    String numeroConta = rs.getString("numero_conta");
                    int idAgencia = rs.getInt("id_agencia");
                    double saldo = rs.getDouble("saldo");
                    String tipoConta = rs.getString("tipo_conta");
                    LocalDateTime dataAbertura = rs.getTimestamp("data_abertura").toLocalDateTime();
                    String status = rs.getString("status");
                    return new Conta(idConta, numeroConta, idAgencia, saldo, tipoConta, idCliente, dataAbertura, status);
                }
            }
        }
        return null;
    }
    public BigDecimal getLimiteContaCorrente(int idConta) throws SQLException {
        String sql = "SELECT limite FROM conta_corrente WHERE id_conta = ?";
        try (Connection conn = ConexaoBanco.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idConta);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("limite");
                }
            }
        }
        return BigDecimal.ZERO; // Retorna zero se não for encontrada ou não for CC
    }
    public BigDecimal obterSaldoPorUsuario(int idCliente) throws SQLException {
        Conta conta = buscarContaPrincipalAtivaPorIdCliente(idCliente);
        return conta != null ? getSaldoAtual(conta.getIdConta()) : BigDecimal.ZERO;
    }

    public void depositar(int idCliente, BigDecimal valor) throws SQLException {
        Conta conta = buscarContaPrincipalAtivaPorIdCliente(idCliente);
        if (conta != null) {
            String sql = "UPDATE conta SET saldo = saldo + ? WHERE id_conta = ?";
            try (Connection conn = ConexaoBanco.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setBigDecimal(1, valor);
                stmt.setInt(2, conta.getIdConta());
                stmt.executeUpdate();
            }
        } else {
            throw new SQLException("Conta não encontrada para o cliente ID: " + idCliente);
        }
    }

    public boolean sacar(int idCliente, BigDecimal valor) throws SQLException {
        Conta conta = buscarContaPrincipalAtivaPorIdCliente(idCliente);
        if (conta != null) {
            BigDecimal saldo = getSaldoAtual(conta.getIdConta());
            if (saldo.compareTo(valor) >= 0) {
                String sql = "UPDATE conta SET saldo = saldo - ? WHERE id_conta = ?";
                try (Connection conn = ConexaoBanco.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setBigDecimal(1, valor);
                    stmt.setInt(2, conta.getIdConta());
                    stmt.executeUpdate();
                    return true;
                }
            }
        }
        return false;
    }

    public boolean transferir(int idCliente, String numeroContaDestino, BigDecimal valor) throws SQLException {
        Conta origem = buscarContaPrincipalAtivaPorIdCliente(idCliente);
        Conta destino = buscarContaPorNumero(numeroContaDestino);
        if (origem != null && destino != null) {
            BigDecimal saldo = getSaldoAtual(origem.getIdConta());
            if (saldo.compareTo(valor) >= 0) {
                try (Connection conn = ConexaoBanco.getConnection()) {
                    conn.setAutoCommit(false);
                    try {
                        // Sacar da conta origem
                        String sqlSaque = "UPDATE conta SET saldo = saldo - ? WHERE id_conta = ?";
                        try (PreparedStatement stmt = conn.prepareStatement(sqlSaque)) {
                            stmt.setBigDecimal(1, valor);
                            stmt.setInt(2, origem.getIdConta());
                            stmt.executeUpdate();
                        }

                        // Depositar na conta destino
                        String sqlDeposito = "UPDATE conta SET saldo = saldo + ? WHERE id_conta = ?";
                        try (PreparedStatement stmt = conn.prepareStatement(sqlDeposito)) {
                            stmt.setBigDecimal(1, valor);
                            stmt.setInt(2, destino.getIdConta());
                            stmt.executeUpdate();
                        }

                        conn.commit();
                        return true;
                    } catch (SQLException e) {
                        conn.rollback();
                        throw e;
                    } finally {
                        conn.setAutoCommit(true);
                    }
                }
            }
        }
        return false;
    }

    public BigDecimal consultarLimite(int idCliente) throws SQLException {
        Conta conta = buscarContaPrincipalAtivaPorIdCliente(idCliente);
        return conta != null ? getLimiteContaCorrente(conta.getIdConta()) : BigDecimal.ZERO;
    }

    public BigDecimal consultarScoreCredito(int idCliente) throws SQLException {
        String sql = "SELECT AVG(saldo) as score FROM conta WHERE id_cliente = ?";
        try (Connection conn = ConexaoBanco.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("score") != null ? rs.getBigDecimal("score") : BigDecimal.ZERO;
                }
            }
        }
        return BigDecimal.ZERO;
    }

    public boolean atualizarDadosCliente(int idCliente, String telefone, String senha) throws SQLException {
        String sql = "UPDATE usuario SET telefone = ?, senha = ? WHERE id_usuario = ?";
        try (Connection conn = ConexaoBanco.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, telefone);
            stmt.setString(2, senha);
            stmt.setInt(3, idCliente);
            return stmt.executeUpdate() > 0;
        }
    }
    public List<Conta> buscarContasPorClienteId(int idCliente) throws SQLException {
        String sql = "SELECT id_conta, numero_conta, id_agencia, saldo, tipo_conta, id_cliente, data_abertura, status " +
                     "FROM conta WHERE id_cliente = ? ORDER BY data_abertura DESC";
        List<Conta> contas = new ArrayList<>();
        try (Connection conn = ConexaoBanco.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int idConta = rs.getInt("id_conta");
                    String numeroConta = rs.getString("numero_conta");
                    int idAgencia = rs.getInt("id_agencia");
                    double saldo = rs.getDouble("saldo");
                    String tipoConta = rs.getString("tipo_conta");
                    // id_cliente já é conhecido
                    LocalDateTime dataAbertura = rs.getTimestamp("data_abertura").toLocalDateTime();
                    String status = rs.getString("status");
                    contas.add(new Conta(idConta, numeroConta, idAgencia, saldo, tipoConta, idCliente, dataAbertura, status));
                }
            }
        }
        return contas;
    }
}
