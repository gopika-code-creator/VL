package com.venturelens.utils;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Standard AWT/Swing helper to assemble cohesive, modern dark-themed components
 * without third-party Look-and-Feel libraries.
 */
public final class UIHelper {
    private UIHelper() {}

    public static JPanel createCardPanel() {
        JPanel card = new JPanel();
        card.setBackground(ThemeColors.CARD_BG);
        card.setBorder(new CompoundBorder(
                new LineBorder(ThemeColors.BORDER, 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));
        return card;
    }

    public static JPanel createCardPanel(int padding) {
        JPanel card = new JPanel();
        card.setBackground(ThemeColors.CARD_BG);
        card.setBorder(new CompoundBorder(
                new LineBorder(ThemeColors.BORDER, 1, true),
                new EmptyBorder(padding, padding, padding, padding)
        ));
        return card;
    }

    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(ThemeColors.FONT_TITLE);
        label.setForeground(ThemeColors.TEXT_PRIMARY);
        return label;
    }

    public static JLabel createSubtitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(ThemeColors.FONT_SUBTITLE);
        label.setForeground(ThemeColors.TEXT_MUTED);
        return label;
    }

    public static JLabel createBodyLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(ThemeColors.FONT_BODY);
        label.setForeground(ThemeColors.TEXT_PRIMARY);
        return label;
    }

    public static JLabel createMutedLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(ThemeColors.FONT_SMALL);
        label.setForeground(ThemeColors.TEXT_MUTED);
        return label;
    }

    public static JTextField createTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setBackground(ThemeColors.INPUT_BG);
        field.setForeground(ThemeColors.TEXT_PRIMARY);
        field.setCaretColor(ThemeColors.ACCENT);
        field.setFont(ThemeColors.FONT_BODY);
        field.setBorder(new CompoundBorder(
                new LineBorder(ThemeColors.BORDER, 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }

    public static JPasswordField createPasswordField(int columns) {
        JPasswordField field = new JPasswordField(columns);
        field.setBackground(ThemeColors.INPUT_BG);
        field.setForeground(ThemeColors.TEXT_PRIMARY);
        field.setCaretColor(ThemeColors.ACCENT);
        field.setFont(ThemeColors.FONT_BODY);
        field.setBorder(new CompoundBorder(
                new LineBorder(ThemeColors.BORDER, 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }

    public static JTextArea createTextArea(int rows, int cols) {
        JTextArea area = new JTextArea(rows, cols);
        area.setBackground(ThemeColors.INPUT_BG);
        area.setForeground(ThemeColors.TEXT_PRIMARY);
        area.setCaretColor(ThemeColors.ACCENT);
        area.setFont(ThemeColors.FONT_BODY);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(new EmptyBorder(8, 10, 8, 10));
        return area;
    }

    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(ThemeColors.FONT_BOLD);
        btn.setForeground(Color.WHITE);
        btn.setBackground(ThemeColors.ACCENT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(ThemeColors.ACCENT_HOVER);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(ThemeColors.ACCENT);
            }
        });
        return btn;
    }

    public static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(ThemeColors.FONT_BOLD);
        btn.setForeground(ThemeColors.TEXT_PRIMARY);
        btn.setBackground(ThemeColors.CARD_BG_HOVER);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new CompoundBorder(
                new LineBorder(ThemeColors.BORDER, 1, true),
                new EmptyBorder(8, 16, 8, 16)
        ));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(0x27, 0x38, 0x54));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(ThemeColors.CARD_BG_HOVER);
            }
        });
        return btn;
    }

    public static JScrollPane wrapInStyledScrollPane(JComponent component) {
        JScrollPane scrollPane = new JScrollPane(component);
        scrollPane.getViewport().setBackground(ThemeColors.INPUT_BG);
        scrollPane.setBackground(ThemeColors.INPUT_BG);
        scrollPane.setBorder(new LineBorder(ThemeColors.BORDER, 1, true));
        return scrollPane;
    }
}
