package com.bancomalvader.dao;

import com.bancomalvader.model.Auditoria;
import com.bancomalvader.util.ConexaoBanco;

import java.sql.Connection;
import java.sql.Date; 
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types; 
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AuditoriaDAO {

    public void registrarAuditoria(Auditoria auditoria, Connection conn) throws SQLException {
        String sql = "INSERT INTO auditoria (id_usuario, acao, data_hora, detalhes) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (auditoria.getIdUsuario() != null) {
                stmt.setInt(1, auditoria.getIdUsuario());
            } else {
                stmt.setNull(1, Types.INTEGER);
            }
            stmt.setString(2, auditoria.getAcao());
            stmt.setTimestamp(3, java.sql.Timestamp.valueOf(auditoria.getDataHora()));
            stmt.setString(4, auditoria.getDetalhes());

            stmt.executeUpdate(); 
        }
    }

    public List<Auditoria> buscarAuditoriaPorUsuario(int idUsuario) throws SQLException {
        String sql = "SELECT id_auditoria, id_usuario, acao, data_hora, detalhes FROM auditoria WHERE id_usuario = ? ORDER BY data_hora DESC";
        List<Auditoria> auditoriaPorId = new ArrayList<>(); 

        try (Connection conn = ConexaoBanco.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) { 
                    int idAuditoria = rs.getInt("id_auditoria");
                    Integer idUsuarioDb = (Integer) rs.getObject("id_usuario");
                    String acao = rs.getString("acao");
                    LocalDateTime dataHora = rs.getTimestamp("data_hora").toLocalDateTime();
                    String detalhes = rs.getString("detalhes");
                  
                    Auditoria auditoria = new Auditoria(idAuditoria, idUsuarioDb, acao, dataHora, detalhes);
                    auditoriaPorId.add(auditoria);
                }
            }
        }
        return auditoriaPorId;
    }

    public List<Auditoria> listarAuditorias() throws SQLException {
        String sql = "SELECT id_auditoria, id_usuario, acao, data_hora, detalhes FROM auditoria ORDER BY data_hora DESC";
        List<Auditoria> auditoriaList = new ArrayList<>();         
        try (Connection conn = ConexaoBanco.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            try(ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int idAuditoria = rs.getInt("id_auditoria");
                    Integer idUsuario = (Integer) rs.getObject("id_usuario");                    
                    String acao = rs.getString("acao");
                    LocalDateTime dataHora = rs.getTimestamp("data_hora").toLocalDateTime();
                    String detalhes = rs.getString("detalhes");
                    
                    Auditoria auditoria = new Auditoria(idAuditoria, idUsuario, acao, dataHora, detalhes);
                    auditoriaList.add(auditoria);
                }
            }
        }
        return auditoriaList;
    }
    
    public List<Auditoria> buscarAuditoriasPorPeriodo(LocalDate dataInicio, LocalDate dataFim) throws SQLException {
        String sql = "SELECT id_auditoria, id_usuario, acao, data_hora, detalhes FROM auditoria WHERE DATE(data_hora) BETWEEN ? AND ? ORDER BY data_hora DESC";
        List<Auditoria> auditoriasPP = new ArrayList<>();
        
        try (Connection conn = ConexaoBanco.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){
        
            stmt.setDate(1, Date.valueOf(dataInicio)); 
            stmt.setDate(2, Date.valueOf(dataFim));   
            
            try(ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) { 
                    int idAuditoria = rs.getInt("id_auditoria");
                    Integer idUsuario = (Integer) rs.getObject("id_usuario"); 
                    String acao = rs.getString("acao");
                    LocalDateTime dataHora = rs.getTimestamp("data_hora").toLocalDateTime();
                    String detalhes = rs.getString("detalhes");
                    
                    Auditoria auditoria = new Auditoria(idAuditoria, idUsuario, acao, dataHora, detalhes);
                    auditoriasPP.add(auditoria);
                }
            }
        }
        return auditoriasPP;
    }
    public void registrarAuditoria(Auditoria auditoria) throws SQLException {
        String sql = "INSERT INTO auditoria (id_usuario, acao, data_hora, detalhes) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexaoBanco.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (auditoria.getIdUsuario() != null) {
                stmt.setInt(1, auditoria.getIdUsuario());
            } else {
                stmt.setNull(1, Types.INTEGER);
            }
            stmt.setString(2, auditoria.getAcao());
            stmt.setTimestamp(3, java.sql.Timestamp.valueOf(auditoria.getDataHora()));
            stmt.setString(4, auditoria.getDetalhes());

            stmt.executeUpdate(); 
        }
    }
}
