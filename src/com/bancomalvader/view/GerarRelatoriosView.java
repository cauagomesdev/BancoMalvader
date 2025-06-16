package com.bancomalvader.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate; // Para seletores de data
import java.time.format.DateTimeFormatter; // Para formatar datas
import java.time.format.DateTimeParseException; // Para parse de data
import java.util.List;
import java.util.Map; // Para dados de relatório
import javax.swing.table.DefaultTableModel; // Para DefaultTableModel de JTable
import javax.swing.text.MaskFormatter; // Para MaskFormatter

// Esta View será um JDialog para seleção de relatórios e exibição
public class GerarRelatoriosView extends JDialog {

    private JComboBox<String> cmbTipoRelatorio;
    private JTextField txtDataInicio; // JFormattedTextField com máscara ou JDatePicker
    private JTextField txtDataFim;    // JFormattedTextField com máscara ou JDatePicker
    private JTextField txtTipoTransacao; // Para filtro de movimentações
    private JTextField txtCodigoAgencia; // Para filtro de movimentações
    private JButton btnGerar;
    private JButton btnExportarExcel;
    private JButton btnExportarPDF;
    private JTable tblResultados;
    private JScrollPane scrollPane;
    private DefaultTableModel tableModel;

    // ✅ CORREÇÃO AQUI: Declarar inputPanel como um membro da classe
    private JPanel inputPanel; 

    public GerarRelatoriosView(Frame owner) {
        super(owner, "Gerar Relatórios - Banco Malvader", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // ✅ CORREÇÃO AQUI: Inicializar inputPanel como membro da classe
        inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10)); 
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        cmbTipoRelatorio = new JComboBox<>(new String[]{"Movimentações por Período", "Clientes Inadimplentes", "Desempenho de Funcionários"});
        txtDataInicio = new JTextField(10); try { MaskFormatter dm = new MaskFormatter("##/##/####"); txtDataInicio = new JFormattedTextField(dm); } catch(Exception e) {}
        txtDataFim = new JTextField(10); try { MaskFormatter dm = new MaskFormatter("##/##/####"); txtDataFim = new JFormattedTextField(dm); } catch(Exception e) {}
        txtTipoTransacao = new JTextField(10); txtTipoTransacao.setText("TODOS"); // Padrão
        txtCodigoAgencia = new JTextField(10);
        btnGerar = new JButton("Gerar Relatório");
        btnExportarExcel = new JButton("Exportar Excel");
        btnExportarPDF = new JButton("Exportar PDF");

        inputPanel.add(new JLabel("Tipo de Relatório:"));
        inputPanel.add(cmbTipoRelatorio);
        inputPanel.add(new JLabel("De:"));
        inputPanel.add(txtDataInicio);
        inputPanel.add(new JLabel("Até:"));
        inputPanel.add(txtDataFim);
        inputPanel.add(new JLabel("Tipo Transação:"));
        inputPanel.add(txtTipoTransacao);
        inputPanel.add(new JLabel("Cód. Agência:"));
        inputPanel.add(txtCodigoAgencia);
        inputPanel.add(btnGerar);
        inputPanel.add(btnExportarExcel);
        inputPanel.add(btnExportarPDF);

        add(inputPanel, BorderLayout.NORTH);

        // Tabela para exibir os resultados do relatório
        tableModel = new DefaultTableModel();
        tblResultados = new JTable(tableModel);
        tblResultados.setFillsViewportHeight(true);
        scrollPane = new JScrollPane(tblResultados);
        add(scrollPane, BorderLayout.CENTER);

        // Esconder campos de data/filtros se o relatório não for de movimentações inicialmente
        cmbTipoRelatorio.addActionListener(e -> toggleFiltrosMovimentacoes());
        toggleFiltrosMovimentacoes(); // Configura a visibilidade inicial

        // Ações dos botões serão configuradas pelo Controller
    }

    private void toggleFiltrosMovimentacoes() {
        String selectedType = (String) cmbTipoRelatorio.getSelectedItem();
        boolean isMovimentacoes = "Movimentações por Período".equals(selectedType);

        txtDataInicio.setVisible(isMovimentacoes);
        txtDataFim.setVisible(isMovimentacoes);
        txtTipoTransacao.setVisible(isMovimentacoes);
        txtCodigoAgencia.setVisible(isMovimentacoes);
        
        // ✅ CORREÇÃO AQUI: Acessar os componentes diretamente do inputPanel
        Component[] components = inputPanel.getComponents(); // Usar o membro da classe inputPanel
        for (Component comp : components) {
            if (comp instanceof JLabel) {
                String text = ((JLabel) comp).getText();
                if (text.contains("De:") || text.contains("Até:") || text.contains("Tipo Transação:") || text.contains("Cód. Agência:")) {
                    comp.setVisible(isMovimentacoes);
                }
            }
        }
        revalidate();
        repaint();
    }
    
    // Getters para os componentes
    public JComboBox<String> getCmbTipoRelatorio() { return cmbTipoRelatorio; }
    public JTextField getTxtDataInicio() { return txtDataInicio; }
    public JTextField getTxtDataFim() { return txtDataFim; }
    public JTextField getTxtTipoTransacao() { return txtTipoTransacao; }
    public JTextField getTxtCodigoAgencia() { return txtCodigoAgencia; }
    public JButton getBtnGerar() { return btnGerar; }
    public JButton getBtnExportarExcel() { return btnExportarExcel; }
    public JButton getBtnExportarPDF() { return btnExportarPDF; }
    public DefaultTableModel getTableModel() { return tableModel; }
}
