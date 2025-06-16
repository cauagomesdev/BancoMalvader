package com.bancomalvader.model;

public class Cliente {
	private int idCliente;
	private int idUsuario;
	private double scoreCredito;
	
	public Cliente(int idCliente, int idUsuario, double scoreCredito) {
		
		this.idCliente = idCliente;
		this.idUsuario = idUsuario;
		this.scoreCredito = scoreCredito;
	}
	
	public Cliente(int idUsuario, double scoreCredito) {
		
		this.idUsuario = idUsuario;
		this.scoreCredito = scoreCredito;
	}

	public int getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(int idCliente) {
		this.idCliente = idCliente;
	}

	public int getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}

	public double getScoreCredito() {
		return scoreCredito;
	}

	public void setScoreCredito(double scoreCredito) {
		this.scoreCredito = scoreCredito;
	}

	@Override
	public String toString() {
		return "Cliente [idCliente=" + idCliente + ", idUsuario=" + idUsuario + ", scoreCredito=" + scoreCredito + "]";
	}
	
}

