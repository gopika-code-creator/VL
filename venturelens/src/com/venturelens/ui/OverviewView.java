package com.venturelens.ui;

import com.venturelens.model.DecisionTier;
import com.venturelens.model.User;
import com.venturelens.model.UserSession;
import com.venturelens.model.VentureIdea;
import com.venturelens.utils.ThemeColors;
import com.venturelens.utils.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Overview / Health Snapshot Screen.
 * Provides founders with immediate visibility into venture scores,
 * decision tiers, equity dilution, cash runway, and quick actions.
 */
public class OverviewView extends JPanel {

    private final MainFrame mainFrame;
    private final JLabel welcomeLabel = UIHelper.createTitleLabel("Welcome to VentureLens OS");
    private final JLabel ventureNameLabel = UIHelper.createSubtitleLabel("Active Venture: DataPulse AI");
    private final JLabel tierBadge = new JLabel("STRONG_GO");
    private final JLabel scoreLabel = new JLabel("84.5 / 100");
    private final JLabel runwayLabel = new JLabel("14.2 Months");
    private final JLabel valuationLabel = new JLabel("$7,500,000");

    public OverviewView(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(20, 20));
        setBackground(ThemeColors.BASE_BG);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        // Top Header
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JPanel titleBlock = new JPanel(new GridLayout(2, 1, 4, 4));
        titleBlock.setOpaque(false);
        titleBlock.add(welcomeLabel);
        titleBlock.add(ventureNameLabel);

        JButton evaluateQuickBtn = UIHelper.createPrimaryButton("+ Run Idea Validator");
        evaluateQuickBtn.addActionListener(e -> mainFrame.showScreen(MainFrame.SCREEN_VALIDATOR));

        topPanel.add(titleBlock, BorderLayout.WEST);
        topPanel.add(evaluateQuickBtn, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Center Dashboard Grid
        JPanel centerPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        centerPanel.setOpaque(false);

        // Card 1: Decision Intelligence
        JPanel card1 = UIHelper.createCardPanel();
        card1.setLayout(new BoxLayout(card1, BoxLayout.Y_AXIS));
        JLabel c1Title = UIHelper.createSubtitleLabel("Validation Decision Tier");
        tierBadge.setFont(ThemeColors.FONT_TITLE);
        tierBadge.setForeground(ThemeColors.TIER_STRONG_GO);
        JLabel c1Sub = UIHelper.createMutedLabel("Rule-Based NLP Composite Score");
        scoreLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
        scoreLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        JButton openValidatorBtn = UIHelper.createSecondaryButton("Open Validator Details");
        openValidatorBtn.addActionListener(e -> mainFrame.showScreen(MainFrame.SCREEN_VALIDATOR));

        card1.add(c1Title);
        card1.add(Box.createVerticalStrut(10));
        card1.add(tierBadge);
        card1.add(Box.createVerticalStrut(14));
        card1.add(c1Sub);
        card1.add(Box.createVerticalStrut(4));
        card1.add(scoreLabel);
        card1.add(Box.createVerticalGlue());
        card1.add(openValidatorBtn);

        // Card 2: BurnWatch Survival Runway
        JPanel card2 = UIHelper.createCardPanel();
        card2.setLayout(new BoxLayout(card2, BoxLayout.Y_AXIS));
        JLabel c2Title = UIHelper.createSubtitleLabel("Cash Runway Survival");
        runwayLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
        runwayLabel.setForeground(ThemeColors.ACCENT);
        JLabel c2Sub = UIHelper.createMutedLabel("Net Monthly Burn: $19,700 | Zero-Cash: Dec 2027");
        JButton openBurnBtn = UIHelper.createSecondaryButton("Manage Financial Ledger");
        openBurnBtn.addActionListener(e -> mainFrame.showScreen(MainFrame.SCREEN_BURNWATCH));

        card2.add(c2Title);
        card2.add(Box.createVerticalStrut(12));
        card2.add(runwayLabel);
        card2.add(Box.createVerticalStrut(8));
        card2.add(c2Sub);
        card2.add(Box.createVerticalGlue());
        card2.add(openBurnBtn);

        // Card 3: CapTable Valuation & Equity
        JPanel card3 = UIHelper.createCardPanel();
        card3.setLayout(new BoxLayout(card3, BoxLayout.Y_AXIS));
        JLabel c3Title = UIHelper.createSubtitleLabel("Financing & CapTable");
        valuationLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
        valuationLabel.setForeground(ThemeColors.SECONDARY);
        JLabel c3Sub = UIHelper.createMutedLabel("Target Seed Round: $1,500,000 | Post-Money Valuation");
        JButton openCapBtn = UIHelper.createSecondaryButton("Simulate Equity Dilution");
        openCapBtn.addActionListener(e -> mainFrame.showScreen(MainFrame.SCREEN_CAPTABLE));

        card3.add(c3Title);
        card3.add(Box.createVerticalStrut(12));
        card3.add(valuationLabel);
        card3.add(Box.createVerticalStrut(8));
        card3.add(c3Sub);
        card3.add(Box.createVerticalGlue());
        card3.add(openCapBtn);

        // Card 4: PitchCraft Export Deck
        JPanel card4 = UIHelper.createCardPanel();
        card4.setLayout(new BoxLayout(card4, BoxLayout.Y_AXIS));
        JLabel c4Title = UIHelper.createSubtitleLabel("PitchCraft Deck Blueprint");
        JLabel c4Desc = UIHelper.createBodyLabel("Ready to compile 10-slide executive outline.");
        JLabel c4Format = UIHelper.createMutedLabel("Supports Markdown (.md) and Printable Browser PDF (.html)");
        JButton openPitchBtn = UIHelper.createPrimaryButton("Review & Export Pitch Deck");
        openPitchBtn.addActionListener(e -> mainFrame.showScreen(MainFrame.SCREEN_PITCHCRAFT));

        card4.add(c4Title);
        card4.add(Box.createVerticalStrut(12));
        card4.add(c4Desc);
        card4.add(Box.createVerticalStrut(8));
        card4.add(c4Format);
        card4.add(Box.createVerticalGlue());
        card4.add(openPitchBtn);

        centerPanel.add(card1);
        centerPanel.add(card2);
        centerPanel.add(card3);
        centerPanel.add(card4);

        add(centerPanel, BorderLayout.CENTER);
    }

    public void refreshState() {
        User u = UserSession.getInstance().getCurrentUser();
        if (u != null) {
            welcomeLabel.setText("Welcome back, " + u.getUsername());
        }
        VentureIdea current = mainFrame.getCurrentVenture();
        if (current != null) {
            ventureNameLabel.setText("Active Venture: " + current.getStartupName());
            scoreLabel.setText(current.getOverallScore() + " / 100");
            tierBadge.setText(current.getDecisionTier().getLabel());
            tierBadge.setForeground(current.getDecisionTier().getBadgeColor());
        }
    }
}
