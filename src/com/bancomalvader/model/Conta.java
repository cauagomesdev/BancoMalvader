package com.bancomalvader.model;

import java.time.LocalDateTime; 

public class Conta {
	private int idConta;
	private String numeroConta;
	private int idAgencia;
	private double saldo;
	private String tipoConta;
	private int idCliente;
	private LocalDateTime dataAbertura;
	private String status;
	
	public Conta(int idConta, String numeroConta, int idAgencia, double saldo, String tipoConta, int idCliente,
			LocalDateTime dataAbertura, String status) {
		
		this.idConta = idConta;
		this.numeroConta = numeroConta;
		this.idAgencia = idAgencia;
		this.saldo = saldo;
		this.tipoConta = tipoConta;
		this.idCliente = idCliente;
		this.dataAbertura = dataAbertura;
		this.status = status;
	}
	public Conta(String numeroConta, int idAgencia, double saldo, String tipoConta, int idCliente,
			LocalDateTime dataAbertura, String status) {
		
		this.numeroConta = numeroConta;
		this.idAgencia = idAgencia;
		this.saldo = saldo;
		this.tipoConta = tipoConta;
		this.idCliente = idCliente;
		this.dataAbertura = dataAbertura;
		this.status = status;
	}
	public int getIdConta() {
		return idConta;
	}
	public void setIdConta(int idConta) {
		this.idConta = idConta;
	}
	public String getNumeroConta() {
		return numeroConta;
	}
	public void setNumeroConta(String numeroConta) {
		this.numeroConta = numeroConta;
	}
	public int getIdAgencia() {
		return idAgencia;
	}
	public void setIdAgencia(int idAgencia) {
		this.idAgencia = idAgencia;
	}
	public double getSaldo() {
		return saldo;
	}
	public void setSaldo(double saldo) {
		this.saldo = saldo;
	}
	public String getTipoConta() {
		return tipoConta;
	}
	public void setTipoConta(String tipoConta) {
		this.tipoConta = tipoConta;
	}
	public int getIdCliente() {
		return idCliente;
	}
	public void setIdCliente(int idCliente) {
		this.idCliente = idCliente;
	}
	public LocalDateTime getDataAbertura() {
		return dataAbertura;
	}
	public void setDataAbertura(LocalDateTime dataAbertura) {
		this.dataAbertura = dataAbertura;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "Conta [idConta=" + idConta + ", numeroConta=" + numeroConta + ", idAgencia=" + idAgencia + ", saldo="
				+ saldo + ", tipoConta=" + tipoConta + ", idCliente=" + idCliente + ", dataAbertura=" + dataAbertura
				+ ", status=" + status + "]";
	}
	
	
}
