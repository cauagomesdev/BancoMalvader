package com.bancomalvader.model;

public class Endereco {
	private int idEndereco;
	private int idUsuario;
	private String cep;
	private String logradouro;
	private int numeroCasa;
	private String bairro;
	private String cidade;
	private String estado;
	private String complemento;
	
	public Endereco(int idEndereco, int idUsuario, String cep, String logradouro, int numeroCasa, String bairro,
			String cidade, String estado, String complemento) {
	
		this.idEndereco = idEndereco;
		this.idUsuario = idUsuario;
		this.cep = cep;
		this.logradouro = logradouro;
		this.numeroCasa = numeroCasa;
		this.bairro = bairro;
		this.cidade = cidade;
		this.estado = estado;
		this.complemento = complemento;
	}

	public Endereco(int idUsuario, String cep, String logradouro, int numeroCasa, String bairro, String cidade,
			String estado, String complemento) {
	
		this.idUsuario = idUsuario;
		this.cep = cep;
		this.logradouro = logradouro;
		this.numeroCasa = numeroCasa;
		this.bairro = bairro;
		this.cidade = cidade;
		this.estado = estado;
		this.complemento = complemento;
	}

	public int getIdEndereco() {
		return idEndereco;
	}

	public void setIdEndereco(int idEndereco) {
		this.idEndereco = idEndereco;
	}

	public int getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	public int getNumeroCasa() {
		return numeroCasa;
	}

	public void setNumeroCasa(int numeroCasa) {
		this.numeroCasa = numeroCasa;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	@Override
	public String toString() {
		return "Endereco [idEndereco=" + idEndereco + ", idUsuario=" + idUsuario + ", cep=" + cep + ", logradouro="
				+ logradouro + ", numeroCasa=" + numeroCasa + ", bairro=" + bairro + ", cidade=" + cidade + ", estado="
				+ estado + ", complemento=" + complemento + "]";
	}
	
}
