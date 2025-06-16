package com.bancomalvader.model;

import java.time.LocalDateTime;

public class ContaPoupanca {
	private int idContaPoupanca;
	private int idConta;
	private double taxaRendimento;
	private LocalDateTime ultimoRendimento;
	
	public ContaPoupanca(int idContaPoupanca, int idConta, double taxaRendimento, LocalDateTime ultimoRendimento) {
		
		this.idContaPoupanca = idContaPoupanca;
		this.idConta = idConta;
		this.taxaRendimento = taxaRendimento;
		this.ultimoRendimento = ultimoRendimento;
	}
	public ContaPoupanca(int idConta, double taxaRendimento, LocalDateTime ultimoRendimento) {
		
		this.idConta = idConta;
		this.taxaRendimento = taxaRendimento;
		this.ultimoRendimento = ultimoRendimento;
	}
	public int getIdContaPoupanca() {
		return idContaPoupanca;
	}
	public void setIdContaPoupanca(int idContaPoupanca) {
		this.idContaPoupanca = idContaPoupanca;
	}
	public int getIdConta() {
		return idConta;
	}
	public void setIdConta(int idConta) {
		this.idConta = idConta;
	}
	public double getTaxaRendimento() {
		return taxaRendimento;
	}
	public void setTaxaRendimento(double taxaRendimento) {
		this.taxaRendimento = taxaRendimento;
	}
	public LocalDateTime getUltimoRendimento() {
		return ultimoRendimento;
	}
	public void setUltimoRendimento(LocalDateTime ultimoRendimento) {
		this.ultimoRendimento = ultimoRendimento;
	}
	@Override
	public String toString() {
		return "contaPoupanca [idContaPoupanca=" + idContaPoupanca + ", idConta=" + idConta + ", taxaRendimento="
				+ taxaRendimento + ", ultimoRendimento=" + ultimoRendimento + "]";
	}
	
	
}
