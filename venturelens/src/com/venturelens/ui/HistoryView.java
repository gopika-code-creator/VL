package com.venturelens.ui;

import com.venturelens.dao.VentureDAO;
import com.venturelens.model.DecisionTier;
import com.venturelens.model.User;
import com.venturelens.model.UserSession;
import com.venturelens.model.VentureIdea;
import com.venturelens.ui.components.DecisionBadgeRenderer;
import com.venturelens.utils.ThemeColors;
import com.venturelens.utils.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Saved Plans & History Screen.
 * Displays previous venture blueprints loaded asynchronously from MySQL using SwingWorker.
 * Features custom DecisionBadgeRenderer for status pills and a dynamic inspection card.
 */
public class HistoryView extends JPanel {

    private final MainFrame mainFrame;
    private final VentureDAO ventureDAO = new VentureDAO();

    private final List<VentureIdea> venturesList = new ArrayList<>();
    private final HistoryTableModel tableModel = new HistoryTableModel();
    private final JTable historyTable = new JTable(tableModel);

    // Bottom Inspection Card Components
    private final JLabel inspectTitle = UIHelper.createTitleLabel("Select a venture to inspect");
    private final JLabel inspectTier = new JLabel(" ");
    private final JLabel inspectScores = new JLabel(" ");
    private final JTextArea inspectProblemArea = UIHelper.createTextArea(3, 40);
    private final JTextArea inspectSolutionArea = UIHelper.createTextArea(3, 40);
    private final JButton loadIntoWorkspaceBtn = UIHelper.createPrimaryButton("Load into Active Workspace");
    private final JLabel statusLabel = new JLabel(" ");

    private VentureIdea selectedVenture;

    public HistoryView(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(16, 16));
        setBackground(ThemeColors.BASE_BG);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        // Top Action Bar
        JPanel topBar = UIHelper.createCardPanel(14);
        topBar.setLayout(new BorderLayout(10, 10));

        JPanel titleBlock = new JPanel(new GridLayout(2, 1, 2, 2));
        titleBlock.setOpaque(false);
        titleBlock.add(UIHelper.createTitleLabel("Saved Plans & Evaluated Ventures"));
        titleBlock.add(UIHelper.createMutedLabel("Direct MySQL persistence via official JDBC. Click any row to inspect decision breakdown."));

        JButton refreshBtn = UIHelper.createSecondaryButton("Refresh from MySQL");
        refreshBtn.addActionListener(e -> reloadVenturesFromDatabase());

