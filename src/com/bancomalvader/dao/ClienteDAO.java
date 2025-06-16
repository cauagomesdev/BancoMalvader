package com.bancomalvader.dao;

import com.bancomalvader.model.Cliente;
import com.bancomalvader.util.ConexaoBanco;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ClienteDAO {

    public int inserirCliente(Cliente cliente, Connection conn) throws SQLException {
        String sql = "INSERT INTO cliente (id_usuario, score_credito) VALUES (?, ?)";
        int idClienteGerado = -1;

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, cliente.getIdUsuario());
            stmt.setDouble(2, cliente.getScoreCredito()); 

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        idClienteGerado = rs.getInt(1);
                        cliente.setIdCliente(idClienteGerado);
                    }
                }
            }
        }
        return idClienteGerado;
    }
    public Cliente buscarClientePorId(int idCliente) throws SQLException {
		String sql = "SELECT id_cliente, id_usuario, score_credito FROM cliente WHERE id_cliente = ?";
		
		try (Connection conn = ConexaoBanco.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)){
			stmt.setInt(1, idCliente);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					int idUsuario = rs.getInt("id_usuario");
					Double scoreCredito = rs.getDouble("score_credito");
					
					return new Cliente(idCliente, idUsuario, scoreCredito);
				}
			}
		}
		return null;
	}
    public Cliente buscarClientePorUsuarioId(int idUsuario) throws SQLException {
		String sql = "SELECT id_cliente, id_usuario, score_credito FROM cliente WHERE id_usuario = ?";
		
		try (Connection conn = ConexaoBanco.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)){
			stmt.setInt(1, idUsuario);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					int idCliente = rs.getInt("id_cliente");
					Double scoreCredito = rs.getDouble("score_credito");
					
					return new Cliente(idCliente, idUsuario, scoreCredito);
				}
			}
		}
		return null;
	}
    public BigDecimal getScoreCredito(int idCliente) throws SQLException {
        String sql = "SELECT score_credito FROM cliente WHERE id_cliente = ?";
        try (Connection conn = ConexaoBanco.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("score_credito");
                }
            }
        }
        return BigDecimal.ZERO; // Retorna zero se o cliente não for encontrado
    }
    public int inserirCliente(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO cliente (id_usuario, score_credito) VALUES (?, ?)";
        int idClienteGerado = -1;

        try (Connection conn = ConexaoBanco.getConnection();
        		PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, cliente.getIdUsuario());
            stmt.setDouble(2, cliente.getScoreCredito()); 

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        idClienteGerado = rs.getInt(1);
                        cliente.setIdCliente(idClienteGerado);
                    }
                }
            }
        }
        return idClienteGerado;
    }
}
