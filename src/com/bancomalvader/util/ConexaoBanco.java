package com.bancomalvader.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBanco {
   
    private static final String URL = "jdbc:mysql://localhost:3306/bancomalvader?useTimezone=true&serverTimezone=UTC";
    private static final String USUARIO = "root"; // Substitua pelo seu usuário MySQL
    private static final String SENHA = "senha";   // Substitua pela sua senha MySQL

    public static Connection getConnection() throws SQLException {
        try {
           
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            return DriverManager.getConnection(URL, USUARIO, SENHA);
        } catch (ClassNotFoundException e) {
           
            System.err.println("Erro: Driver JDBC do MySQL não encontrado. Certifique-se de que o JAR está no classpath.");
            throw new SQLException("Erro ao carregar o driver do banco de dados.", e);
        }
    }

       public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão com o banco de dados: " + e.getMessage());
            }
        }
    }
}
