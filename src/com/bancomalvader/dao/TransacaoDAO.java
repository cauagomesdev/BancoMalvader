package com.bancomalvader.dao;

import com.bancomalvader.model.Transacao;
import com.bancomalvader.util.ConexaoBanco;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class TransacaoDAO {

	public int inserirTransacao(Transacao transacao) throws SQLException{
		String sql = "INSERT INTO transacao ( id_conta_origem, id_conta_destino, tipo_transacao, valor, data_hora, descricao) VALUES (?, ?, ?, ?, ?, ?)";
		int idTransacaoGerado = -1;
		
		try (Connection conn = ConexaoBanco.getConnection();
			PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			
			stmt.setInt(1, transacao.getIdContaOrigem());
			
			if (transacao.getIdContaDestino() != null) {
				stmt.setInt(2, transacao.getIdContaDestino());
			} else {
				stmt.setNull(2, Types.INTEGER);
			}
			stmt.setString(3, transacao.getTipoTransacao());
			stmt.setDouble(4, transacao.getValor());
			stmt.setTimestamp(5, java.sql.Timestamp.valueOf(transacao.getDataHora()));
			stmt.setString(6, transacao.getDescricao());
			
			int rowsAffected = stmt.executeUpdate();
			if (rowsAffected > 0) {
				try (ResultSet rs = stmt.getGeneratedKeys()){
					if (rs.next()) {
						idTransacaoGerado = rs.getInt(1);
						transacao.setIdTransacao(idTransacaoGerado);
					}
				}
			}
		}
		return idTransacaoGerado;
	}
	public List<Transacao> buscarTransacaoPorConta(int idConta) throws SQLException {
		String sql = "SELECT id_transacao, id_conta_origem, id_conta_destino, tipo_transacao, valor, data_hora, descricao FROM transacao WHERE id_conta_origem = ? OR id_conta_destino = ? ORDER BY data_hora DESC";
		List<Transacao> transacoes = new ArrayList<>();
		
			try (Connection conn = ConexaoBanco.getConnection();
					PreparedStatement stmt = conn.prepareStatement(sql)){
				stmt.setInt(1, idConta);
				stmt.setInt(2, idConta);
			
			try(ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					int idTransacao = rs.getInt("id_transacao");
					int idContaOrigem = rs.getInt("id_conta_origem");
					Integer idContaDestino = (Integer) rs.getObject("id_conta_destino");
					String tipoTransacao = rs.getString("tipo_transacao");
					Double valor = rs.getDouble("valor");
					LocalDateTime dataHora = rs.getTimestamp("data_hora").toLocalDateTime();
					String descricao = rs.getString("descricao");
					
					Transacao transacao = new Transacao(idTransacao, idContaOrigem, idContaDestino, tipoTransacao, valor, dataHora, descricao);
					transacoes.add(transacao);
				}
			}
		}
		return transacoes;
	}
	public List<Transacao> buscarTransacoesPorContaEPeriodo(int idConta, LocalDate dataInicio, LocalDate dataFim) throws SQLException {
		String sql = "SELECT id_transacao, id_conta_origem, id_conta_destino, tipo_transacao, valor, data_hora, descricao FROM transacao WHERE (id_conta_origem = ? OR id_conta_destino = ?) AND  DATE(data_hora) BETWEEN ? AND ? ORDER BY data_hora DESC";
		List<Transacao> transacoesPCP = new ArrayList<>();
		
			try (Connection conn = ConexaoBanco.getConnection();
					PreparedStatement stmt = conn.prepareStatement(sql)){
				stmt.setInt(1, idConta);
				stmt.setInt(2, idConta);
				stmt.setDate(3, Date.valueOf(dataInicio));
				stmt.setDate(4, Date.valueOf(dataFim));
				
			try(ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					int idTransacao = rs.getInt("id_transacao");
					int idContaOrigem = rs.getInt("id_conta_origem");
					Integer idContaDestino = (Integer) rs.getObject("id_conta_destino");
					String tipoTransacao = rs.getString("tipo_transacao");
					Double valor = rs.getDouble("valor");
					LocalDateTime dataHora = rs.getTimestamp("data_hora").toLocalDateTime();
					String descricao = rs.getString("descricao");
					
					Transacao transacao = new Transacao(idTransacao, idContaOrigem, idContaDestino, tipoTransacao, valor, dataHora, descricao);
					transacoesPCP.add(transacao);
				}
			}
		}
		return transacoesPCP;
	}
	public List<Transacao> buscarUltimasTransacoesPorConta(int idConta, int limite) throws SQLException {
		String sql = "SELECT id_transacao, id_conta_origem, id_conta_destino, tipo_transacao, valor, data_hora, descricao FROM transacao WHERE id_conta_origem = ? OR id_conta_destino = ? ORDER BY data_hora DESC LIMIT ?";
		List<Transacao> ultimastransacoes = new ArrayList<>();
		
			try (Connection conn = ConexaoBanco.getConnection();
					PreparedStatement stmt = conn.prepareStatement(sql)){
				stmt.setInt(1, idConta);
				stmt.setInt(2, idConta);
				stmt.setInt(3, limite);
				
			try(ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					int idTransacao = rs.getInt("id_transacao");
					int idContaOrigem = rs.getInt("id_conta_origem");
					Integer idContaDestino = (Integer) rs.getObject("id_conta_destino");
					String tipoTransacao = rs.getString("tipo_transacao");
					Double valor = rs.getDouble("valor");
					LocalDateTime dataHora = rs.getTimestamp("data_hora").toLocalDateTime();
					String descricao = rs.getString("descricao");
					
					Transacao transacao = new Transacao(idTransacao, idContaOrigem, idContaDestino, tipoTransacao, valor, dataHora, descricao);
					ultimastransacoes.add(transacao);
				}
			}
		}
		return ultimastransacoes;
	}
	public double calcularTotalDepositosDiarios(int idConta, LocalDate data) throws SQLException {
		String sql = "SELECT SUM (valor) AS total FROM transacao WHERE id_conta_origem = ? AND tipo_transacao = 'DEPOSITO' AND DATE(data_hora) = ?";
		double total = 0;
		
			try (Connection conn = ConexaoBanco.getConnection();
					PreparedStatement stmt = conn.prepareStatement(sql)){
				stmt.setInt(1, idConta);
				stmt.setDate(2, Date.valueOf(data));
				
			try(ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					total = rs.getDouble("total");
				}
			}
		}
		return total;
	}
    public int contarSaquesNomes(int idConta) throws SQLException {
        String sql = "SELECT COUNT(*) FROM transacao " +
                     "WHERE id_conta_origem = ? " +
                     "AND (tipo_transacao = 'SAQUE' OR tipo_transacao = 'TAXA') " +
                     "AND YEAR(data_hora) = YEAR(CURDATE()) " +
                     "AND MONTH(data_hora) = MONTH(CURDATE())";
        try (Connection conn = ConexaoBanco.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idConta);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0; // Retorna 0 se não encontrar saques
    }

}
