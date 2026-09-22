package com.venturelens.ui;

import com.venturelens.dao.UserDAO;
import com.venturelens.model.User;
import com.venturelens.model.UserSession;
import com.venturelens.utils.ThemeColors;
import com.venturelens.utils.UIHelper;

import javax.swing.*;
import java.awt.*;

/**
 * Centered, dark-themed login card running in CardLayout.
 * Database authentication executes inside javax.swing.SwingWorker
 * to guarantee the Event Dispatch Thread (EDT) never freezes.
 */
public class LoginView extends JPanel {

    private final MainFrame mainFrame;
    private final UserDAO userDAO = new UserDAO();

    private final JTextField usernameField = UIHelper.createTextField(18);
    private final JPasswordField passwordField = UIHelper.createPasswordField(18);
    private final JLabel statusLabel = new JLabel(" ");
    private final JButton loginBtn = UIHelper.createPrimaryButton("Sign In to VentureLens");
    private final JButton goToRegisterBtn = UIHelper.createSecondaryButton("Create New Account");

    public LoginView(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());
        setBackground(ThemeColors.BASE_BG);

        // Centered Card Panel
        JPanel card = UIHelper.createCardPanel(32);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(420, 480));
        card.setMaximumSize(new Dimension(420, 480));

        // Header
        JLabel logoLabel = new JLabel("VentureLens");
        logoLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 26));
        logoLabel.setForeground(ThemeColors.ACCENT);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tagLabel = UIHelper.createSubtitleLabel("Startup OS & Decision Intelligence");
        tagLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLabel = UIHelper.createMutedLabel("Enter your credentials to access your venture suite.");
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Form Fields
        JLabel userLbl = UIHelper.createBodyLabel("Username");
        userLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        usernameField.setMaximumSize(new Dimension(360, 38));
        usernameField.setText("founder_alex"); // Pre-populate demo user

        JLabel passLbl = UIHelper.createBodyLabel("Password");
        passLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.setMaximumSize(new Dimension(360, 38));
        passwordField.setText("admin123"); // Demo password

        statusLabel.setFont(ThemeColors.FONT_SMALL);
        statusLabel.setForeground(ThemeColors.DANGER);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(360, 42));

        goToRegisterBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        goToRegisterBtn.setMaximumSize(new Dimension(360, 38));

        // Wire Actions
        loginBtn.addActionListener(e -> performLogin());
        goToRegisterBtn.addActionListener(e -> mainFrame.showScreen(MainFrame.SCREEN_REGISTER));

        // Assemble
        card.add(logoLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(tagLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(descLabel);
        card.add(Box.createVerticalStrut(24));

        card.add(userLbl);
        card.add(Box.createVerticalStrut(6));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(14));

        card.add(passLbl);
        card.add(Box.createVerticalStrut(6));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(14));

        card.add(statusLabel);
        card.add(Box.createVerticalStrut(14));

        card.add(loginBtn);
        card.add(Box.createVerticalStrut(10));
        card.add(goToRegisterBtn);

        add(card);
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setForeground(ThemeColors.DANGER);
            statusLabel.setText("Please enter both username and password.");
            return;
        }

        loginBtn.setEnabled(false);
        statusLabel.setForeground(ThemeColors.TEXT_MUTED);
        statusLabel.setText("Authenticating with MySQL...");

        // Execute DB login inside SwingWorker to keep EDT fluid
        new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws Exception {
                return userDAO.authenticate(username, password);
            }

            @Override
            protected void done() {
                loginBtn.setEnabled(true);
                try {
                    User user = get();
                    if (user != null) {
                        UserSession.getInstance().login(user);
                        statusLabel.setText(" ");
                        mainFrame.onLoginSuccess(user);
                    } else {
                        statusLabel.setForeground(ThemeColors.DANGER);
                        statusLabel.setText("Invalid credentials. Please verify your password.");
                    }
                } catch (Exception ex) {
                    statusLabel.setForeground(ThemeColors.DANGER);
                    statusLabel.setText("Authentication failed: " + ex.getMessage());
                }
            }
        }.execute();
    }
}
