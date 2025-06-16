package com.bancomalvader.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel; // Para manipular dados da JTable
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map; // Para dados de relatório/consulta

// Esta View será um JDialog para coletar tipo de consulta e exibir resultados em JTable
public class ConsultarDados extends JDialog {

    private JComboBox<String> cmbTipoConsulta;
    private JTextField txtCriterioBusca; // Campo para CPF, Número da Conta, Código Funcionário
    private JButton btnBuscar;
    private JTextArea txtResultados; // Para exibir resultados simples
    private JTable tblResultados; // Para exibir resultados tabulares
    private DefaultTableModel tableModel; // Modelo da tabela

    public ConsultarDados(Frame owner) {
        super(owner, "Consultar Dados - Banco Malvader", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        cmbTipoConsulta = new JComboBox<>(new String[]{"Conta", "Funcionário", "Cliente"});
        txtCriterioBusca = new JTextField(20);
        btnBuscar = new JButton("Buscar");

        inputPanel.add(new JLabel("Consultar por:"));
        inputPanel.add(cmbTipoConsulta);
        inputPanel.add(new JLabel("Critério (CPF/Número/Código):"));
        inputPanel.add(txtCriterioBusca);
        inputPanel.add(btnBuscar);
        
        add(inputPanel, BorderLayout.NORTH);

        // Área de resultados (iniciando com um JTextArea simples, pode ser substituído por JTable)
        // txtResultados = new JTextArea("Resultados da consulta aparecerão aqui...");
        // txtResultados.setEditable(false);
        // add(new JScrollPane(txtResultados), BorderLayout.CENTER);

        // Usando JTable para resultados tabulares
        tableModel = new DefaultTableModel();
        tblResultados = new JTable(tableModel);
        tblResultados.setFillsViewportHeight(true); // Preenche a altura disponível
        add(new JScrollPane(tblResultados), BorderLayout.CENTER);

        // Ação do botão buscar será definida pelo Controller
        // btnBuscar.addActionListener(e -> { /* Lógica de busca */ });
    }

    // Getters para os componentes que o Controller precisa acessar
    public JComboBox<String> getCmbTipoConsulta() { return cmbTipoConsulta; }
    public JTextField getTxtCriterioBusca() { return txtCriterioBusca; }
    public JButton getBtnBuscar() { return btnBuscar; }
    // public JTextArea getTxtResultados() { return txtResultados; } // Se usar JTextArea
    public DefaultTableModel getTableModel() { return tableModel; } // Para atualizar a JTable
}
