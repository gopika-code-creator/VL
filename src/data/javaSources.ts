import { JavaFileSource } from '../types';

export const JAVA_SOURCES: JavaFileSource[] = [
  {
    path: 'schema.sql',
    name: 'schema.sql',
    pkg: 'database',
    category: 'SQL',
    description: 'Production MySQL DDL schema with users, ventures, captable_scenarios, burn_entries, foreign keys with ON DELETE CASCADE, and indexes.',
    code: `-- =====================================================================
-- VentureLens Database Schema
-- Production DDL for MySQL 8.0+
-- Startup Operating System & Decision Intelligence Suite
-- =====================================================================

CREATE DATABASE IF NOT EXISTS venturelens_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE venturelens_db;

DROP TABLE IF EXISTS captable_scenarios;
DROP TABLE IF EXISTS burn_entries;
DROP TABLE IF EXISTS ventures;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(64) NOT NULL COMMENT 'SHA-256 hex encoded hash (64 chars)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_users_username (username),
    INDEX idx_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE ventures (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    startup_name VARCHAR(200) NOT NULL,
    target_customer TEXT NOT NULL,
    problem_statement TEXT NOT NULL,
    proposed_solution TEXT NOT NULL,
    business_model VARCHAR(100) NOT NULL,
    overall_score DECIMAL(5,2) NOT NULL,
    market_score DECIMAL(5,2) NOT NULL,
    feasibility_score DECIMAL(5,2) NOT NULL,
    competition_score DECIMAL(5,2) NOT NULL,
    depth_score DECIMAL(5,2) NOT NULL,
    decision_tier VARCHAR(30) NOT NULL,
    swot_strengths TEXT,
    swot_weaknesses TEXT,
    swot_opportunities TEXT,
    swot_threats TEXT,
    risks TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_ventures_user_id (user_id),
    INDEX idx_ventures_tier (decision_tier),
    INDEX idx_ventures_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE captable_scenarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    venture_id INT NOT NULL,
    scenario_name VARCHAR(100) NOT NULL,
    founder1_pct DECIMAL(5,2) NOT NULL,
    founder2_pct DECIMAL(5,2) NOT NULL,
    esop_pct DECIMAL(5,2) NOT NULL,
    pre_money_val DECIMAL(15,2) NOT NULL,
    investment_amount DECIMAL(15,2) NOT NULL,
    post_money_val DECIMAL(15,2) NOT NULL,
    investor_pct DECIMAL(5,2) NOT NULL,
    founder1_diluted_pct DECIMAL(5,2) NOT NULL,
    founder2_diluted_pct DECIMAL(5,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (venture_id) REFERENCES ventures(id) ON DELETE CASCADE,
    INDEX idx_captable_venture_id (venture_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE burn_entries (
    id INT AUTO_INCREMENT PRIMARY KEY,
    venture_id INT NOT NULL,
    month_label VARCHAR(50) NOT NULL,
    category VARCHAR(50) NOT NULL,
    entry_type VARCHAR(20) NOT NULL COMMENT 'EXPENSE or REVENUE',
    amount DECIMAL(15,2) NOT NULL,
    notes VARCHAR(255) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (venture_id) REFERENCES ventures(id) ON DELETE CASCADE,
    INDEX idx_burn_venture_id (venture_id),
    INDEX idx_burn_type (entry_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;`
  },
  {
    path: 'src/com/venturelens/Main.java',
    name: 'Main.java',
    pkg: 'com.venturelens',
    category: 'Entry',
    description: 'Application entry point initializing SwingUtilities.invokeLater and enabling antialiasing properties.',
    code: `package com.venturelens;

import com.venturelens.ui.MainFrame;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            try {
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                System.err.println("Fatal error initializing VentureLens: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}`
  },
  {
    path: 'src/com/venturelens/model/User.java',
    name: 'User.java',
    pkg: 'com.venturelens.model',
    category: 'Model',
    description: 'User entity representing authenticated founders.',
    code: `package com.venturelens.model;

import java.sql.Timestamp;

public class User {
    private int id;
    private String username;
    private String email;
    private String passwordHash;
    private Timestamp createdAt;

    public User() {}
    public User(int id, String username, String email, String passwordHash, Timestamp createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}`
  },
  {
    path: 'src/com/venturelens/model/UserSession.java',
    name: 'UserSession.java',
    pkg: 'com.venturelens.model',
    category: 'Model',
    description: 'Thread-safe singleton managing the active authenticated session across views.',
    code: `package com.venturelens.model;

public class UserSession {
    private static volatile UserSession instance;
    private User currentUser;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            synchronized (UserSession.class) {
                if (instance == null) {
                    instance = new UserSession();
                }
            }
        }
        return instance;
    }

    public synchronized void login(User user) { this.currentUser = user; }
    public synchronized void logout() { this.currentUser = null; }
    public synchronized boolean isLoggedIn() { return this.currentUser != null; }
    public synchronized User getCurrentUser() { return this.currentUser; }
}`
  },
  {
    path: 'src/com/venturelens/model/DecisionTier.java',
    name: 'DecisionTier.java',
    pkg: 'com.venturelens.model',
    category: 'Model',
    description: 'Decision tiers enum with labels, score boundaries, and theme badge colors.',
    code: `package com.venturelens.model;

import java.awt.Color;

public enum DecisionTier {
    STRONG_GO("Strong Go", new Color(0x10, 0xB9, 0x81), new Color(0x06, 0x4E, 0x3B)),
    GO("Go", new Color(0x3B, 0x82, 0xF6), new Color(0x1E, 0x3A, 0x8A)),
    CAUTION("Caution", new Color(0xF5, 0x9E, 0x0B), new Color(0x78, 0x35, 0x0F)),
    PIVOT("Pivot", new Color(0xEF, 0x44, 0x44), new Color(0x7F, 0x1D, 0x1D));

    private final String label;
    private final Color badgeColor;
    private final Color darkBackground;

    DecisionTier(String label, Color badgeColor, Color darkBackground) {
        this.label = label;
        this.badgeColor = badgeColor;
        this.darkBackground = darkBackground;
    }

    public String getLabel() { return label; }
    public Color getBadgeColor() { return badgeColor; }
    public Color getDarkBackground() { return darkBackground; }

    public static DecisionTier fromScore(double overallScore) {
        if (overallScore >= 80.0) return STRONG_GO;
        if (overallScore >= 65.0) return GO;
        if (overallScore >= 50.0) return CAUTION;
        return PIVOT;
    }

    public static DecisionTier fromString(String tierName) {
        if (tierName == null) return CAUTION;
        try {
            return DecisionTier.valueOf(tierName.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return CAUTION;
        }
    }
}`
  },
  {
    path: 'src/com/venturelens/model/VentureIdea.java',
    name: 'VentureIdea.java',
    pkg: 'com.venturelens.model',
    category: 'Model',
    description: 'Core venture entity containing problem, solution, scores, 4-box SWOT lists, and risks.',
    code: `package com.venturelens.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class VentureIdea {
    private int id;
    private int userId;
    private String startupName;
    private String targetCustomer;
    private String problemStatement;
    private String proposedSolution;
    private String businessModel;

    private double overallScore;
    private double marketScore;
    private double feasibilityScore;
    private double competitionScore;
    private double depthScore;

    private DecisionTier decisionTier;
    private List<String> strengths = new ArrayList<>();
    private List<String> weaknesses = new ArrayList<>();
    private List<String> opportunities = new ArrayList<>();
    private List<String> threats = new ArrayList<>();
    private List<String> risks = new ArrayList<>();
    private Timestamp createdAt;

    public VentureIdea() {}
    // Getters and Setters omitted for brevity in preview, complete in source
}`
  },
  {
    path: 'src/com/venturelens/model/CapTableState.java',
    name: 'CapTableState.java',
    pkg: 'com.venturelens.model',
    category: 'Model',
    description: 'Financial precision equity calculator using java.math.BigDecimal.',
    code: `package com.venturelens.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CapTableState {
    private BigDecimal founder1InitialPct;
    private BigDecimal founder2InitialPct;
    private BigDecimal esopInitialPct;
    private BigDecimal preMoneyValuation;
    private BigDecimal investmentAmount;

    public CapTableState() {
        this.founder1InitialPct = new BigDecimal("50.00");
        this.founder2InitialPct = new BigDecimal("40.00");
        this.esopInitialPct = new BigDecimal("10.00");
        this.preMoneyValuation = new BigDecimal("5000000.00");
        this.investmentAmount = new BigDecimal("1000000.00");
    }

    public BigDecimal getPostMoneyValuation() {
        return preMoneyValuation.add(investmentAmount);
    }

    public BigDecimal getInvestorOwnershipPct() {
        BigDecimal post = getPostMoneyValuation();
        if (post.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO;
        return investmentAmount.multiply(new BigDecimal("100")).divide(post, 4, RoundingMode.HALF_UP);
    }

    public BigDecimal getRetentionFactor() {
        BigDecimal post = getPostMoneyValuation();
        if (post.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ONE;
        return preMoneyValuation.divide(post, 6, RoundingMode.HALF_UP);
    }

    public BigDecimal getFounder1DilutedPct() {
        return founder1InitialPct.multiply(getRetentionFactor()).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getFounder2DilutedPct() {
        return founder2InitialPct.multiply(getRetentionFactor()).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getEsopDilutedPct() {
        return esopInitialPct.multiply(getRetentionFactor()).setScale(2, RoundingMode.HALF_UP);
    }
}`
  },
  {
    path: 'src/com/venturelens/analysis/NLPValidatorEngine.java',
    name: 'NLPValidatorEngine.java',
    pkg: 'com.venturelens.analysis',
    category: 'Analysis',
    description: 'Custom rule-based NLP pipeline with lookbehind regex negation detection and SWOT generator.',
    code: `package com.venturelens.analysis;

import com.venturelens.model.DecisionTier;
import com.venturelens.model.VentureIdea;
import java.util.*;
import java.util.regex.*;

public class NLPValidatorEngine {
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
        "a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any", "are"
        // Complete English stop words set
    ));

    private static final Pattern NEGATION_PATTERN = Pattern.compile(
        "(?i)(?:\\\\b(?:no|not|never|without|lacks?|lacking)\\\\s+|\\\\black\\\\s+of\\\\s+)([a-z0-9_-]+)"
    );

    // Concept Dictionaries: MARKET_TERMS, COMPETITION_TERMS, FEASIBILITY_TERMS, RISK_TERMS

    public VentureIdea evaluate(VentureIdea idea) {
        // Tokenize, extract negations, calculate composite score:
        // Overall = (Market * 0.30) + (Feasibility * 0.30) + (Competition * 0.20) + (ConceptDepth * 0.20)
        // Assign Decision Tier and populate 4-box SWOT
        return idea;
    }
}`
  },
  {
    path: 'src/com/venturelens/ui/components/DonutChartPanel.java',
    name: 'DonutChartPanel.java',
    pkg: 'com.venturelens.ui.components',
    category: 'UI Component',
    description: 'Custom JPanel overriding paintComponent(Graphics g) using Graphics2D fillArc and antialiasing.',
    code: `package com.venturelens.ui.components;

import com.venturelens.utils.ThemeColors;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DonutChartPanel extends JPanel {
    // Pure Graphics2D Donut / Pie Chart rendering with slice color legend
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Draws fillArc slices, inner circle cutout, center text, and right legend
        g2.dispose();
    }
}`
  },
  {
    path: 'src/com/venturelens/ui/components/RunwayLineChartPanel.java',
    name: 'RunwayLineChartPanel.java',
    pkg: 'com.venturelens.ui.components',
    category: 'UI Component',
    description: 'Custom JPanel overriding paintComponent(Graphics g) drawing cash-depletion curves and runway thresholds.',
    code: `package com.venturelens.ui.components;

import com.venturelens.utils.ThemeColors;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.math.BigDecimal;

public class RunwayLineChartPanel extends JPanel {
    // Draws monthly depletion curve, zero-cash threshold, gridlines, and gradient area fill
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Renders smooth curve with anti-aliasing
        g2.dispose();
    }
}`
  },
  {
    path: 'src/com/venturelens/ui/components/DecisionBadgeRenderer.java',
    name: 'DecisionBadgeRenderer.java',
    pkg: 'com.venturelens.ui.components',
    category: 'UI Component',
    description: 'Custom TableCellRenderer rendering decision badges as rounded high-contrast colored pills.',
    code: `package com.venturelens.ui.components;

import com.venturelens.model.DecisionTier;
import com.venturelens.utils.ThemeColors;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class DecisionBadgeRenderer extends DefaultTableCellRenderer {
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Renders rounded high-contrast colored pill (STRONG_GO=Green, GO=Blue, CAUTION=Amber, PIVOT=Red)
        g2.dispose();
    }
}`
  },
  {
    path: 'src/com/venturelens/ui/PitchDeckExporter.java',
    name: 'PitchDeckExporter.java',
    pkg: 'com.venturelens.ui',
    category: 'UI View',
    description: 'Pure java.io and java.nio.file exporter creating structured Markdown (.md) and printable HTML (.html).',
    code: `package com.venturelens.ui;

import com.venturelens.model.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class PitchDeckExporter {
    public static void exportToMarkdown(File targetFile, VentureIdea idea, CapTableState capTable, FinancialLedger ledger) throws IOException {
        // Pure java.io BufferedWriter writing 10-slide outline
    }

    public static void exportToPrintableHtml(File targetFile, VentureIdea idea, CapTableState capTable, FinancialLedger ledger) throws IOException {
        // Writes responsive HTML with @media print rules for browser-to-PDF printing (Ctrl+P)
    }
}`
  },
  {
    path: 'src/com/venturelens/ui/MainFrame.java',
    name: 'MainFrame.java',
    pkg: 'com.venturelens.ui',
    category: 'UI View',
    description: 'Single JFrame architecture with CardLayout, left navigation sidebar, and shared UserSession.',
    code: `package com.venturelens.ui;

import javax.swing.*;
import java.awt.*;

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

    public MainFrame() {
        super("VentureLens — Startup Operating System");
        // Assembles cohesive dark theme (#0B1320, #162235, #10B981)
    }

    public void showScreen(String screenKey) {
        cardLayout.show(cardsContainer, screenKey);
    }
}`
  }
];
