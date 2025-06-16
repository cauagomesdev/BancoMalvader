package com.bancomalvader.controller;

import com.bancomalvader.dao.AgenciaDAO;
import com.bancomalvader.dao.AuditoriaDAO;
import com.bancomalvader.dao.ClienteDAO;
import com.bancomalvader.dao.ContaDAO;
import com.bancomalvader.dao.EnderecoDAO;
import com.bancomalvader.dao.UsuarioDAO;
import com.bancomalvader.model.*; // Importa todas as classes do pacote model
import com.bancomalvader.util.ConexaoBanco; // Para transações
import com.bancomalvader.util.CpfValidator;
import com.bancomalvader.util.PasswordHasher;

import javax.swing.JOptionPane;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

public class ContaController {

    private UsuarioDAO usuarioDAO;
    private EnderecoDAO enderecoDAO;
    private ClienteDAO clienteDAO;
    private AgenciaDAO agenciaDAO;
    private ContaDAO contaDAO;
    private AuditoriaDAO auditoriaDAO;

    public ContaController() {
        this.usuarioDAO = new UsuarioDAO();
        this.enderecoDAO = new EnderecoDAO();
        this.clienteDAO = new ClienteDAO();
        this.agenciaDAO = new AgenciaDAO();
        this.contaDAO = new ContaDAO();
        this.auditoriaDAO = new AuditoriaDAO();
    }

    /**
     * Valida se um CPF já existe no sistema.
     * @param cpf O CPF a ser validado.
     * @return True se o CPF já existe, false caso contrário.
     */
    public boolean validarCpfExistente(String cpf) {
        try {
            return usuarioDAO.cpfExiste(cpf);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao validar CPF: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            return true; // Considera como existente para evitar duplicação em caso de erro no banco
        }
    }

