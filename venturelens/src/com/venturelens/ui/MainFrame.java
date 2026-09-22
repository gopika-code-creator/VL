package com.venturelens.ui;

import com.venturelens.dao.VentureDAO;
import com.venturelens.model.CapTableState;
import com.venturelens.model.FinancialLedger;
import com.venturelens.model.User;
import com.venturelens.model.UserSession;
import com.venturelens.model.VentureIdea;
import com.venturelens.utils.ThemeColors;
import com.venturelens.utils.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main application window for VentureLens.
 * Encapsulates the entire desktop operating system within a single JFrame
 * and switches all workspace modules smoothly using java.awt.CardLayout.
 */
public class MainFrame extends JFrame {

    public static final String SCREEN_LOGIN = "SCREEN_LOGIN";
    public static final String SCREEN_REGISTER = "SCREEN_REGISTER";
    public static final String SCREEN_OVERVIEW = "SCREEN_OVERVIEW";
    public static final String SCREEN_VALIDATOR = "SCREEN_VALIDATOR";
    public static final String SCREEN_CAPTABLE = "SCREEN_CAPTABLE";
    public static final String SCREEN_BURNWATCH = "SCREEN_BURNWATCH";
    public static final String SCREEN_PITCHCRAFT = "SCREEN_PITCHCRAFT";
    public static final String SCREEN_HISTORY = "SCREEN_HISTORY";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardsContainer = new JPanel(cardLayout);

    // Sidebar
    private final JPanel sidebarPanel = new JPanel();
    private final JLabel userPillLabel = new JLabel("Guest");
    private final Map<String, JButton> navButtons = new HashMap<>();

    // Child Module Views
    private LoginView loginView;
    private RegisterView registerView;
    private OverviewView overviewView;
    private IdeaValidatorView validatorView;
    private CapTableView capTableView;
    private BurnWatchView burnWatchView;
    private PitchCraftView pitchCraftView;
    private HistoryView historyView;

    // Cross-Module Shared State
    private VentureIdea currentVenture;
    private CapTableState currentCapTable = new CapTableState();
    private FinancialLedger currentLedger = new FinancialLedger();

    public MainFrame() {
        super("VentureLens — Startup Operating System & Decision Intelligence Suite");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 840);
        setMinimumSize(new Dimension(1024, 700));
        setLocationRelativeTo(null);

        // Content pane setup
        Container rootPane = getContentPane();
        rootPane.setLayout(new BorderLayout());
        rootPane.setBackground(ThemeColors.BASE_BG);

        // Build Navigation Sidebar
        buildSidebar();

        // Build View Panels
        buildViews();

        rootPane.add(sidebarPanel, BorderLayout.WEST);
        rootPane.add(cardsContainer, BorderLayout.CENTER);

