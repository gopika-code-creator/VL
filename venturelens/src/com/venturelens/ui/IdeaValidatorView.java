package com.venturelens.ui;

import com.venturelens.analysis.NLPValidatorEngine;
import com.venturelens.dao.VentureDAO;
import com.venturelens.model.DecisionTier;
import com.venturelens.model.User;
import com.venturelens.model.UserSession;
import com.venturelens.model.VentureIdea;
import com.venturelens.utils.ThemeColors;
import com.venturelens.utils.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Idea Validator Screen.
 * Uses the Custom Rule-Based NLP Pipeline with lookbehind negation detection.
 * Calculates composite decision tiers, presents a 4-box SWOT matrix,
 * failure risks, and persists to MySQL via SwingWorker.
 */
public class IdeaValidatorView extends JPanel {

    private final MainFrame mainFrame;
    private final NLPValidatorEngine nlpEngine = new NLPValidatorEngine();
    private final VentureDAO ventureDAO = new VentureDAO();

    // Inputs
    private final JTextField nameField = UIHelper.createTextField(20);
    private final JTextField customerField = UIHelper.createTextField(20);
    private final JTextArea problemArea = UIHelper.createTextArea(3, 20);
    private final JTextArea solutionArea = UIHelper.createTextArea(3, 20);
    private final JTextField businessModelField = UIHelper.createTextField(20);

    // Outputs
    private final JLabel overallScoreLabel = new JLabel("-- / 100");
    private final JLabel tierBadgeLabel = new JLabel("PENDING");
    private final JLabel marketScoreLabel = new JLabel("Market: --");
    private final JLabel feasibilityScoreLabel = new JLabel("Feasibility: --");
    private final JLabel competitionScoreLabel = new JLabel("Competition: --");
    private final JLabel depthScoreLabel = new JLabel("Depth: --");

    private final DefaultListModel<String> strengthsModel = new DefaultListModel<>();
    private final DefaultListModel<String> weaknessesModel = new DefaultListModel<>();
    private final DefaultListModel<String> opportunitiesModel = new DefaultListModel<>();
    private final DefaultListModel<String> threatsModel = new DefaultListModel<>();
    private final DefaultListModel<String> risksModel = new DefaultListModel<>();

    private final JButton evaluateBtn = UIHelper.createPrimaryButton("Run NLP Evaluation");
    private final JButton saveBtn = UIHelper.createSecondaryButton("Save to Database");
    private final JLabel saveStatusLabel = new JLabel(" ");

    private VentureIdea currentEvaluation;

    public IdeaValidatorView(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(16, 16));
        setBackground(ThemeColors.BASE_BG);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        // Prepopulate demo fields
        nameField.setText("DataPulse AI");
        customerField.setText("B2B SaaS engineering leaders and enterprise CTOs");
        problemArea.setText("Engineering organizations struggle with silent data pipeline breakages causing massive enterprise revenue loss, SLA penalties, and customer churn.");
        solutionArea.setText("An automated, cloud-native real-time telemetry agent with proprietary anomaly detection algorithms and zero-config SDK integrations.");
        businessModelField.setText("B2B Enterprise SaaS Subscription (Tiered per cluster)");

        // Left Panel: Form Inputs
        JPanel leftPanel = UIHelper.createCardPanel(20);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setPreferredSize(new Dimension(460, 680));

        JLabel formTitle = UIHelper.createSubtitleLabel("Venture Definition & Inputs");
        leftPanel.add(formTitle);
        leftPanel.add(Box.createVerticalStrut(14));

        leftPanel.add(UIHelper.createBodyLabel("Startup Name"));
        leftPanel.add(Box.createVerticalStrut(4));
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        leftPanel.add(nameField);
        leftPanel.add(Box.createVerticalStrut(10));

        leftPanel.add(UIHelper.createBodyLabel("Target Customer (ICP)"));
        leftPanel.add(Box.createVerticalStrut(4));
        customerField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        leftPanel.add(customerField);
        leftPanel.add(Box.createVerticalStrut(10));

        leftPanel.add(UIHelper.createBodyLabel("Problem Statement"));
        leftPanel.add(Box.createVerticalStrut(4));
        JScrollPane probScroll = UIHelper.wrapInStyledScrollPane(problemArea);
        probScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        leftPanel.add(probScroll);
        leftPanel.add(Box.createVerticalStrut(10));

