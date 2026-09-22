package com.venturelens.ui;

import com.venturelens.dao.UserDAO;
import com.venturelens.model.User;
import com.venturelens.model.UserSession;
import com.venturelens.utils.ThemeColors;
import com.venturelens.utils.UIHelper;

import javax.swing.*;
import java.awt.*;

/**
 * Centered, dark-themed registration card running in CardLayout.
 * Inserts new users using SHA-256 password hashing inside SwingWorker.
 */
public class RegisterView extends JPanel {

    private final MainFrame mainFrame;
    private final UserDAO userDAO = new UserDAO();

    private final JTextField usernameField = UIHelper.createTextField(18);
    private final JTextField emailField = UIHelper.createTextField(18);
    private final JPasswordField passwordField = UIHelper.createPasswordField(18);
    private final JPasswordField confirmPasswordField = UIHelper.createPasswordField(18);
    private final JLabel statusLabel = new JLabel(" ");
    private final JButton registerBtn = UIHelper.createPrimaryButton("Create VentureLens Account");
    private final JButton backToLoginBtn = UIHelper.createSecondaryButton("Back to Sign In");

    public RegisterView(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());
        setBackground(ThemeColors.BASE_BG);

        // Centered Card Panel
        JPanel card = UIHelper.createCardPanel(28);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(420, 540));
        card.setMaximumSize(new Dimension(420, 540));

        JLabel titleLabel = UIHelper.createTitleLabel("Founder Registration");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLabel = UIHelper.createMutedLabel("Set up your secure VentureLens workspace.");
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel userLbl = UIHelper.createBodyLabel("Username");
        userLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        usernameField.setMaximumSize(new Dimension(360, 36));

        JLabel emailLbl = UIHelper.createBodyLabel("Founder Email");
        emailLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailField.setMaximumSize(new Dimension(360, 36));

        JLabel passLbl = UIHelper.createBodyLabel("Password");
        passLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.setMaximumSize(new Dimension(360, 36));

        JLabel confirmLbl = UIHelper.createBodyLabel("Confirm Password");
        confirmLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        confirmPasswordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        confirmPasswordField.setMaximumSize(new Dimension(360, 36));

        statusLabel.setFont(ThemeColors.FONT_SMALL);
        statusLabel.setForeground(ThemeColors.DANGER);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerBtn.setMaximumSize(new Dimension(360, 40));

        backToLoginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backToLoginBtn.setMaximumSize(new Dimension(360, 36));

        registerBtn.addActionListener(e -> performRegistration());
        backToLoginBtn.addActionListener(e -> mainFrame.showScreen(MainFrame.SCREEN_LOGIN));

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(descLabel);
        card.add(Box.createVerticalStrut(18));

        card.add(userLbl);
        card.add(Box.createVerticalStrut(4));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(10));

        card.add(emailLbl);
        card.add(Box.createVerticalStrut(4));
        card.add(emailField);
        card.add(Box.createVerticalStrut(10));

        card.add(passLbl);
        card.add(Box.createVerticalStrut(4));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(10));

        card.add(confirmLbl);
        card.add(Box.createVerticalStrut(4));
        card.add(confirmPasswordField);
        card.add(Box.createVerticalStrut(12));

        card.add(statusLabel);
        card.add(Box.createVerticalStrut(12));

        card.add(registerBtn);
        card.add(Box.createVerticalStrut(8));
        card.add(backToLoginBtn);

        add(card);
    }

    private void performRegistration() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String confirm = new String(confirmPasswordField.getPassword()).trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            statusLabel.setForeground(ThemeColors.DANGER);
            statusLabel.setText("Please fill in all registration fields.");
            return;
        }

        if (!password.equals(confirm)) {
            statusLabel.setForeground(ThemeColors.DANGER);
            statusLabel.setText("Passwords do not match.");
            return;
        }

        registerBtn.setEnabled(false);
        statusLabel.setForeground(ThemeColors.TEXT_MUTED);
        statusLabel.setText("Creating account in MySQL...");

        new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws Exception {
                return userDAO.register(username, email, password);
            }

            @Override
            protected void done() {
                registerBtn.setEnabled(true);
                try {
                    User newUser = get();
                    if (newUser != null) {
                        UserSession.getInstance().login(newUser);
                        mainFrame.onLoginSuccess(newUser);
                    }
                } catch (Exception ex) {
                    statusLabel.setForeground(ThemeColors.DANGER);
                    statusLabel.setText("Registration error: " + ex.getMessage());
                }
            }
        }.execute();
    }
}
