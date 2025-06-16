package com.bancomalvader.model;

public class ContaInvestimento {
	private int idContaInvestimento;
	private int idConta;
	private String perfilRisco;
	private double valorMinimo;
	private double taxaRendimentoBase;
	
	public ContaInvestimento(int idContaInvestimento, int idConta, String perfilRisco, double valorMinimo,
			double taxaRendimentoBase) {
		
		this.idContaInvestimento = idContaInvestimento;
		this.idConta = idConta;
		this.perfilRisco = perfilRisco;
		this.valorMinimo = valorMinimo;
		this.taxaRendimentoBase = taxaRendimentoBase;
	}
	public ContaInvestimento(int idConta, String perfilRisco, double valorMinimo, double taxaRendimentoBase) {
		
		this.idConta = idConta;
		this.perfilRisco = perfilRisco;
		this.valorMinimo = valorMinimo;
		this.taxaRendimentoBase = taxaRendimentoBase;
	}
	public int getIdContaInvestimento() {
		return idContaInvestimento;
	}
	public void setIdContaInvestimento(int idContaInvestimento) {
		this.idContaInvestimento = idContaInvestimento;
	}
	public int getIdConta() {
		return idConta;
	}
	public void setIdConta(int idConta) {
		this.idConta = idConta;
	}
	public String getPerfilRisco() {
		return perfilRisco;
	}
	public void setPerfilRisco(String perfilRisco) {
		this.perfilRisco = perfilRisco;
	}
	public double getValorMinimo() {
		return valorMinimo;
	}
	public void setValorMinimo(double valorMinimo) {
		this.valorMinimo = valorMinimo;
	}
	public double getTaxaRendimentoBase() {
		return taxaRendimentoBase;
	}
	public void setTaxaRendimentoBase(double taxaRendimentoBase) {
		this.taxaRendimentoBase = taxaRendimentoBase;
	}
	@Override
	public String toString() {
		return "ContaInvestimento [idContaInvestimento=" + idContaInvestimento + ", idConta=" + idConta
				+ ", perfilRisco=" + perfilRisco + ", valorMinimo=" + valorMinimo + ", taxaRendimentoBase="
				+ taxaRendimentoBase + "]";
	}
	
	
}