        leftPanel.add(UIHelper.createBodyLabel("Proposed Solution"));
        leftPanel.add(Box.createVerticalStrut(4));
        JScrollPane solScroll = UIHelper.wrapInStyledScrollPane(solutionArea);
        solScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        leftPanel.add(solScroll);
        leftPanel.add(Box.createVerticalStrut(10));

        leftPanel.add(UIHelper.createBodyLabel("Business Model"));
        leftPanel.add(Box.createVerticalStrut(4));
        businessModelField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        leftPanel.add(businessModelField);
        leftPanel.add(Box.createVerticalStrut(18));

        evaluateBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        evaluateBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(evaluateBtn);
        leftPanel.add(Box.createVerticalStrut(8));

        saveBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(saveBtn);
        leftPanel.add(Box.createVerticalStrut(6));

        saveStatusLabel.setFont(ThemeColors.FONT_SMALL);
        saveStatusLabel.setForeground(ThemeColors.ACCENT);
        leftPanel.add(saveStatusLabel);

        // Right Panel: Results, SWOT Matrix, Risks
        JPanel rightPanel = new JPanel(new BorderLayout(14, 14));
        rightPanel.setOpaque(false);

        // Top Score Header Card
        JPanel scoreCard = UIHelper.createCardPanel(16);
        scoreCard.setLayout(new BorderLayout(16, 16));

