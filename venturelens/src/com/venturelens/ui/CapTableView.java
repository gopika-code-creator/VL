package com.venturelens.ui;

import com.venturelens.model.CapTableState;
import com.venturelens.ui.components.DonutChartPanel;
import com.venturelens.utils.ThemeColors;
import com.venturelens.utils.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * CapTable & Equity Dilution Simulator Screen.
 * Employs java.math.BigDecimal for financial accuracy.
 * Integrates the native DonutChartPanel for real-time Graphics2D visualization.
 */
public class CapTableView extends JPanel {

    private final MainFrame mainFrame;
    private final CapTableState capTableState = new CapTableState();
    private final DecimalFormat currencyFmt = new DecimalFormat("$#,##0.00");
    private final DecimalFormat pctFmt = new DecimalFormat("#0.00'%'");

    // Inputs
    private final JTextField founder1Field = UIHelper.createTextField(8);
    private final JTextField founder2Field = UIHelper.createTextField(8);
    private final JTextField esopField = UIHelper.createTextField(8);
    private final JTextField preMoneyField = UIHelper.createTextField(14);
    private final JTextField investmentField = UIHelper.createTextField(14);

    // Outputs
    private final JLabel postMoneyLabel = new JLabel("$0.00");
    private final JLabel investorPctLabel = new JLabel("0.00%");
    private final JLabel founder1DilutedLabel = new JLabel("0.00%");
    private final JLabel founder2DilutedLabel = new JLabel("0.00%");
    private final JLabel esopDilutedLabel = new JLabel("0.00%");

    // Custom Graphics2D Donut Chart
    private final DonutChartPanel donutChart = new DonutChartPanel();

    public CapTableView(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(20, 20));
        setBackground(ThemeColors.BASE_BG);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        // Prepopulate baseline values
        founder1Field.setText("50.00");
        founder2Field.setText("40.00");
        esopField.setText("10.00");
        preMoneyField.setText("6000000.00");
        investmentField.setText("1500000.00");

        // Left Controls Panel
        JPanel leftPanel = UIHelper.createCardPanel(20);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setPreferredSize(new Dimension(420, 600));

        JLabel titleLbl = UIHelper.createSubtitleLabel("Equity Split & Investment Terms");
        leftPanel.add(titleLbl);
        leftPanel.add(Box.createVerticalStrut(14));

        leftPanel.add(UIHelper.createBodyLabel("Founder 1 Initial Share (%)"));
        leftPanel.add(Box.createVerticalStrut(4));
        founder1Field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        leftPanel.add(founder1Field);
        leftPanel.add(Box.createVerticalStrut(10));

        leftPanel.add(UIHelper.createBodyLabel("Founder 2 Initial Share (%)"));
        leftPanel.add(Box.createVerticalStrut(4));
        founder2Field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        leftPanel.add(founder2Field);
        leftPanel.add(Box.createVerticalStrut(10));

        leftPanel.add(UIHelper.createBodyLabel("ESOP Employee Option Pool (%)"));
        leftPanel.add(Box.createVerticalStrut(4));
        esopField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        leftPanel.add(esopField);
        leftPanel.add(Box.createVerticalStrut(14));

