package com.bancomalvader.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Usuario {
    private int idUsuario;
    private String nome;
    private String cpf;
    private LocalDate dataNascimento; 
    private String telefone;
    private String tipoUsuario; 
    private String senhaHash;  
    private String otpAtivo;   
    private LocalDateTime otpExpiracao; 

    public Usuario(int idUsuario, String nome, String cpf, LocalDate dataNascimento,
                   String telefone, String tipoUsuario, String senhaHash,
                   String otpAtivo, LocalDateTime otpExpiracao) {
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.telefone = telefone;
        this.tipoUsuario = tipoUsuario;
        this.senhaHash = senhaHash;
        this.otpAtivo = otpAtivo;
        this.otpExpiracao = otpExpiracao;
    }

    public Usuario(String nome, String cpf, LocalDate dataNascimento,
                   String telefone, String tipoUsuario, String senhaHash) {
        this.nome = nome;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.telefone = telefone;
        this.tipoUsuario = tipoUsuario;
        this.senhaHash = senhaHash;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public String getOtpAtivo() {
        return otpAtivo;
    }

    public void setOtpAtivo(String otpAtivo) {
        this.otpAtivo = otpAtivo;
    }

    public LocalDateTime getOtpExpiracao() {
        return otpExpiracao;
    }

    public void setOtpExpiracao(LocalDateTime otpExpiracao) {
        this.otpExpiracao = otpExpiracao;
    }

    @Override
    public String toString() {
        return "Usuario{" +
               "idUsuario=" + idUsuario +
               ", nome='" + nome + '\'' +
               ", cpf='" + cpf + '\'' +
               ", dataNascimento=" + dataNascimento +
               ", telefone='" + telefone + '\'' +
               ", tipoUsuario='" + tipoUsuario + '\'' +
               ", otpAtivo='" + otpAtivo + '\'' +
               ", otpExpiracao=" + otpExpiracao +
               '}';
    }
}