        JPanel scoreLeft = new JPanel(new GridLayout(2, 1, 4, 4));
        scoreLeft.setOpaque(false);
        overallScoreLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 32));
        overallScoreLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        tierBadgeLabel.setFont(ThemeColors.FONT_TITLE);
        tierBadgeLabel.setForeground(ThemeColors.ACCENT);
        scoreLeft.add(overallScoreLabel);
        scoreLeft.add(tierBadgeLabel);

        JPanel scoreMetrics = new JPanel(new GridLayout(2, 2, 8, 8));
        scoreMetrics.setOpaque(false);
        marketScoreLabel.setFont(ThemeColors.FONT_BODY);
        marketScoreLabel.setForeground(ThemeColors.TEXT_MUTED);
        feasibilityScoreLabel.setFont(ThemeColors.FONT_BODY);
        feasibilityScoreLabel.setForeground(ThemeColors.TEXT_MUTED);
        competitionScoreLabel.setFont(ThemeColors.FONT_BODY);
        competitionScoreLabel.setForeground(ThemeColors.TEXT_MUTED);
        depthScoreLabel.setFont(ThemeColors.FONT_BODY);
        depthScoreLabel.setForeground(ThemeColors.TEXT_MUTED);

        scoreMetrics.add(marketScoreLabel);
        scoreMetrics.add(feasibilityScoreLabel);
        scoreMetrics.add(competitionScoreLabel);
        scoreMetrics.add(depthScoreLabel);

        scoreCard.add(scoreLeft, BorderLayout.WEST);
        scoreCard.add(scoreMetrics, BorderLayout.CENTER);
        rightPanel.add(scoreCard, BorderLayout.NORTH);

        // Center 4-Box SWOT Matrix
        JPanel swotPanel = new JPanel(new GridLayout(2, 2, 12, 12));
        swotPanel.setOpaque(false);

        swotPanel.add(createSwotBox("Strengths", ThemeColors.ACCENT, strengthsModel));
        swotPanel.add(createSwotBox("Weaknesses", ThemeColors.DANGER, weaknessesModel));
        swotPanel.add(createSwotBox("Opportunities", ThemeColors.SECONDARY, opportunitiesModel));
        swotPanel.add(createSwotBox("Threats", ThemeColors.TIER_CAUTION, threatsModel));

        // Bottom Failure Risks Card
        JPanel risksCard = UIHelper.createCardPanel(14);
        risksCard.setLayout(new BorderLayout(6, 6));
        JLabel risksTitle = UIHelper.createSubtitleLabel("Critical Failure Risks & Negation Alerts");
        risksTitle.setForeground(ThemeColors.DANGER);
        JList<String> risksList = new JList<>(risksModel);
        risksList.setBackground(ThemeColors.INPUT_BG);
        risksList.setForeground(ThemeColors.TEXT_PRIMARY);
        risksList.setFont(ThemeColors.FONT_BODY);
        JScrollPane risksScroll = new JScrollPane(risksList);
        risksScroll.setPreferredSize(new Dimension(400, 90));
        risksScroll.setBorder(new LineBorder(ThemeColors.BORDER, 1, true));

        risksCard.add(risksTitle, BorderLayout.NORTH);
        risksCard.add(risksScroll, BorderLayout.CENTER);

        JPanel centerRight = new JPanel(new BorderLayout(12, 12));
        centerRight.setOpaque(false);
        centerRight.add(swotPanel, BorderLayout.CENTER);
        centerRight.add(risksCard, BorderLayout.SOUTH);

        rightPanel.add(centerRight, BorderLayout.CENTER);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);

        // Wire Action Handlers
        evaluateBtn.addActionListener(e -> runEvaluation());
        saveBtn.addActionListener(e -> saveCurrentVenture());

        // Run initial evaluation on load
        runEvaluation();
    }

    private JPanel createSwotBox(String title, Color color, DefaultListModel<String> model) {
        JPanel box = UIHelper.createCardPanel(12);
        box.setLayout(new BorderLayout(6, 6));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(ThemeColors.FONT_SECTION);
        titleLbl.setForeground(color);

        JList<String> list = new JList<>(model);
        list.setBackground(ThemeColors.INPUT_BG);
        list.setForeground(ThemeColors.TEXT_PRIMARY);
        list.setFont(ThemeColors.FONT_BODY);

        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(new LineBorder(ThemeColors.BORDER, 1, true));

        box.add(titleLbl, BorderLayout.NORTH);
        box.add(scroll, BorderLayout.CENTER);
        return box;
    }

    public void runEvaluation() {
        VentureIdea idea = new VentureIdea();
        idea.setStartupName(nameField.getText().trim());
        idea.setTargetCustomer(customerField.getText().trim());
        idea.setProblemStatement(problemArea.getText().trim());
        idea.setProposedSolution(solutionArea.getText().trim());
        idea.setBusinessModel(businessModelField.getText().trim());

        User current = UserSession.getInstance().getCurrentUser();
        idea.setUserId(current != null ? current.getId() : 1);

        currentEvaluation = nlpEngine.evaluate(idea);
        displayEvaluationResults(currentEvaluation);
        mainFrame.setCurrentVenture(currentEvaluation);
    }

    private void displayEvaluationResults(VentureIdea idea) {
        overallScoreLabel.setText(idea.getOverallScore() + " / 100");
        tierBadgeLabel.setText(idea.getDecisionTier().getLabel());
        tierBadgeLabel.setForeground(idea.getDecisionTier().getBadgeColor());

        marketScoreLabel.setText("Market: " + idea.getMarketScore());
        feasibilityScoreLabel.setText("Feasibility: " + idea.getFeasibilityScore());
        competitionScoreLabel.setText("Competition: " + idea.getCompetitionScore());
        depthScoreLabel.setText("Concept Depth: " + idea.getDepthScore());

        strengthsModel.clear();
        for (String s : idea.getStrengths()) strengthsModel.addElement(s);

        weaknessesModel.clear();
        for (String w : idea.getWeaknesses()) weaknessesModel.addElement(w);

        opportunitiesModel.clear();
        for (String o : idea.getOpportunities()) opportunitiesModel.addElement(o);

        threatsModel.clear();
        for (String t : idea.getThreats()) threatsModel.addElement(t);

        risksModel.clear();
        for (String r : idea.getRisks()) risksModel.addElement(r);

        saveStatusLabel.setText(" ");
    }

    private void saveCurrentVenture() {
        if (currentEvaluation == null) {
            runEvaluation();
        }

        saveBtn.setEnabled(false);
        saveStatusLabel.setText("Persisting to MySQL database...");

        new SwingWorker<Integer, Void>() {
            @Override
            protected Integer doInBackground() throws Exception {
                return ventureDAO.saveVenture(currentEvaluation);
            }

            @Override
            protected void done() {
                saveBtn.setEnabled(true);
                try {
                    int id = get();
                    saveStatusLabel.setForeground(ThemeColors.ACCENT);
                    saveStatusLabel.setText("Saved successfully to MySQL (Record #" + id + ")");
                } catch (Exception ex) {
                    saveStatusLabel.setForeground(ThemeColors.DANGER);
                    saveStatusLabel.setText("Save failed: " + ex.getMessage());
                }
            }
        }.execute();
    }
}
