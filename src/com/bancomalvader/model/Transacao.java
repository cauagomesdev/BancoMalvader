package com.bancomalvader.model;

import java.time.LocalDateTime;

public class Transacao {
	private int idTransacao;
	private int idContaOrigem;
	private Integer idContaDestino;
	private String tipoTransacao;
	private double valor;
	private LocalDateTime dataHora;
	private String descricao;
	
	public Transacao(int idTransacao, int idContaOrigem, Integer idContaDestino, String tipoTransacao, double valor,
			LocalDateTime dataHora, String descricao) {
		this.idTransacao = idTransacao;
		this.idContaOrigem = idContaOrigem;
		this.idContaDestino = idContaDestino;
		this.tipoTransacao = tipoTransacao;
		this.valor = valor;
		this.dataHora = dataHora;
		this.descricao = descricao;
	}

	public Transacao(int idContaOrigem, Integer idContaDestino, String tipoTransacao, double valor,
			LocalDateTime dataHora, String descricao) {
		this.idContaOrigem = idContaOrigem;
		this.idContaDestino = idContaDestino;
		this.tipoTransacao = tipoTransacao;
		this.valor = valor;
		this.dataHora = LocalDateTime.now();
		this.descricao = descricao;
	}

	public int getIdTransacao() {
		return idTransacao;
	}

	public void setIdTransacao(int idTransacao) {
		this.idTransacao = idTransacao;
	}

	public int getIdContaOrigem() {
		return idContaOrigem;
	}

	public void setIdContaOrigem(int idContaOrigem) {
		this.idContaOrigem = idContaOrigem;
	}

	public Integer getIdContaDestino() {
		return idContaDestino;
	}

	public void setIdContaDestino(Integer idContaDestino) {
		this.idContaDestino = idContaDestino;
	}

	public String getTipoTransacao() {
		return tipoTransacao;
	}

	public void setTipoTransacao(String tipoTransacao) {
		this.tipoTransacao = tipoTransacao;
	}

	public double getValor() {
		return valor;
	}

	public void setValor(double valor) {
		this.valor = valor;
	}

	public LocalDateTime getDataHora() {
		return dataHora;
	}

	public void setDataHora(LocalDateTime dataHora) {
		this.dataHora = dataHora;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Override
	public String toString() {
		return "Transacao [idTransacao=" + idTransacao + ", idContaOrigem=" + idContaOrigem + ", idContaDestino="
				+ idContaDestino + ", tipoTransacao=" + tipoTransacao + ", valor=" + valor + ", dataHora=" + dataHora
				+ ", descricao=" + descricao + "]";
	}
	
}