        // Start at Login screen
        showScreen(SCREEN_LOGIN);
    }

    private void buildSidebar() {
        sidebarPanel.setLayout(new BorderLayout());
        sidebarPanel.setBackground(ThemeColors.CARD_BG);
        sidebarPanel.setPreferredSize(new Dimension(240, 800));
        sidebarPanel.setBorder(new LineBorder(ThemeColors.BORDER, 1, false));

        // Top Brand Header
        JPanel brandPanel = new JPanel(new GridLayout(2, 1, 2, 2));
        brandPanel.setOpaque(false);
        brandPanel.setBorder(new EmptyBorder(24, 20, 24, 20));

        JLabel brandTitle = new JLabel("VentureLens");
        brandTitle.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        brandTitle.setForeground(ThemeColors.ACCENT);

        JLabel brandSub = UIHelper.createMutedLabel("STARTUP OPERATING SYSTEM");
        brandPanel.add(brandTitle);
        brandPanel.add(brandSub);
        sidebarPanel.add(brandPanel, BorderLayout.NORTH);

        // Nav Button List in Center
        JPanel navList = new JPanel();
        navList.setLayout(new BoxLayout(navList, BoxLayout.Y_AXIS));
        navList.setOpaque(false);
        navList.setBorder(new EmptyBorder(0, 12, 12, 12));

        addNavButton(navList, "Overview / Snapshot", SCREEN_OVERVIEW);
        addNavButton(navList, "Idea Validator", SCREEN_VALIDATOR);
        addNavButton(navList, "CapTable Simulator", SCREEN_CAPTABLE);
        addNavButton(navList, "BurnWatch (Cash Flow)", SCREEN_BURNWATCH);
        addNavButton(navList, "PitchCraft (Deck Exporter)", SCREEN_PITCHCRAFT);
        addNavButton(navList, "Saved Plans & History", SCREEN_HISTORY);

        sidebarPanel.add(navList, BorderLayout.CENTER);

        // Bottom User Session Pill & Logout
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(12, 16, 20, 16));

        JPanel userCard = new JPanel(new BorderLayout(8, 8));
        userCard.setBackground(ThemeColors.INPUT_BG);
        userCard.setBorder(new EmptyBorder(10, 12, 10, 12));

        JLabel avatar = new JLabel("\u25CF");
        avatar.setForeground(ThemeColors.ACCENT);
        avatar.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));

        userPillLabel.setFont(ThemeColors.FONT_BOLD);
        userPillLabel.setForeground(ThemeColors.TEXT_PRIMARY);

        userCard.add(avatar, BorderLayout.WEST);
        userCard.add(userPillLabel, BorderLayout.CENTER);

        JButton logoutBtn = UIHelper.createSecondaryButton("Logout");
        logoutBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        logoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutBtn.addActionListener(e -> performLogout());

        bottomPanel.add(userCard);
        bottomPanel.add(Box.createVerticalStrut(10));
        bottomPanel.add(logoutBtn);

        sidebarPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    private void addNavButton(JPanel container, String title, String screenKey) {
        JButton btn = new JButton(title);
        btn.setFont(ThemeColors.FONT_BOLD);
        btn.setForeground(ThemeColors.TEXT_MUTED);
        btn.setBackground(ThemeColors.CARD_BG);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(12, 16, 12, 16));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.getForeground() != ThemeColors.ACCENT) {
                    btn.setBackground(ThemeColors.CARD_BG_HOVER);
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.getForeground() != ThemeColors.ACCENT) {
                    btn.setBackground(ThemeColors.CARD_BG);
                }
            }
        });

        btn.addActionListener(e -> showScreen(screenKey));

        navButtons.put(screenKey, btn);
        container.add(btn);
        container.add(Box.createVerticalStrut(4));
    }

    private void buildViews() {
        cardsContainer.setBackground(ThemeColors.BASE_BG);

        loginView = new LoginView(this);
        registerView = new RegisterView(this);
        overviewView = new OverviewView(this);
        validatorView = new IdeaValidatorView(this);
        capTableView = new CapTableView(this);
        burnWatchView = new BurnWatchView(this);
        pitchCraftView = new PitchCraftView(this);
        historyView = new HistoryView(this);

        cardsContainer.add(loginView, SCREEN_LOGIN);
        cardsContainer.add(registerView, SCREEN_REGISTER);
        cardsContainer.add(overviewView, SCREEN_OVERVIEW);
        cardsContainer.add(validatorView, SCREEN_VALIDATOR);
        cardsContainer.add(capTableView, SCREEN_CAPTABLE);
        cardsContainer.add(burnWatchView, SCREEN_BURNWATCH);
        cardsContainer.add(pitchCraftView, SCREEN_PITCHCRAFT);
        cardsContainer.add(historyView, SCREEN_HISTORY);
    }

    public void showScreen(String screenKey) {
        boolean isAuthScreen = SCREEN_LOGIN.equals(screenKey) || SCREEN_REGISTER.equals(screenKey);
        sidebarPanel.setVisible(!isAuthScreen);

        // Highlight active nav button
        for (Map.Entry<String, JButton> entry : navButtons.entrySet()) {
            JButton b = entry.getValue();
            if (entry.getKey().equals(screenKey)) {
                b.setForeground(ThemeColors.ACCENT);
                b.setBackground(ThemeColors.CARD_BG_HOVER);
            } else {
                b.setForeground(ThemeColors.TEXT_MUTED);
                b.setBackground(ThemeColors.CARD_BG);
            }
        }

        if (SCREEN_OVERVIEW.equals(screenKey)) {
            overviewView.refreshState();
        } else if (SCREEN_PITCHCRAFT.equals(screenKey)) {
            pitchCraftView.refreshDeckContent();
        } else if (SCREEN_HISTORY.equals(screenKey)) {
            historyView.reloadVenturesFromDatabase();
        }

        cardLayout.show(cardsContainer, screenKey);
    }

    public void onLoginSuccess(User user) {
        userPillLabel.setText(user.getUsername());

        // Load most recent venture if available
        new SwingWorker<List<VentureIdea>, Void>() {
            @Override
            protected List<VentureIdea> doInBackground() throws Exception {
                return new VentureDAO().getVenturesByUser(user.getId());
            }

            @Override
            protected void done() {
                try {
                    List<VentureIdea> ventures = get();
                    if (!ventures.isEmpty()) {
                        setCurrentVenture(ventures.get(0));
                    }
                } catch (Exception ignored) {
                }
                showScreen(SCREEN_OVERVIEW);
            }
        }.execute();
    }

    public void performLogout() {
        UserSession.getInstance().logout();
        userPillLabel.setText("Guest");
        showScreen(SCREEN_LOGIN);
    }

    public VentureIdea getCurrentVenture() {
        return currentVenture;
    }

    public void setCurrentVenture(VentureIdea currentVenture) {
        this.currentVenture = currentVenture;
        if (overviewView != null) overviewView.refreshState();
    }

    public CapTableState getCurrentCapTable() {
        return currentCapTable;
    }

    public void setCurrentCapTable(CapTableState currentCapTable) {
        this.currentCapTable = currentCapTable;
    }

    public FinancialLedger getCurrentLedger() {
        return currentLedger;
    }

    public void setCurrentLedger(FinancialLedger currentLedger) {
        this.currentLedger = currentLedger;
    }
}
