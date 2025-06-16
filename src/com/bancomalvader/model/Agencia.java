package com.bancomalvader.model;

public class Agencia {
	private int idAgencia;
	private String nome;
	private String codigoAgencia;
	private int enderecoId;
	
	public Agencia(int idAgencia, String nome, String codigoAgencia, int enderecoId) {
		
		this.idAgencia = idAgencia;
		this.nome = nome;
		this.codigoAgencia = codigoAgencia;
		this.enderecoId = enderecoId;
	}
	public Agencia(String nome, String codigoAgencia, int enderecoId) {
		
		this.nome = nome;
		this.codigoAgencia = codigoAgencia;
		this.enderecoId = enderecoId;
	}
	public int getIdAgencia() {
		return idAgencia;
	}
	public void setIdAgencia(int idAgencia) {
		this.idAgencia = idAgencia;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getCodigoAgencia() {
		return codigoAgencia;
	}
	public void setCodigoAgencia(String codigoAgencia) {
		this.codigoAgencia = codigoAgencia;
	}
	public int getEnderecoId() {
		return enderecoId;
	}
	public void setEnderecoId(int enderecoId) {
		this.enderecoId = enderecoId;
	}
	@Override
	public String toString() {
		return "Agencia [idAgencia=" + idAgencia + ", nome=" + nome + ", codigoAgencia=" + codigoAgencia
				+ ", enderecoId=" + enderecoId + "]";
	}
	
	
}