    public boolean abrirConta(String nome, String cpf, LocalDate dataNascimento, String telefone,
                                String senha, String cep, String logradouro, int numeroCasa,String bairro, String cidade,
                                String estado, String complemento, String codigoAgencia, String tipoConta,
                                double taxaRendimentoCP, BigDecimal limiteCC, LocalDate dataVencimentoCC,
                                double taxaManutencaoCC, String perfilRiscoCI, double valorMinimoCI,
                                double taxaRendimentoBaseCI, int idFuncionarioResponsavel) {

        // 1. Validações de entrada de dados (básicas, mais completas seriam na UI ou Models)
        if (nome.isEmpty() || !CpfValidator.isValidFormat(cpf) || dataNascimento == null || telefone.isEmpty() ||
            senha.isEmpty() || cep.isEmpty() || logradouro.isEmpty() || numeroCasa <= 0 || bairro.isEmpty() ||
            cidade.isEmpty() || estado.isEmpty() || codigoAgencia.isEmpty() || tipoConta.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor, preencha todos os campos obrigatórios e verifique os formatos.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Validação de senha forte (antes de hashear)
        if (senha.length() < 8 || !senha.matches(".*[A-Z].*") || !senha.matches(".*\\d.*") || !senha.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            JOptionPane.showMessageDialog(null, "A senha deve ter pelo menos 8 caracteres, 1 letra maiúscula, 1 número e 1 caractere especial.", "Erro de Senha", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Hashing da senha
        String senhaHash = PasswordHasher.hashPasswordMD5(senha);

        Connection conn = null; // Conexão para a transação
        try {
            conn = ConexaoBanco.getConnection();
            conn.setAutoCommit(false); // Inicia a transação

            // 2. Verificar se o CPF já existe
            if (usuarioDAO.cpfExiste(cpf)) {
                JOptionPane.showMessageDialog(null, "CPF já cadastrado no sistema.", "Erro", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // 3. Buscar ou criar agência (assumimos que a agência já existe)
            Agencia agencia = agenciaDAO.buscarAgenciaPorCodigo(codigoAgencia);
            if (agencia == null) {
                 JOptionPane.showMessageDialog(null, "Agência com código " + codigoAgencia + " não encontrada.", "Erro", JOptionPane.ERROR_MESSAGE);
                 return false;
            }

            // 4. Inserir Usuário (como CLIENTE)
            Usuario novoUsuario = new Usuario(nome, cpf, dataNascimento, telefone, "CLIENTE", senhaHash);
            int idUsuario = usuarioDAO.inserirUsuario(novoUsuario);
            if (idUsuario == -1) {
                throw new SQLException("Falha ao inserir o usuário.");
            }

            // 5. Inserir Endereço para o novo usuário
            Endereco novoEndereco = new Endereco(idUsuario, cep, logradouro, numeroCasa, bairro, cidade, estado, complemento); // Usar String.valueOf(numeroCasa) se numeroCasa for int
            int idEndereco = enderecoDAO.inserirEndereco(novoEndereco);
            if (idEndereco == -1) {
                throw new SQLException("Falha ao inserir o endereço.");
            }

            // 6. Inserir Cliente para o novo usuário
            Cliente novoCliente = new Cliente(idUsuario, 0.0); // Score inicial 0.0
            int idCliente = clienteDAO.inserirCliente(novoCliente);
            if (idCliente == -1) {
                throw new SQLException("Falha ao inserir o cliente.");
            }

            // 7. Gerar número da conta
            String numeroConta = gerarNumeroContaAleatorio(); // Implemente um gerador de número de conta real

            // 8. Inserir Conta principal
            Conta novaConta = new Conta(numeroConta, agencia.getIdAgencia(), 0.0, tipoConta, idCliente, LocalDateTime.now(), "ATIVA");
            int idConta = contaDAO.inserirConta(novaConta, conn); // Passa a conexão da transação
            if (idConta == -1) {
                throw new SQLException("Falha ao inserir a conta principal.");
            }

            // 9. Inserir Detalhes da Conta Específica
            switch (tipoConta) {
                case "POUPANCA":
                    ContaPoupanca cp = new ContaPoupanca(idConta, taxaRendimentoCP, null);
                    contaDAO.inserirContaPoupanca(cp, conn); // Passa a conexão da transação
                    break;
                case "CORRENTE":
                    ContaCorrente cc = new ContaCorrente(idConta, limiteCC, dataVencimentoCC, taxaManutencaoCC);
                    contaDAO.inserirContaCorrente(cc, conn); // Passa a conexão da transação
                    break;
                case "INVESTIMENTO":
                    ContaInvestimento ci = new ContaInvestimento(idConta, perfilRiscoCI, valorMinimoCI, taxaRendimentoBaseCI);
                    contaDAO.inserirContaInvestimento(ci, conn); // Passa a conexão da transação
                    break;
            }

            // 10. Registrar Auditoria de Abertura de Conta
            String detalhesAuditoria = "Abertura de nova conta " + tipoConta + " para CPF: " + cpf + " (Número da Conta: " + numeroConta + ")";
            Auditoria auditoria = new Auditoria(idFuncionarioResponsavel, "ABERTURA_CONTA", LocalDateTime.now(), detalhesAuditoria);
            auditoriaDAO.registrarAuditoria(auditoria);

            conn.commit(); // Confirma todas as operações da transação
            JOptionPane.showMessageDialog(null, "Conta aberta com sucesso! Número da conta: " + numeroConta, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            return true;

        } catch (SQLException e) {
            // Em caso de erro, tenta fazer rollback da transação
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Erro ao fazer rollback da transação: " + rollbackEx.getMessage());
                }
            }
            JOptionPane.showMessageDialog(null, "Erro ao abrir conta: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); // Imprime o stack trace para depuração
            return false;
        } finally {
            ConexaoBanco.closeConnection(conn); // Fecha a conexão
        }
    }

    /**
     * Atualiza o telefone, endereço e/ou senha de um cliente.
     * Esta operação é uma transação para garantir atomicidade.
     * @param idUsuarioCliente ID do usuário (cliente) cujos dados serão atualizados.
     * @param novoTelefone Novo telefone. Pode ser null se não for para atualizar.
     * @param novoEndereco Objeto Endereco com os novos dados de endereço. Pode ser null se não for para atualizar.
     * @param novaSenhaTextoClaro Nova senha em texto claro. Será hasheada. Pode ser null se não for para atualizar.
     * @param idFuncionarioResponsavel ID do funcionário que está realizando a operação (para auditoria).
     * @return True se a atualização for bem-sucedida, false caso contrário.
     */
    public boolean atualizarDadosCliente(int idUsuarioCliente, String novoTelefone, Endereco novoEndereco, 
                                          String novaSenhaTextoClaro, int idFuncionarioResponsavel) {
        Connection conn = null;
        String acaoAuditoria = "ALTERACAO_CLIENTE";
        String detalhesAuditoria = "Alteração de dados para o usuário cliente ID: " + idUsuarioCliente;

        try {
            conn = ConexaoBanco.getConnection();
            conn.setAutoCommit(false); // Inicia a transação

            // 1. Atualizar Telefone (se fornecido)
            if (novoTelefone != null && !novoTelefone.trim().isEmpty()) {
                usuarioDAO.atualizarTelefone(idUsuarioCliente, novoTelefone.trim(), conn);
                detalhesAuditoria += " - Telefone atualizado.";
            }

            // 2. Atualizar Endereço (se fornecido)
            if (novoEndereco != null) {
                // Antes de atualizar, precisamos ter o id_endereco
                // Buscar o endereço atual do usuário
                Endereco enderecoAtual = enderecoDAO.buscarEnderecoPorUsuarioId(idUsuarioCliente);
                if (enderecoAtual != null) {
                    novoEndereco.setIdEndereco(enderecoAtual.getIdEndereco()); // Define o ID para a atualização
                    novoEndereco.setIdUsuario(idUsuarioCliente); // Garante que a FK está correta
                    enderecoDAO.atualizarEndereco(novoEndereco, conn);
                    detalhesAuditoria += " - Endereço atualizado.";
                } else {
                    // Se o endereço não existe, talvez seja uma inserção? Ou erro?
                    // Por enquanto, lançamos um erro se for atualização e o endereço não for encontrado
                    throw new SQLException("Endereço não encontrado para o usuário ID: " + idUsuarioCliente + ". Não foi possível atualizar.");
                }
            }

            // 3. Atualizar Senha (se fornecida)
            if (novaSenhaTextoClaro != null && !novaSenhaTextoClaro.trim().isEmpty()) {
                // Validação de senha forte para a nova senha
                if (novaSenhaTextoClaro.length() < 8 || !novaSenhaTextoClaro.matches(".*[A-Z].*") ||
                    !novaSenhaTextoClaro.matches(".*\\d.*") || !novaSenhaTextoClaro.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
                    throw new IllegalArgumentException("A nova senha deve ter pelo menos 8 caracteres, 1 letra maiúscula, 1 número e 1 caractere especial.");
                }
                String novaSenhaHash = PasswordHasher.hashPasswordMD5(novaSenhaTextoClaro);
                usuarioDAO.atualizarSenha(idUsuarioCliente, novaSenhaHash, conn);
                detalhesAuditoria += " - Senha atualizada.";
            }

            // 4. Registrar Auditoria
            Auditoria auditoria = new Auditoria(idFuncionarioResponsavel, acaoAuditoria, LocalDateTime.now(), detalhesAuditoria);
            auditoriaDAO.registrarAuditoria(auditoria);

            conn.commit(); // Confirma a transação
            JOptionPane.showMessageDialog(null, "Dados do cliente atualizados com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            return true;

        } catch (SQLException | IllegalArgumentException e) { // Captura SQLException e IllegalArgumentException
            if (conn != null) {
                try {
                    conn.rollback(); // Desfaz tudo em caso de erro
                    System.err.println("Rollback da transação de atualização de dados do cliente devido a erro: " + e.getMessage());
                } catch (SQLException rollbackEx) {
                    System.err.println("Erro ao fazer rollback: " + rollbackEx.getMessage());
                }
            }
            // Mensagem de erro para o usuário
            JOptionPane.showMessageDialog(null, "Erro ao atualizar dados do cliente: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        } finally {
            ConexaoBanco.closeConnection(conn); // Fecha a conexão
        }
    }


    /**
     * Gera um número de conta aleatório simples.
     * No futuro, implementar o algoritmo de Luhn para dígito verificador.
     * @return Um número de conta de 12 dígitos.
     */
    private String gerarNumeroContaAleatorio() {
        Random rand = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 11; i++) { // 11 dígitos para o número base
            sb.append(rand.nextInt(10));
        }
        // Adicionar um dígito verificador simples (não Luhn) para exemplo
        int digitoVerificador = rand.nextInt(10);
        sb.append(digitoVerificador);
        return sb.toString();
    }
}