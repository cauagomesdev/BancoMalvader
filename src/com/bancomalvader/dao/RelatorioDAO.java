package com.bancomalvader.dao;

import com.bancomalvader.model.Relatorio;
import com.bancomalvader.util.ConexaoBanco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date; 
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelatorioDAO {
	
 int inserirRelatorio(Relatorio relatorio) throws SQLException {
		String sql = "INSERT INTO relatorio (id_funcionario, tipo_relatorio, data_geracao, conteudo) VALUES (?, ?, ?, ?)";
		int idRelatorioGerado = -1;
		
		try (Connection conn = ConexaoBanco.getConnection();
			PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			
			stmt.setInt(1, relatorio.getIdFuncionario());
			stmt.setString(2, relatorio.getTipoRelatorio());
			stmt.setTimestamp(3, java.sql.Timestamp.valueOf(relatorio.getDatageracao())); 
			stmt.setString(4, relatorio.getConteudo());
			
			int rowsAffected = stmt.executeUpdate();
			if (rowsAffected > 0) {
				try (ResultSet rs = stmt.getGeneratedKeys()) {
					if (rs.next()) {
						idRelatorioGerado = rs.getInt(1);
						relatorio.setIdRelatorio(idRelatorioGerado);
					}
				}
			}
		}
		return idRelatorioGerado;
	}
	
	public List<Relatorio> buscarRelatoriosPorFuncionario(int idFuncionario) throws SQLException {
        String sql = "SELECT id_relatorio, id_funcionario, tipo_relatorio, data_geracao, conteudo FROM relatorio WHERE id_funcionario = ? ORDER BY data_geracao DESC";
        List<Relatorio> relatoriosPorFuncionario = new ArrayList<>(); 

        try (Connection conn = ConexaoBanco.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idFuncionario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) { 
                    int idRelatorio = rs.getInt("id_relatorio");
                    String tipoRelatorio = rs.getString("tipo_relatorio");
                    LocalDateTime dataGeracao = rs.getTimestamp("data_geracao").toLocalDateTime();
                    String conteudo = rs.getString("conteudo");
                  
                    Relatorio relatorio = new Relatorio(idRelatorio, idFuncionario, tipoRelatorio, dataGeracao, conteudo);
                    relatoriosPorFuncionario.add(relatorio);
                }
            }
        }
        return relatoriosPorFuncionario;
    }

	public List<Map<String, Object>> gerarRelatorioMovimentacoes(LocalDate inicio, LocalDate fim, String tipoTransacao, int idAgencia) throws SQLException {
    
        StringBuilder sqlBuilder = new StringBuilder( 
        	"SELECT id_transacao, numero_conta_origem, nome_cliente_origem, numero_conta_destino, nome_cliente_destino, tipo_transacao, valor, data_hora, descricao " +
            "FROM vw_movimentacoes_recentes " +
            "WHERE DATE(data_hora) BETWEEN ? AND ?");


        List<Object> params = new ArrayList<>();
        params.add(Date.valueOf(inicio)); 
        params.add(Date.valueOf(fim));  
        
        if (tipoTransacao != null && !tipoTransacao.isEmpty() && !"TODOS".equalsIgnoreCase(tipoTransacao)) {
            sqlBuilder.append(" AND tipo_transacao = ?");
            params.add(tipoTransacao);
        }
          
        if (idAgencia > 0) {
              
            sqlBuilder.append(" AND (id_agencia_origem = ? OR id_agencia_destino = ?)"); 
            params.add(idAgencia);
            params.add(idAgencia); 
        }
        
        sqlBuilder.append(" ORDER BY data_hora DESC");

        List<Map<String, Object>> reportData = new ArrayList<>();
        
        try (Connection conn = ConexaoBanco.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlBuilder.toString())) {
            
        	for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i)); 
            }

            try (ResultSet rs = stmt.executeQuery()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnLabel(i);
                        Object value = rs.getObject(i);
                        row.put(columnName, value);
                    }
                    reportData.add(row);
                }
            }
        }
        return reportData;
    }

	public List<Map<String, Object>> gerarRelatorioInadimplencia() throws SQLException {
        String sql = "SELECT u.nome AS nome_cliente, u.cpf, u.telefone, co.numero_conta, co.saldo, co.tipo_conta, cc.limite AS limite_cheque_especial, cc.taxa_manutencao " + 
                     "FROM cliente cl " +
                     "JOIN usuario u ON cl.id_usuario = u.id_usuario "+
                     "JOIN conta co ON cl.id_cliente = co.id_cliente "+
                     "LEFT JOIN conta_corrente cc ON co.id_conta = cc.id_conta AND co.tipo_conta = 'CORRENTE' "+
                     "WHERE co.saldo < 0 OR (co.tipo_conta = 'CORRENTE' AND co.saldo < -cc.limite) "+
                     "ORDER BY u.nome, co.numero_conta"; 

        List<Map<String, Object>> reportData = new ArrayList<>();

        try (Connection conn = ConexaoBanco.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = stmt.executeQuery()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnLabel(i);
                        Object value = rs.getObject(i);
                        row.put(columnName, value);
                    }
                    reportData.add(row);
                }
            }
        }
        return reportData;
    }
}