        topBar.add(titleBlock, BorderLayout.WEST);
        topBar.add(refreshBtn, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // Center Table Card
        JPanel tableCard = UIHelper.createCardPanel(14);
        tableCard.setLayout(new BorderLayout(10, 10));

        historyTable.setBackground(ThemeColors.INPUT_BG);
        historyTable.setForeground(ThemeColors.TEXT_PRIMARY);
        historyTable.setGridColor(ThemeColors.BORDER);
        historyTable.setRowHeight(32);
        historyTable.getTableHeader().setBackground(ThemeColors.CARD_BG_HOVER);
        historyTable.getTableHeader().setForeground(ThemeColors.TEXT_MUTED);
        historyTable.getTableHeader().setFont(ThemeColors.FONT_SECTION);
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Assign Custom Rounded Pill Badge Renderer to Decision Tier Column
        historyTable.getColumnModel().getColumn(2).setCellRenderer(new DecisionBadgeRenderer());

        // Row Selection Listener
        historyTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = historyTable.getSelectedRow();
                if (row >= 0 && row < venturesList.size()) {
                    updateInspectionCard(venturesList.get(row));
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.getViewport().setBackground(ThemeColors.INPUT_BG);
        scrollPane.setBorder(UIHelper.wrapInStyledScrollPane(historyTable).getBorder());
        tableCard.add(scrollPane, BorderLayout.CENTER);

        // Bottom Inspection Card
        JPanel inspectCard = UIHelper.createCardPanel(16);
        inspectCard.setLayout(new BorderLayout(12, 12));
        inspectCard.setPreferredSize(new Dimension(800, 240));

        JPanel inspectHeader = new JPanel(new BorderLayout());
        inspectHeader.setOpaque(false);

        JPanel inspectTitleBlock = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        inspectTitleBlock.setOpaque(false);
        inspectTitleBlock.add(inspectTitle);
        inspectTitleBlock.add(inspectTier);
        inspectHeader.add(inspectTitleBlock, BorderLayout.WEST);
        inspectHeader.add(loadIntoWorkspaceBtn, BorderLayout.EAST);

        loadIntoWorkspaceBtn.setEnabled(false);
        loadIntoWorkspaceBtn.addActionListener(e -> {
            if (selectedVenture != null) {
                mainFrame.setCurrentVenture(selectedVenture);
                mainFrame.showScreen(MainFrame.SCREEN_OVERVIEW);
            }
        });

        JPanel inspectContent = new JPanel(new GridLayout(1, 2, 16, 16));
        inspectContent.setOpaque(false);

        JPanel leftInspect = new JPanel(new BorderLayout(6, 6));
        leftInspect.setOpaque(false);
        leftInspect.add(UIHelper.createSubtitleLabel("Problem Statement"), BorderLayout.NORTH);
        inspectProblemArea.setEditable(false);
        leftInspect.add(UIHelper.wrapInStyledScrollPane(inspectProblemArea), BorderLayout.CENTER);

        JPanel rightInspect = new JPanel(new BorderLayout(6, 6));
        rightInspect.setOpaque(false);
        rightInspect.add(UIHelper.createSubtitleLabel("Proposed Solution"), BorderLayout.NORTH);
        inspectSolutionArea.setEditable(false);
        rightInspect.add(UIHelper.wrapInStyledScrollPane(inspectSolutionArea), BorderLayout.CENTER);

        inspectContent.add(leftInspect);
        inspectContent.add(rightInspect);

        inspectCard.add(inspectHeader, BorderLayout.NORTH);
        inspectCard.add(inspectContent, BorderLayout.CENTER);
        inspectCard.add(inspectScores, BorderLayout.SOUTH);

        // Center container combining table and inspection card
        JPanel mainContent = new JPanel(new BorderLayout(14, 14));
        mainContent.setOpaque(false);
        mainContent.add(tableCard, BorderLayout.CENTER);
        mainContent.add(inspectCard, BorderLayout.SOUTH);

        add(mainContent, BorderLayout.CENTER);

        reloadVenturesFromDatabase();
    }

    public void reloadVenturesFromDatabase() {
        User user = UserSession.getInstance().getCurrentUser();
        final int userId = user != null ? user.getId() : 1;

        new SwingWorker<List<VentureIdea>, Void>() {
            @Override
            protected List<VentureIdea> doInBackground() throws Exception {
                return ventureDAO.getVenturesByUser(userId);
            }

            @Override
            protected void done() {
                try {
                    List<VentureIdea> list = get();
                    venturesList.clear();
                    venturesList.addAll(list);
                    tableModel.fireTableDataChanged();
                    if (!venturesList.isEmpty()) {
                        historyTable.setRowSelectionInterval(0, 0);
                        updateInspectionCard(venturesList.get(0));
                    }
                } catch (Exception ex) {
                    statusLabel.setText("Failed to load history: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void updateInspectionCard(VentureIdea idea) {
        this.selectedVenture = idea;
        inspectTitle.setText(idea.getStartupName());
        inspectTier.setText(" [" + idea.getDecisionTier().getLabel() + "] ");
        inspectTier.setFont(ThemeColors.FONT_TITLE);
        inspectTier.setForeground(idea.getDecisionTier().getBadgeColor());

        inspectProblemArea.setText(idea.getProblemStatement());
        inspectSolutionArea.setText(idea.getProposedSolution());

        inspectScores.setText("Composite Score: " + idea.getOverallScore() + "/100 | Market: "
                + idea.getMarketScore() + " | Feasibility: " + idea.getFeasibilityScore()
                + " | Competition: " + idea.getCompetitionScore() + " | Depth: " + idea.getDepthScore());
        inspectScores.setFont(ThemeColors.FONT_BODY);
        inspectScores.setForeground(ThemeColors.TEXT_MUTED);

        loadIntoWorkspaceBtn.setEnabled(true);
    }

    private class HistoryTableModel extends AbstractTableModel {
        private final String[] cols = {"ID", "Startup Name", "Decision Tier", "Overall Score", "Business Model", "Date Created"};

        @Override
        public int getRowCount() {
            return venturesList.size();
        }

        @Override
        public int getColumnCount() {
            return cols.length;
        }

        @Override
        public String getColumnName(int c) {
            return cols[c];
        }

        @Override
        public Object getValueAt(int row, int col) {
            VentureIdea v = venturesList.get(row);
            switch (col) {
                case 0: return v.getId();
                case 1: return v.getStartupName();
                case 2: return v.getDecisionTier();
                case 3: return v.getOverallScore() + " / 100";
                case 4: return v.getBusinessModel();
                case 5: return v.getCreatedAt() != null ? v.getCreatedAt().toString() : "Recent";
                default: return "";
            }
        }
    }
}
