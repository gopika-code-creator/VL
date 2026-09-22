package com.venturelens.ui;

import com.venturelens.model.BurnEntry;
import com.venturelens.model.FinancialLedger;
import com.venturelens.ui.components.RunwayLineChartPanel;
import com.venturelens.utils.ThemeColors;
import com.venturelens.utils.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * BurnWatch (Cash Flow & Survival Tracker) Screen.
 * Uses a JTable with a custom AbstractTableModel for logging expenses and revenues.
 * Computes Net Monthly Burn, Cash Survival Runway in months, Zero-Cash Date,
 * and renders a native Graphics2D RunwayLineChartPanel.
 */
public class BurnWatchView extends JPanel {

    private final MainFrame mainFrame;
    private final FinancialLedger ledger = new FinancialLedger();
    private final DecimalFormat currencyFmt = new DecimalFormat("$#,##0.00");

    // Table Model
    private final LedgerTableModel tableModel = new LedgerTableModel();
    private final JTable ledgerTable = new JTable(tableModel);

    // Summary Metric Labels
    private final JTextField cashBalanceField = UIHelper.createTextField(10);
    private final JLabel grossBurnLabel = new JLabel("$0.00");
    private final JLabel monthlyRevenueLabel = new JLabel("$0.00");
    private final JLabel netBurnLabel = new JLabel("$0.00");
    private final JLabel runwayLabel = new JLabel("0.0 Months");
    private final JLabel zeroCashDateLabel = new JLabel("--");

    // Chart
    private final RunwayLineChartPanel lineChart = new RunwayLineChartPanel();

    // Entry Form Inputs
    private final JTextField monthField = UIHelper.createTextField(8);
    private final JComboBox<String> categoryCombo = new JComboBox<>(new String[]{
            "Engineering Salaries", "Cloud Infrastructure", "Marketing & Growth",
            "SaaS Tools & Office", "Legal & Accounting", "B2B Pilot Contracts",
            "Enterprise Subscriptions", "Consulting & Services"
    });
    private final JComboBox<BurnEntry.EntryType> typeCombo = new JComboBox<>(BurnEntry.EntryType.values());
    private final JTextField amountField = UIHelper.createTextField(8);
    private final JTextField notesField = UIHelper.createTextField(12);

    public BurnWatchView(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(16, 16));
        setBackground(ThemeColors.BASE_BG);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        // Seed initial ledger data
        seedInitialLedger();

        // Top Metrics Bar
        JPanel topBar = UIHelper.createCardPanel(14);
        topBar.setLayout(new GridLayout(1, 5, 12, 12));

        JPanel cashPanel = new JPanel(new GridLayout(2, 1, 2, 2));
        cashPanel.setOpaque(false);
        cashPanel.add(UIHelper.createMutedLabel("Cash Balance ($)"));
        cashBalanceField.setText("250000.00");
        cashBalanceField.addActionListener(e -> updateCalculations());
        cashPanel.add(cashBalanceField);

        topBar.add(cashPanel);
        topBar.add(createStatBlock("Monthly Revenue", monthlyRevenueLabel, ThemeColors.ACCENT));
        topBar.add(createStatBlock("Gross Expenses", grossBurnLabel, ThemeColors.DANGER));
        topBar.add(createStatBlock("Net Monthly Burn", netBurnLabel, ThemeColors.TIER_CAUTION));
        topBar.add(createStatBlock("Runway / Zero-Cash", runwayLabel, ThemeColors.ACCENT));

        add(topBar, BorderLayout.NORTH);