        JSeparator sep = new JSeparator();
        sep.setForeground(ThemeColors.BORDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        leftPanel.add(sep);
        leftPanel.add(Box.createVerticalStrut(14));

        leftPanel.add(UIHelper.createBodyLabel("Pre-Money Valuation ($)"));
        leftPanel.add(Box.createVerticalStrut(4));
        preMoneyField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        leftPanel.add(preMoneyField);
        leftPanel.add(Box.createVerticalStrut(10));

        leftPanel.add(UIHelper.createBodyLabel("Simulated Investment Round ($)"));
        leftPanel.add(Box.createVerticalStrut(4));
        investmentField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        leftPanel.add(investmentField);
        leftPanel.add(Box.createVerticalStrut(18));

        JButton recalculateBtn = UIHelper.createPrimaryButton("Calculate Post-Round Dilution");
        recalculateBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        recalculateBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        recalculateBtn.addActionListener(e -> recalculateCapTable());
        leftPanel.add(recalculateBtn);

        // Right Simulation Visuals & Cards
        JPanel rightPanel = new JPanel(new BorderLayout(16, 16));
        rightPanel.setOpaque(false);

        // Output Metric Cards in a Grid
        JPanel metricsGrid = new JPanel(new GridLayout(2, 3, 12, 12));
        metricsGrid.setOpaque(false);
        metricsGrid.setPreferredSize(new Dimension(500, 160));

        metricsGrid.add(createMetricCard("Post-Money Valuation", postMoneyLabel, ThemeColors.TEXT_PRIMARY));
        metricsGrid.add(createMetricCard("Investor Ownership", investorPctLabel, ThemeColors.ACCENT));
        metricsGrid.add(createMetricCard("Founder 1 Diluted", founder1DilutedLabel, ThemeColors.SECONDARY));
        metricsGrid.add(createMetricCard("Founder 2 Diluted", founder2DilutedLabel, ThemeColors.SECONDARY));
        metricsGrid.add(createMetricCard("ESOP Option Pool", esopDilutedLabel, new Color(0x8B, 0x5C, 0xF6)));

        JPanel emptyHolder = UIHelper.createCardPanel(12);
        emptyHolder.setLayout(new BorderLayout());
        JLabel syncLabel = UIHelper.createMutedLabel("Precision: java.math.BigDecimal");
        syncLabel.setHorizontalAlignment(SwingConstants.CENTER);
        emptyHolder.add(syncLabel, BorderLayout.CENTER);
        metricsGrid.add(emptyHolder);

        // Donut Chart Container
        JPanel chartCard = UIHelper.createCardPanel(16);
        chartCard.setLayout(new BorderLayout(10, 10));
        JLabel chartTitle = UIHelper.createSubtitleLabel("Simulated Post-Round Ownership Distribution");
        chartCard.add(chartTitle, BorderLayout.NORTH);
        chartCard.add(donutChart, BorderLayout.CENTER);

        rightPanel.add(metricsGrid, BorderLayout.NORTH);
        rightPanel.add(chartCard, BorderLayout.CENTER);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);

        // Perform initial calculation
        recalculateCapTable();
    }

    private JPanel createMetricCard(String title, JLabel valLabel, Color accentColor) {
        JPanel card = UIHelper.createCardPanel(12);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel titleLbl = UIHelper.createMutedLabel(title);
        valLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        valLabel.setForeground(accentColor);

        card.add(titleLbl);
        card.add(Box.createVerticalStrut(6));
        card.add(valLabel);
        return card;
    }

    public void recalculateCapTable() {
        try {
            BigDecimal f1 = new BigDecimal(founder1Field.getText().trim());
            BigDecimal f2 = new BigDecimal(founder2Field.getText().trim());
            BigDecimal esop = new BigDecimal(esopField.getText().trim());
            BigDecimal preMoney = new BigDecimal(preMoneyField.getText().trim());
            BigDecimal investment = new BigDecimal(investmentField.getText().trim());

            capTableState.setFounder1InitialPct(f1);
            capTableState.setFounder2InitialPct(f2);
            capTableState.setEsopInitialPct(esop);
            capTableState.setPreMoneyValuation(preMoney);
            capTableState.setInvestmentAmount(investment);

            BigDecimal postMoney = capTableState.getPostMoneyValuation();
            BigDecimal investorPct = capTableState.getInvestorOwnershipPct();
            BigDecimal f1Diluted = capTableState.getFounder1DilutedPct();
            BigDecimal f2Diluted = capTableState.getFounder2DilutedPct();
            BigDecimal esopDiluted = capTableState.getEsopDilutedPct();

            postMoneyLabel.setText(currencyFmt.format(postMoney));
            investorPctLabel.setText(pctFmt.format(investorPct));
            founder1DilutedLabel.setText(pctFmt.format(f1Diluted));
            founder2DilutedLabel.setText(pctFmt.format(f2Diluted));
            esopDilutedLabel.setText(pctFmt.format(esopDiluted));

            donutChart.updateCapTableData(f1Diluted, f2Diluted, esopDiluted, investorPct);
            donutChart.setCenterText("Post-Money", currencyFmt.format(postMoney));

            mainFrame.setCurrentCapTable(capTableState);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid numerical input in CapTable fields: " + ex.getMessage(),
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public CapTableState getCapTableState() {
        return capTableState;
    }
}
