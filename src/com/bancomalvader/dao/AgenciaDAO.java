package com.bancomalvader.dao;

import com.bancomalvader.model.Agencia;
import com.bancomalvader.util.ConexaoBanco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AgenciaDAO {

    public int inserirAgencia(Agencia agencia) throws SQLException {
        String sql = "INSERT INTO agencia (nome, codigo_agencia, endereco_id) VALUES (?, ?, ?)";
        int idAgenciaGerado = -1;

        try (Connection conn = ConexaoBanco.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, agencia.getNome());
            stmt.setString(2, agencia.getCodigoAgencia());
            stmt.setInt(3, agencia.getEnderecoId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        idAgenciaGerado = rs.getInt(1);
                        agencia.setIdAgencia(idAgenciaGerado);
                    }
                }
            }
        }
        return idAgenciaGerado;
    }

    public Agencia buscarAgenciaPorCodigo(String codigoAgencia) throws SQLException {
        String sql = "SELECT id_agencia, nome, codigo_agencia, endereco_id FROM agencia WHERE codigo_agencia = ?";
        try (Connection conn = ConexaoBanco.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, codigoAgencia);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int idAgencia = rs.getInt("id_agencia");
                    String nome = rs.getString("nome");
                    int enderecoId = rs.getInt("endereco_id");
                    return new Agencia(idAgencia, nome, codigoAgencia, enderecoId);
                }
            }
        }
        return null;
    }

    public Agencia buscarAgenciaPorId(int idAgencia) throws SQLException {
        String sql = "SELECT id_agencia, nome, codigo_agencia, endereco_id FROM agencia WHERE id_agencia = ?";
        try (Connection conn = ConexaoBanco.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idAgencia);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String nome = rs.getString("nome");
                    String codigoAgencia = rs.getString("codigo_agencia");
                    int enderecoId = rs.getInt("endereco_id");
                    return new Agencia(idAgencia, nome, codigoAgencia, enderecoId);
                }
            }
        }
        return null;
    }
}