        // Center Split: Left Ledger Table, Right Chart
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 16, 16));
        centerPanel.setOpaque(false);

        // Left Card: Ledger Table & Add Entry Form
        JPanel tableCard = UIHelper.createCardPanel(14);
        tableCard.setLayout(new BorderLayout(10, 10));

        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setOpaque(false);
        tableHeader.add(UIHelper.createSubtitleLabel("Monthly Operational Cash Ledger"), BorderLayout.WEST);
        JButton deleteBtn = UIHelper.createSecondaryButton("Delete Selected");
        deleteBtn.addActionListener(e -> deleteSelectedRow());
        tableHeader.add(deleteBtn, BorderLayout.EAST);
        tableCard.add(tableHeader, BorderLayout.NORTH);

        // Style JTable
        ledgerTable.setBackground(ThemeColors.INPUT_BG);
        ledgerTable.setForeground(ThemeColors.TEXT_PRIMARY);
        ledgerTable.setGridColor(ThemeColors.BORDER);
        ledgerTable.setRowHeight(28);
        ledgerTable.getTableHeader().setBackground(ThemeColors.CARD_BG_HOVER);
        ledgerTable.getTableHeader().setForeground(ThemeColors.TEXT_MUTED);
        ledgerTable.getTableHeader().setFont(ThemeColors.FONT_SECTION);

        // Align right for amounts
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        ledgerTable.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);

        JScrollPane scrollPane = new JScrollPane(ledgerTable);
        scrollPane.getViewport().setBackground(ThemeColors.INPUT_BG);
        scrollPane.setBorder(UIHelper.wrapInStyledScrollPane(ledgerTable).getBorder());
        tableCard.add(scrollPane, BorderLayout.CENTER);

        // Add Entry Form at Bottom of Table Card
        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        addPanel.setOpaque(false);
        monthField.setText("Month 1");
        amountField.setText("10000.00");
        notesField.setText("Note");

        addPanel.add(monthField);
        addPanel.add(categoryCombo);
        addPanel.add(typeCombo);
        addPanel.add(amountField);
        JButton addBtn = UIHelper.createPrimaryButton("+ Add");
        addBtn.addActionListener(e -> addNewEntry());
        addPanel.add(addBtn);

        tableCard.add(addPanel, BorderLayout.SOUTH);

        // Right Card: Line Chart
        JPanel chartCard = UIHelper.createCardPanel(14);
        chartCard.setLayout(new BorderLayout(10, 10));
        chartCard.add(UIHelper.createSubtitleLabel("Cash Balance Depletion & Runway Curve"), BorderLayout.NORTH);
        chartCard.add(lineChart, BorderLayout.CENTER);

        centerPanel.add(tableCard);
        centerPanel.add(chartCard);

        add(centerPanel, BorderLayout.CENTER);

        updateCalculations();
    }

    private JPanel createStatBlock(String title, JLabel val, Color col) {
        JPanel p = new JPanel(new GridLayout(2, 1, 2, 2));
        p.setOpaque(false);
        p.add(UIHelper.createMutedLabel(title));
        val.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 17));
        val.setForeground(col);
        p.add(val);
        return p;
    }

    private void seedInitialLedger() {
        ledger.addEntry(new BurnEntry(1, 1, "Month 1", "Engineering Salaries", BurnEntry.EntryType.EXPENSE, new BigDecimal("22000.00"), "Core team"));
        ledger.addEntry(new BurnEntry(2, 1, "Month 1", "Cloud Infrastructure", BurnEntry.EntryType.EXPENSE, new BigDecimal("4500.00"), "AWS/GCP cluster"));
        ledger.addEntry(new BurnEntry(3, 1, "Month 1", "SaaS Subscriptions", BurnEntry.EntryType.EXPENSE, new BigDecimal("1200.00"), "Dev tools"));
        ledger.addEntry(new BurnEntry(4, 1, "Month 1", "B2B Pilot Contracts", BurnEntry.EntryType.REVENUE, new BigDecimal("8000.00"), "Pilot milestone"));
    }

    private void addNewEntry() {
        try {
            String month = monthField.getText().trim();
            String cat = (String) categoryCombo.getSelectedItem();
            BurnEntry.EntryType type = (BurnEntry.EntryType) typeCombo.getSelectedItem();
            BigDecimal amount = new BigDecimal(amountField.getText().trim());
            String notes = notesField.getText().trim();

            BurnEntry entry = new BurnEntry(ledger.getEntries().size() + 1, 1, month, cat, type, amount, notes);
            ledger.addEntry(entry);
            tableModel.fireTableDataChanged();
            updateCalculations();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount: " + ex.getMessage());
        }
    }

    private void deleteSelectedRow() {
        int selected = ledgerTable.getSelectedRow();
        if (selected >= 0) {
            ledger.removeEntry(selected);
            tableModel.fireTableDataChanged();
            updateCalculations();
        }
    }

    public void updateCalculations() {
        try {
            BigDecimal starting = new BigDecimal(cashBalanceField.getText().trim());
            ledger.setStartingCashBalance(starting);
        } catch (Exception ignored) {
        }

        BigDecimal gross = ledger.getTotalMonthlyExpenses();
        BigDecimal rev = ledger.getTotalMonthlyRevenue();
        BigDecimal net = ledger.getNetMonthlyBurn();
        double runway = ledger.getRunwayMonths();
        String zeroDate = ledger.getZeroCashDateEstimate();

        grossBurnLabel.setText(currencyFmt.format(gross));
        monthlyRevenueLabel.setText(currencyFmt.format(rev));
        netBurnLabel.setText(currencyFmt.format(net));

        if (Double.isInfinite(runway)) {
            runwayLabel.setText("Positive");
        } else {
            runwayLabel.setText(String.format("%.1f Mo (%s)", runway, zeroDate));
        }

        lineChart.updateRunwayData(ledger.getStartingCashBalance(), net, 18);
        mainFrame.setCurrentLedger(ledger);
    }

    public FinancialLedger getLedger() {
        return ledger;
    }

    /**
     * Custom AbstractTableModel for Financial Ledger
     */
    private class LedgerTableModel extends AbstractTableModel {
        private final String[] columns = {"Month", "Category", "Type", "Amount ($)", "Notes"};

        @Override
        public int getRowCount() {
            return ledger.getEntries().size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int col) {
            return columns[col];
        }

        @Override
        public Object getValueAt(int row, int col) {
            BurnEntry e = ledger.getEntries().get(row);
            switch (col) {
                case 0: return e.getMonthLabel();
                case 1: return e.getCategory();
                case 2: return e.getEntryType().name();
                case 3: return currencyFmt.format(e.getAmount());
                case 4: return e.getNotes();
                default: return "";
            }
        }
    }
}
