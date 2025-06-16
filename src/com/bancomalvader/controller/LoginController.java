package com.bancomalvader.controller;

import com.bancomalvader.dao.AuditoriaDAO;
import com.bancomalvader.dao.UsuarioDAO;
import com.bancomalvader.model.Auditoria;
import com.bancomalvader.model.Usuario;
import com.bancomalvader.util.PasswordHasher;

import java.sql.SQLException;
import java.time.LocalDateTime;

import javax.swing.JOptionPane;

public class LoginController {

    private UsuarioDAO usuarioDAO;
    private AuditoriaDAO auditoriaDAO; // Você precisará criar esta classe

    public LoginController() {
        this.usuarioDAO = new UsuarioDAO();
        this.auditoriaDAO = new AuditoriaDAO(); // Instancie o DAO de auditoria
    }

    public Usuario buscarUsuarioParaOtp(String cpf, String tipoUsuario) throws SQLException {
        return usuarioDAO.buscarUsuarioPorCpfETipo(cpf, tipoUsuario);
    }
    /**
     * Tenta autenticar um usuário.
     * @param cpf CPF do usuário.
     * @param senha Senha em texto claro.
     * @param otp OTP fornecido.
     * @param tipoUsuario Tipo de usuário ("FUNCIONARIO" ou "CLIENTE").
     * @return O objeto Usuario se o login for bem-sucedido, ou null caso contrário.
     */
    public Usuario autenticar(String cpf, String senha, String otp, String tipoUsuario) {
        Usuario usuario = null;
        String acaoAuditoria = "";
        String detalhesAuditoria = "";
        int idUsuarioAuditoria = -1; // -1 se o usuário não for encontrado

        try {
            usuario = usuarioDAO.buscarUsuarioPorCpfETipo(cpf, tipoUsuario);

            if (usuario == null) {
                acaoAuditoria = "LOGIN_FALHA";
                detalhesAuditoria = "CPF ou Tipo de Usuário não encontrado para CPF: " + cpf;
                return null; // Usuário não encontrado
            }

            idUsuarioAuditoria = usuario.getIdUsuario(); // Obtém o ID do usuário para auditoria

            // 1. Verificar a senha
            if (!PasswordHasher.checkPassword(senha, usuario.getSenhaHash())) {
                acaoAuditoria = "LOGIN_FALHA";
                detalhesAuditoria = "Senha incorreta para usuário: " + cpf;
                return null; // Senha incorreta
            }

            // 2. Verificar o OTP (se ativado)
            if (usuario.getOtpAtivo() != null && usuario.getOtpExpiracao() != null) {
                if (!usuario.getOtpAtivo().equals(otp)) {
                    acaoAuditoria = "LOGIN_FALHA";
                    detalhesAuditoria = "OTP incorreto para usuário: " + cpf;
                    return null; // OTP incorreto
                }
                if (LocalDateTime.now().isAfter(usuario.getOtpExpiracao())) {
                    acaoAuditoria = "LOGIN_FALHA";
                    detalhesAuditoria = "OTP expirado para usuário: " + cpf;
                    return null; // OTP expirado
                }
                // Limpar OTP após uso bem-sucedido (ou em cada login para gerar um novo)
                usuarioDAO.atualizarOtpUsuario(usuario.getIdUsuario(), null, null);
            } else {
                // Se o OTP não estiver ativo no banco, o usuário não precisa fornecer um
                // A validação de OTP só ocorre se otp_ativo e otp_expiracao não forem nulos.
                // Se o OTP é sempre gerado no login, aqui pode-se gerar um novo e pedir ao usuário.
                // Para este exemplo, estamos assumindo que o OTP é gerado antes e enviado ao usuário.
                // Ou, se o OTP é opcional, esta parte é pulada.
            }

            acaoAuditoria = "LOGIN_SUCESSO";
            detalhesAuditoria = "Login bem-sucedido para usuário: " + cpf;
            return usuario; // Login bem-sucedido

        } catch (SQLException e) {
            acaoAuditoria = "ERRO_SISTEMA_LOGIN";
            detalhesAuditoria = "Erro de SQL durante login para CPF " + cpf + ": " + e.getMessage();
            System.err.println("Erro de SQL no LoginController: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            // Registrar auditoria, mesmo em caso de exceção para tentativas (se idUsuarioAuditoria for -1, registrar assim mesmo)
            try {
                auditoriaDAO.registrarAuditoria(new Auditoria(idUsuarioAuditoria, acaoAuditoria, LocalDateTime.now(), detalhesAuditoria));
            } catch (SQLException ex) {
                System.err.println("Erro ao registrar auditoria de login: " + ex.getMessage());
            }
        }
    }

    /**
     * Gera e atualiza um OTP para o usuário.
     * @param idUsuario ID do usuário.
     * @throws SQLException Se ocorrer um erro no banco de dados.
     */
    public void gerarNovoOtp(int idUsuario) throws SQLException {
        // Isso chamaria um stored procedure no banco para gerar o OTP
        // Ou geraria no Java e atualizaria via DAO
        // Por simplicidade aqui, vamos simular a geração e atualização via DAO
        String novoOtp = String.format("%06d", new java.util.Random().nextInt(1000000));
        LocalDateTime expiracao = LocalDateTime.now().plusMinutes(5); // Válido por 5 minutos
        usuarioDAO.atualizarOtpUsuario(idUsuario, novoOtp, expiracao);
        // Em um sistema real, o OTP seria enviado ao usuário (SMS, e-mail, etc.)
        System.out.println("OTP gerado para o usuário " + idUsuario + ": " + novoOtp + " (Válido até: " + expiracao + ")");
        JOptionPane.showMessageDialog(null, "Um novo OTP foi gerado para o seu CPF. Por favor, verifique.", "OTP Gerado", JOptionPane.INFORMATION_MESSAGE);
    }
}