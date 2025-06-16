package com.bancomalvader.controller;

import com.bancomalvader.dao.UsuarioDAO;
import com.bancomalvader.dao.EnderecoDAO;
import com.bancomalvader.dao.ClienteDAO;
import com.bancomalvader.dao.FuncionarioDAO;
import com.bancomalvader.dao.AuditoriaDAO;

import com.bancomalvader.model.Usuario;
import com.bancomalvader.model.Endereco;
import com.bancomalvader.model.Cliente;
import com.bancomalvader.model.Funcionario;
import com.bancomalvader.model.Auditoria; // Necessário para registrar auditoria

import com.bancomalvader.util.ConexaoBanco;
import com.bancomalvader.util.CpfValidator;
import com.bancomalvader.util.PasswordHasher;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class UsuarioController {

    private final UsuarioDAO usuarioDAO;
    private final EnderecoDAO enderecoDAO;
    private final ClienteDAO clienteDAO;
    private final FuncionarioDAO funcionarioDAO;
    private final AuditoriaDAO auditoriaDAO;

    public UsuarioController() {
        this.usuarioDAO = new UsuarioDAO();
        this.enderecoDAO = new EnderecoDAO();
        this.clienteDAO = new ClienteDAO();
        this.funcionarioDAO = new FuncionarioDAO();
        this.auditoriaDAO = new AuditoriaDAO();
    }

    /**
     * CADASTRO COMPLETO: Cadastra um novo usuário (Funcionário ou Cliente) com seu endereço e perfil específico.
     * Esta operação é uma transação atômica.
     * @param nome Nome do usuário.
     * @param cpf CPF do usuário.
     * @param dataNascimento Data de nascimento do usuário.
     * @param telefone Telefone do usuário.
     * @param senha Senha do usuário (texto claro).
     * @param tipoUsuario Tipo de usuário ("FUNCIONARIO" ou "CLIENTE").
     * @param cep CEP do endereço.
     * @param logradouro Logradouro do endereço.
     * @param numeroCasa Número da casa.
     * @param bairro Bairro do endereço.
     * @param cidade Cidade do endereço.
     * @param estado Estado do endereço.
     * @param complemento Complemento do endereço (opcional).
     * @param codigoFuncionario (Opcional, apenas para FUNCIONARIO) Código único do funcionário.
     * @param cargoFuncionario (Opcional, apenas para FUNCIONARIO) Cargo do funcionário.
     * @param idSupervisorFuncionario (Opcional, apenas para FUNCIONARIO) ID do supervisor.
     * @return True se o cadastro foi bem-sucedido, false caso contrário.
     * @throws IllegalArgumentException Se alguma validação de dados falhar.
     * @throws SQLException Se ocorrer um erro no acesso ao banco de dados.
     */
    public boolean cadastrarUsuarioCompleto(String nome, String cpf, LocalDate dataNascimento, String telefone,
                                            String senha, String tipoUsuario,
                                            String cep, String logradouro, int numeroCasa, String bairro, String cidade,
                                            String estado, String complemento,
                                            String codigoFuncionario, String cargoFuncionario, Integer idSupervisorFuncionario)
                                            throws IllegalArgumentException, SQLException {

        // Validações (já presentes)
        if (nome.isEmpty() || !CpfValidator.isValidFormat(cpf) || dataNascimento == null || telefone.isEmpty() ||
            senha.isEmpty() || tipoUsuario.isEmpty() ||
            cep.isEmpty() || logradouro.isEmpty() || numeroCasa <= 0 || bairro.isEmpty() ||
            cidade.isEmpty() || estado.isEmpty()) {
            throw new IllegalArgumentException("Por favor, preencha todos os campos obrigatórios e verifique os formatos.");
        }
        
        if ("FUNCIONARIO".equalsIgnoreCase(tipoUsuario)) {
            if (codigoFuncionario == null || codigoFuncionario.isEmpty() || cargoFuncionario == null || cargoFuncionario.isEmpty()) {
                throw new IllegalArgumentException("Para funcionário, Código e Cargo são obrigatórios.");
            }
        }

        if (senha.length() < 8 || !senha.matches(".*[A-Z].*") || !senha.matches(".*\\d.*") || !senha.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            throw new IllegalArgumentException("A senha deve ter pelo menos 8 caracteres, 1 letra maiúscula, 1 número e 1 caractere especial.");
        }

        String senhaHash = PasswordHasher.hashPasswordMD5(senha);

        Connection conn = null; // Conexão para a transação
        try {
            conn = ConexaoBanco.getConnection();
            conn.setAutoCommit(false);

            if (usuarioDAO.cpfExiste(cpf)) {
                throw new IllegalArgumentException("CPF já cadastrado no sistema.");
            }

            Usuario novoUsuario = new Usuario(nome, cpf, dataNascimento, telefone, tipoUsuario, senhaHash);
            int idUsuario = usuarioDAO.inserirUsuario(novoUsuario, conn); // Passa a conexão
            if (idUsuario == -1) { throw new SQLException("Falha ao inserir o usuário."); }
            novoUsuario.setIdUsuario(idUsuario);

            Endereco novoEndereco = new Endereco(idUsuario, cep, logradouro, numeroCasa, bairro, cidade, estado, complemento);
            int idEndereco = enderecoDAO.inserirEndereco(novoEndereco, conn); // Passa a conexão
            if (idEndereco == -1) { throw new SQLException("Falha ao inserir o endereço."); }
            novoEndereco.setIdEndereco(idEndereco);

            String acaoAuditoria = "";
            if ("CLIENTE".equalsIgnoreCase(tipoUsuario)) {
                Cliente novoCliente = new Cliente(idUsuario, 0.0);
                int idCliente = clienteDAO.inserirCliente(novoCliente, conn); // Passa a conexão
                if (idCliente == -1) { throw new SQLException("Falha ao inserir o cliente."); }
                novoCliente.setIdCliente(idCliente);
                acaoAuditoria = "CADASTRO_CLIENTE";
            } else if ("FUNCIONARIO".equalsIgnoreCase(tipoUsuario)) {
                Funcionario novoFuncionario = new Funcionario(idUsuario, codigoFuncionario, cargoFuncionario, idSupervisorFuncionario);
                int idFuncionario = funcionarioDAO.inserirFuncionario(novoFuncionario, conn); // Passa a conexão
                if (idFuncionario == -1) { throw new SQLException("Falha ao inserir o funcionário."); }
                novoFuncionario.setIdFuncionario(idFuncionario);
                acaoAuditoria = "CADASTRO_FUNCIONARIO";
            } else {
                throw new IllegalArgumentException("Tipo de usuário inválido.");
            }

            Auditoria auditoria = new Auditoria(novoUsuario.getIdUsuario(), acaoAuditoria, LocalDateTime.now(), "Cadastro de novo usuário " + tipoUsuario + " para CPF: " + cpf);
            auditoriaDAO.registrarAuditoria(auditoria); // AuditoriaDAO também deve aceitar conn ou ter auto-commit

            conn.commit();
            return true;

        } catch (IllegalArgumentException | SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException rollbackEx) { System.err.println("Erro ao fazer rollback: " + rollbackEx.getMessage()); }
            }
            throw e;
        } finally {
            ConexaoBanco.closeConnection(conn);
        }
    }


    /**
     * CADASTRO SIMPLIFICADO: Cadastra apenas um novo usuário básico (sem endereço, cliente/funcionário).
     * Este método gerencia sua própria conexão, pois é uma operação isolada.
     * @param nome Nome do usuário.
     * @param cpf CPF do usuário.
     * @param dataNascimento Data de nascimento do usuário.
     * @param telefone Telefone do usuário.
     * @param senha Senha do usuário (texto claro).
     * @param tipoUsuario Tipo de usuário ("FUNCIONARIO" ou "CLIENTE").
     * @return True se o cadastro foi bem-sucedido.
     * @throws IllegalArgumentException Se alguma validação de dados falhar.
     * @throws SQLException Se ocorrer um erro no acesso ao banco de dados.
     */
    public boolean cadastrarApenasUsuario(String nome, String cpf, LocalDate dataNascimento, String telefone,
                                          String senha, String tipoUsuario)
                                          throws IllegalArgumentException, SQLException {
        // 1. Validações de entrada de dados (básicas)
        if (nome.isEmpty() || !CpfValidator.isValidFormat(cpf) || dataNascimento == null || telefone.isEmpty() ||
            senha.isEmpty() || tipoUsuario.isEmpty()) {
            throw new IllegalArgumentException("Por favor, preencha todos os campos obrigatórios para o usuário.");
        }
        
        // Validação de senha forte
        if (senha.length() < 8 || !senha.matches(".*[A-Z].*") || !senha.matches(".*\\d.*") || !senha.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            throw new IllegalArgumentException("A senha deve ter pelo menos 8 caracteres, 1 letra maiúscula, 1 número e 1 caractere especial.");
        }

        // Hashing da senha
        String senhaHash = PasswordHasher.hashPasswordMD5(senha);

        try {
            // 2. Verificar se o CPF já existe
            if (usuarioDAO.cpfExiste(cpf)) { // Este método usa a própria conexão, ok
                throw new IllegalArgumentException("CPF já cadastrado no sistema.");
            }

            // 3. Inserir Usuário
            Usuario novoUsuario = new Usuario(nome, cpf, dataNascimento, telefone, tipoUsuario, senhaHash);
            // Este método DEVE usar uma nova conexão ou ter uma sobrecarga sem o 'conn'
            // No UsuarioDAO, crie uma sobrecarga 'inserirUsuario(Usuario usuario)'
            int idUsuario = usuarioDAO.inserirUsuario(novoUsuario); 
            
            if (idUsuario == -1) { throw new SQLException("Falha ao inserir o usuário."); }
            novoUsuario.setIdUsuario(idUsuario);

            // 4. Registrar Auditoria
            String acaoAuditoria = "CADASTRO_USUARIO_SIMPLIFICADO";
            String detalhesAuditoria = "Cadastro básico de usuário " + tipoUsuario + " para CPF: " + cpf;
            // AuditoriaDAO.registrarAuditoria geralmente aceita sua própria conexão (auto-commit), ok.
            auditoriaDAO.registrarAuditoria(new Auditoria(novoUsuario.getIdUsuario(), acaoAuditoria, LocalDateTime.now(), detalhesAuditoria));

            return true; // Sucesso

        } catch (IllegalArgumentException | SQLException e) {
            // Não há rollback aqui, pois a operação é individual por DAO
            throw e; // Relança a exceção para ser tratada pela camada View/UI
        }
    }
}
