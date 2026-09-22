package com.venturelens.ui;

import com.venturelens.model.CapTableState;
import com.venturelens.model.FinancialLedger;
import com.venturelens.model.VentureIdea;
import com.venturelens.utils.ThemeColors;
import com.venturelens.utils.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

/**
 * PitchCraft Screen.
 * Previews the 10-slide executive outline and exports to Markdown (.md)
 * or Printable HTML/CSS (.html) via pure standard java.io / java.nio.file.
 */
public class PitchCraftView extends JPanel {

    private final MainFrame mainFrame;
    private final JTextArea previewArea = UIHelper.createTextArea(20, 50);
    private final JLabel statusLabel = new JLabel(" ");

    public PitchCraftView(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(16, 16));
        setBackground(ThemeColors.BASE_BG);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        // Top Action Bar
        JPanel topBar = UIHelper.createCardPanel(14);
        topBar.setLayout(new BorderLayout(10, 10));

        JPanel titleBlock = new JPanel(new GridLayout(2, 1, 2, 2));
        titleBlock.setOpaque(false);
        titleBlock.add(UIHelper.createTitleLabel("PitchCraft — Pitch Deck & Blueprint Exporter"));
        titleBlock.add(UIHelper.createMutedLabel("Consolidates Idea Validation, SWOT, CapTable splits, and BurnWatch runway into 10 structured slides."));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);

        JButton exportMdBtn = UIHelper.createPrimaryButton("Export Markdown (.md)");
        exportMdBtn.addActionListener(e -> exportMarkdown());

        JButton exportHtmlBtn = UIHelper.createSecondaryButton("Export Printable HTML (PDF Ready)");
        exportHtmlBtn.addActionListener(e -> exportHtml());

        JButton refreshBtn = UIHelper.createSecondaryButton("Refresh Preview");
        refreshBtn.addActionListener(e -> refreshDeckContent());

        buttons.add(refreshBtn);
        buttons.add(exportMdBtn);
        buttons.add(exportHtmlBtn);

        topBar.add(titleBlock, BorderLayout.WEST);
        topBar.add(buttons, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        // Center: Preview Scroll Card
        JPanel previewCard = UIHelper.createCardPanel(16);
        previewCard.setLayout(new BorderLayout(8, 8));
        previewCard.add(UIHelper.createSubtitleLabel("10-Slide Startup Outline Preview"), BorderLayout.NORTH);

        previewArea.setEditable(false);
        previewArea.setFont(ThemeColors.FONT_MONO);
        previewArea.setBackground(ThemeColors.INPUT_BG);

        JScrollPane scrollPane = UIHelper.wrapInStyledScrollPane(previewArea);
        previewCard.add(scrollPane, BorderLayout.CENTER);

        statusLabel.setFont(ThemeColors.FONT_SMALL);
        statusLabel.setForeground(ThemeColors.ACCENT);
        previewCard.add(statusLabel, BorderLayout.SOUTH);

        add(previewCard, BorderLayout.CENTER);

        refreshDeckContent();
    }

    public void refreshDeckContent() {
        VentureIdea idea = mainFrame.getCurrentVenture();
        CapTableState cap = mainFrame.getCurrentCapTable();
        FinancialLedger ledger = mainFrame.getCurrentLedger();

        if (idea == null) {
            previewArea.setText("No active venture selected. Run Idea Validator first.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("========================================================================\n");
        sb.append("  VENTURELENS EXECUTIVE PITCH DECK OUTLINE\n");
        sb.append("  Venture: ").append(idea.getStartupName()).append("\n");
        sb.append("  Decision Tier: ").append(idea.getDecisionTier().getLabel()).append(" (Score: ").append(idea.getOverallScore()).append("/100)\n");
        sb.append("========================================================================\n\n");

        sb.append("[Slide 1] TITLE & HOOK\n");
        sb.append("  Startup: ").append(idea.getStartupName()).append("\n");
        sb.append("  Target ICP: ").append(idea.getTargetCustomer()).append("\n\n");

        sb.append("[Slide 2] THE PROBLEM\n");
        sb.append("  ").append(idea.getProblemStatement()).append("\n\n");

        sb.append("[Slide 3] THE SOLUTION\n");
        sb.append("  ").append(idea.getProposedSolution()).append("\n\n");

        sb.append("[Slide 4] TARGET MARKET & BUSINESS MODEL\n");
        sb.append("  Business Model: ").append(idea.getBusinessModel()).append("\n");
        sb.append("  Market Score: ").append(idea.getMarketScore()).append("/100\n\n");

        sb.append("[Slide 5] STRATEGIC SWOT MATRIX\n");
        sb.append("  Strengths: ").append(String.join(", ", idea.getStrengths())).append("\n");
        sb.append("  Weaknesses: ").append(String.join(", ", idea.getWeaknesses())).append("\n");
        sb.append("  Opportunities: ").append(String.join(", ", idea.getOpportunities())).append("\n");
        sb.append("  Threats: ").append(String.join(", ", idea.getThreats())).append("\n\n");

        sb.append("[Slide 6] RISK ANALYSIS & MITIGATION\n");
        for (String r : idea.getRisks()) sb.append("  * ").append(r).append("\n");
        sb.append("\n");

        sb.append("[Slide 7] CAPTABLE & EQUITY STRUCTURE\n");
        if (cap != null) {
            sb.append("  Pre-Money Valuation: $").append(cap.getPreMoneyValuation()).append("\n");
            sb.append("  Seed Investment Amount: $").append(cap.getInvestmentAmount()).append("\n");
            sb.append("  Post-Money Valuation: $").append(cap.getPostMoneyValuation()).append("\n");
            sb.append("  Founder 1: ").append(cap.getFounder1DilutedPct()).append("% | Founder 2: ")
              .append(cap.getFounder2DilutedPct()).append("% | ESOP: ").append(cap.getEsopDilutedPct())
              .append("% | Investors: ").append(cap.getInvestorOwnershipPct()).append("%\n\n");
        }

        sb.append("[Slide 8] FINANCIAL LEDGER & RUNWAY\n");
        if (ledger != null) {
            sb.append("  Starting Cash Balance: $").append(ledger.getStartingCashBalance()).append("\n");
            sb.append("  Net Monthly Burn: $").append(ledger.getNetMonthlyBurn()).append("\n");
            sb.append("  Survival Runway: ").append(String.format("%.1f", ledger.getRunwayMonths())).append(" Months\n");
            sb.append("  Zero-Cash Date: ").append(ledger.getZeroCashDateEstimate()).append("\n\n");
        }

        sb.append("[Slide 9] ROADMAP & MILESTONES\n");
        sb.append("  Q1: Pilot design partner validation.\n");
        sb.append("  Q2: Enterprise outbound acceleration & SOC2 compliance.\n");
        sb.append("  Q3-Q4: Scale to $50k MRR.\n\n");

        sb.append("[Slide 10] THE INVESTMENT ASK\n");
        if (cap != null) {
            sb.append("  Raising $").append(cap.getInvestmentAmount()).append(" in Seed Preferred equity.\n");
        }

        previewArea.setText(sb.toString());
        previewArea.setCaretPosition(0);
    }

    private void exportMarkdown() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("pitch-deck-outline.md"));
        int ret = chooser.showSaveDialog(this);
        if (ret == JFileChooser.APPROVE_OPTION) {
            try {
                PitchDeckExporter.exportToMarkdown(
                        chooser.getSelectedFile(),
                        mainFrame.getCurrentVenture(),
                        mainFrame.getCurrentCapTable(),
                        mainFrame.getCurrentLedger()
                );
                statusLabel.setForeground(ThemeColors.ACCENT);
                statusLabel.setText("Successfully exported Markdown outline to " + chooser.getSelectedFile().getName());
            } catch (Exception ex) {
                statusLabel.setForeground(ThemeColors.DANGER);
                statusLabel.setText("Export failed: " + ex.getMessage());
            }
        }
    }

    private void exportHtml() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("pitch-deck-printable.html"));
        int ret = chooser.showSaveDialog(this);
        if (ret == JFileChooser.APPROVE_OPTION) {
            try {
                PitchDeckExporter.exportToPrintableHtml(
                        chooser.getSelectedFile(),
                        mainFrame.getCurrentVenture(),
                        mainFrame.getCurrentCapTable(),
                        mainFrame.getCurrentLedger()
                );
                statusLabel.setForeground(ThemeColors.ACCENT);
                statusLabel.setText("Successfully exported printable HTML deck to " + chooser.getSelectedFile().getName() + " (Open in browser and press Ctrl+P to save as PDF)");
            } catch (Exception ex) {
                statusLabel.setForeground(ThemeColors.DANGER);
                statusLabel.setText("Export failed: " + ex.getMessage());
            }
        }
    }
}
