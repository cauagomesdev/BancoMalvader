package com.bancomalvader.dao;

import com.bancomalvader.model.Usuario;
import com.bancomalvader.util.ConexaoBanco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UsuarioDAO {

    public int inserirUsuario(Usuario usuario, Connection conn) throws SQLException {
        String sql = "INSERT INTO usuario (nome, cpf, data_nascimento, telefone, tipo_usuario, senha_hash) VALUES (?, ?, ?, ?, ?, ?)";
        int idUsuarioGerado = -1;

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getCpf());
            stmt.setString(3, usuario.getDataNascimento().format(DateTimeFormatter.ISO_DATE)); 
            stmt.setString(4, usuario.getTelefone());
            stmt.setString(5, usuario.getTipoUsuario());
            stmt.setString(6, usuario.getSenhaHash());

            int rowsAffected = stmt.executeUpdate(); 
            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        idUsuarioGerado = rs.getInt(1);
                        usuario.setIdUsuario(idUsuarioGerado); 
                    }
                }
            }
        }
        return idUsuarioGerado;
    }

    public boolean cpfExiste(String cpf) throws SQLException {
        String sql = "SELECT COUNT(*) FROM usuario WHERE cpf = ?";
        try (Connection conn = ConexaoBanco.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public Usuario buscarUsuarioPorCpfETipo(String cpf, String tipoUsuario) throws SQLException {
        String sql = "SELECT id_usuario, nome, cpf, data_nascimento, telefone, tipo_usuario, senha_hash, otp_ativo, otp_expiracao FROM usuario WHERE cpf = ? AND tipo_usuario = ?";
        try (Connection conn = ConexaoBanco.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            stmt.setString(2, tipoUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id_usuario");
                    String nome = rs.getString("nome");
                    LocalDate dataNascimento = rs.getDate("data_nascimento").toLocalDate();
                    String telefone = rs.getString("telefone");
                    String senhaHash = rs.getString("senha_hash");
                    String otpAtivo = rs.getString("otp_ativo");
                    LocalDateTime otpExpiracao = rs.getTimestamp("otp_expiracao") != null ? rs.getTimestamp("otp_expiracao").toLocalDateTime() : null;

                    return new Usuario(id, nome, cpf, dataNascimento, telefone, tipoUsuario, senhaHash, otpAtivo, otpExpiracao);
                }
            }
        }
        return null;
    }

    public void atualizarOtpUsuario(int idUsuario, String otp, LocalDateTime otpExpiracao) throws SQLException {
        String sql = "UPDATE usuario SET otp_ativo = ?, otp_expiracao = ? WHERE id_usuario = ?";
        try (Connection conn = ConexaoBanco.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Define o OTP ativo (pode ser String ou null)
            stmt.setString(1, otp);

            // CORREÇÃO AQUI: Tratar otpExpiracao que pode ser null
            if (otpExpiracao != null) {
                stmt.setTimestamp(2, java.sql.Timestamp.valueOf(otpExpiracao));
            } else {
                // Se otpExpiracao for null, define a coluna como NULL no banco de dados para o tipo TIMESTAMP
                stmt.setNull(2, Types.TIMESTAMP); 
            }
            
            stmt.setInt(3, idUsuario);
            stmt.executeUpdate();
        }
    }

    public Usuario buscarUsuarioPorId(int idUsuario) throws SQLException {
        String sql = "SELECT id_usuario, nome, cpf, data_nascimento, telefone, tipo_usuario, senha_hash, otp_ativo, otp_expiracao FROM usuario WHERE id_usuario = ?";
        try (Connection conn = ConexaoBanco.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String nome = rs.getString("nome");
                    String cpf = rs.getString("cpf");
                    LocalDate dataNascimento = rs.getDate("data_nascimento").toLocalDate();
                    String telefone = rs.getString("telefone");
                    String tipoUsuario = rs.getString("tipo_usuario");
                    String senhaHash = rs.getString("senha_hash");
                    String otpAtivo = rs.getString("otp_ativo");
                    LocalDateTime otpExpiracao = rs.getTimestamp("otp_expiracao") != null ? rs.getTimestamp("otp_expiracao").toLocalDateTime() : null;

                    return new Usuario(idUsuario, nome, cpf, dataNascimento, telefone, tipoUsuario, senhaHash, otpAtivo, otpExpiracao);
                }
            }
        }
        return null;
    }
    public void atualizarTelefone(int idUsuario, String novoTelefone, Connection conn) throws SQLException {
        String sql = "UPDATE usuario SET telefone = ? WHERE id_usuario = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, novoTelefone);
            stmt.setInt(2, idUsuario);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
               
                throw new SQLException("Nenhum usuário encontrado ou telefone atualizado com ID: " + idUsuario);
            }
        }
    }
    public void atualizarSenha(int idUsuario, String novaSenhaHash, Connection conn) throws SQLException {
        String sql = "UPDATE usuario SET senha_hash = ? WHERE id_usuario = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, novaSenhaHash);
            stmt.setInt(2, idUsuario);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Nenhum usuário encontrado ou senha atualizada com ID: " + idUsuario);
            }
        }
    }
    public int inserirUsuario(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuario (nome, cpf, data_nascimento, telefone, tipo_usuario, senha_hash) VALUES (?, ?, ?, ?, ?, ?)";
        int idUsuarioGerado = -1;
        try (Connection conn = ConexaoBanco.getConnection();
        		PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getCpf());
            stmt.setString(3, usuario.getDataNascimento().format(DateTimeFormatter.ISO_DATE));
            stmt.setString(4, usuario.getTelefone());
            stmt.setString(5, usuario.getTipoUsuario());
            stmt.setString(6, usuario.getSenhaHash());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        idUsuarioGerado = rs.getInt(1);
                        usuario.setIdUsuario(idUsuarioGerado);
                    }
                }
            }
        }
        return idUsuarioGerado;
    }
}
