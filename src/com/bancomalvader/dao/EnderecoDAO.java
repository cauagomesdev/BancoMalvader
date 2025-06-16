package com.bancomalvader.dao;

import com.bancomalvader.model.Endereco;
import com.bancomalvader.util.ConexaoBanco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class EnderecoDAO {

        public int inserirEndereco(Endereco endereco, Connection conn) throws SQLException {
        String sql = "INSERT INTO endereco (id_usuario, cep, local, numero_casa, bairro, cidade, estado, complemento) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        int idEnderecoGerado = -1;

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, endereco.getIdUsuario());
            stmt.setString(2, endereco.getCep());
            stmt.setString(3, endereco.getLogradouro());
            stmt.setInt(4, endereco.getNumeroCasa());
            stmt.setString(5, endereco.getBairro());
            stmt.setString(6, endereco.getCidade());
            stmt.setString(7, endereco.getEstado());
            stmt.setString(8, endereco.getComplemento());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        idEnderecoGerado = rs.getInt(1);
                        endereco.setIdEndereco(idEnderecoGerado);
                    }
                }
            }
        }
        return idEnderecoGerado;
    }
       public Endereco buscarEnderecoPorUsuarioId(int idUsuario) throws SQLException {
   		String sql = "SELECT id_endereco, id_usuario, cep, local, numero_casa, bairro, cidade, estado, complemento FROM endereco WHERE id_endereco = ?";
		
   		try (Connection conn = ConexaoBanco.getConnection();
   				PreparedStatement stmt = conn.prepareStatement(sql)){
   			stmt.setInt(1, idUsuario);
   			try (ResultSet rs = stmt.executeQuery()) {
   				if (rs.next()) {
   					int idEndereco = rs.getInt("id_endereco");
                    String cep = rs.getString("cep");
                    String logradouro = rs.getString("local"); 
                    int numeroCasa = rs.getInt("numero_casa");
                    String bairro = rs.getString("bairro");
                    String cidade = rs.getString("cidade");
                    String estado = rs.getString("estado");
                    String complemento = rs.getString("complemento");
                    
                    return new Endereco(idEndereco, idUsuario, cep, logradouro, numeroCasa, bairro, cidade, estado, complemento);
   				}
   			}
   		}
   		return null;
   	}
       public Endereco buscarEnderecoPorUsuarioId(int idUsuario, Connection conn) throws SQLException {
      		String sql = "SELECT id_endereco, id_usuario, cep, local, numero_casa, bairro, cidade, estado, complemento FROM endereco WHERE id_endereco = ?";
   		
      		try (PreparedStatement stmt = conn.prepareStatement(sql)){
      			stmt.setInt(1, idUsuario);
      			try (ResultSet rs = stmt.executeQuery()) {
      				if (rs.next()) {
      					int idEndereco = rs.getInt("id_endereco");
                       String cep = rs.getString("cep");
                       String logradouro = rs.getString("local"); 
                       int numeroCasa = rs.getInt("numero_casa");
                       String bairro = rs.getString("bairro");
                       String cidade = rs.getString("cidade");
                       String estado = rs.getString("estado");
                       String complemento = rs.getString("complemento");
                       
                       return new Endereco(idEndereco, idUsuario, cep, logradouro, numeroCasa, bairro, cidade, estado, complemento);
      				}
      			}
      		}
      		return null;
      	}
       public void atualizarEndereco(Endereco endereco, Connection conn) throws SQLException {
   		String sql = "UPDATE endereco SET cep = ?, local = ?, numero_casa = ?, bairro = ?, cidade = ?, estado = ?, complemento = ? WHERE id_endereco = ?";
   		   			
   			try(PreparedStatement stmt = conn.prepareStatement(sql)){
   			
   				stmt.setString(1, endereco.getCep());
   	            stmt.setString(2, endereco.getLogradouro());
   	            stmt.setInt(3, endereco.getNumeroCasa());
   	            stmt.setString(4, endereco.getBairro());
   	            stmt.setString(5, endereco.getCidade());
   	            stmt.setString(6, endereco.getEstado());
   	            stmt.setString(7, endereco.getComplemento());
   	            stmt.setInt(8, endereco.getIdEndereco());
   				
   				  int rowsAffected1 = stmt.executeUpdate();
   	                if (rowsAffected1 == 0) {
   	                    throw new SQLException("Nenhum endereço encontrado ou atualizado com ID: " + endereco.getIdEndereco());
   	        }
   		}
   	}
       public int inserirEndereco(Endereco endereco) throws SQLException {
           String sql = "INSERT INTO endereco (id_usuario, cep, local, numero_casa, bairro, cidade, estado, complemento) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
           int idEnderecoGerado = -1;

           try (Connection conn = ConexaoBanco.getConnection();
        		   PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

               stmt.setInt(1, endereco.getIdUsuario());
               stmt.setString(2, endereco.getCep());
               stmt.setString(3, endereco.getLogradouro());
               stmt.setInt(4, endereco.getNumeroCasa());
               stmt.setString(5, endereco.getBairro());
               stmt.setString(6, endereco.getCidade());
               stmt.setString(7, endereco.getEstado());
               stmt.setString(8, endereco.getComplemento());

               int rowsAffected = stmt.executeUpdate();
               if (rowsAffected > 0) {
                   try (ResultSet rs = stmt.getGeneratedKeys()) {
                       if (rs.next()) {
                           idEnderecoGerado = rs.getInt(1);
                           endereco.setIdEndereco(idEnderecoGerado);
                       }
                   }
               }
           }
           return idEnderecoGerado;
       }
}
