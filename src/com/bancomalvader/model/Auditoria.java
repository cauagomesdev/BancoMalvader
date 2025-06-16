package com.bancomalvader.model;

import java.time.LocalDateTime;

public class Auditoria {
	private int idAuditoria;
	private Integer idUsuario;
	private String acao;
	private LocalDateTime dataHora;
	private String detalhes;
	
	public Auditoria(int idAuditoria, Integer idUsuario, String acao, LocalDateTime dataHora, String detalhes) {
		
		this.idAuditoria = idAuditoria;
		this.idUsuario = idUsuario;
		this.acao = acao;
		this.dataHora = dataHora;
		this.detalhes = detalhes;
	}
	public Auditoria(Integer idUsuario, String acao, LocalDateTime dataHora, String detalhes) {
		
		this.idUsuario = idUsuario;
		this.acao = acao;
		this.dataHora = dataHora;
		this.detalhes = detalhes;
	}
	public int getIdAuditoria() {
		return idAuditoria;
	}
	public void setIdAuditoria(int idAuditoria) {
		this.idAuditoria = idAuditoria;
	}
	public Integer getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}
	public String getAcao() {
		return acao;
	}
	public void setAcao(String acao) {
		this.acao = acao;
	}
	public LocalDateTime getDataHora() {
		return dataHora;
	}
	public void setDataHora(LocalDateTime dataHora) {
		this.dataHora = dataHora;
	}
	public String getDetalhes() {
		return detalhes;
	}
	public void setDetalhes(String detalhes) {
		this.detalhes = detalhes;
	}
	@Override
	public String toString() {
		return "Auditoria [idAuditoria=" + idAuditoria + ", idUsuario=" + idUsuario + ", acao=" + acao + ", dataHora="
				+ dataHora + ", detalhes=" + detalhes + "]";
	}
	
	
}