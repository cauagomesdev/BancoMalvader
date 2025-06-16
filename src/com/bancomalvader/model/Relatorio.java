package com.bancomalvader.model;

import java.time.LocalDateTime;

public class Relatorio {
	private int idRelatorio;
	private int idFuncionario;
	private String tipoRelatorio;
	private LocalDateTime datageracao;
	private String conteudo;
	
	public Relatorio(int idRelatorio, int idFuncionario, String tipoRelatorio, LocalDateTime datageracao,
			String conteudo) {
	
		this.idRelatorio = idRelatorio;
		this.idFuncionario = idFuncionario;
		this.tipoRelatorio = tipoRelatorio;
		this.datageracao = datageracao;
		this.conteudo = conteudo;
	}

	public Relatorio(int idFuncionario, String tipoRelatorio, LocalDateTime datageracao, String conteudo) {
	
		this.idFuncionario = idFuncionario;
		this.tipoRelatorio = tipoRelatorio;
		this.datageracao = LocalDateTime.now();
		this.conteudo = conteudo;
	}

	public int getIdRelatorio() {
		return idRelatorio;
	}

	public void setIdRelatorio(int idRelatorio) {
		this.idRelatorio = idRelatorio;
	}

	public int getIdFuncionario() {
		return idFuncionario;
	}

	public void setIdFuncionario(int idFuncionario) {
		this.idFuncionario = idFuncionario;
	}

	public String getTipoRelatorio() {
		return tipoRelatorio;
	}

	public void setTipoRelatorio(String tipoRelatorio) {
		this.tipoRelatorio = tipoRelatorio;
	}

	public LocalDateTime getDatageracao() {
		return datageracao;
	}

	public void setDatageracao(LocalDateTime datageracao) {
		this.datageracao = datageracao;
	}

	public String getConteudo() {
		return conteudo;
	}

	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}

	@Override
	public String toString() {
		return "Relatorio [idRelatorio=" + idRelatorio + ", idFuncionario=" + idFuncionario + ", tipoRelatorio="
				+ tipoRelatorio + ", datageracao=" + datageracao + ", conteudo=" + conteudo + "]";
	}
	
}
