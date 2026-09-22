package com.venturelens.ui.components;

import com.venturelens.model.DecisionTier;
import com.venturelens.utils.ThemeColors;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Custom TableCellRenderer rendering decision badges as rounded, high-contrast
 * colored pills (Green for STRONG_GO, Blue for GO, Amber for CAUTION, Red for PIVOT).
 */
public class DecisionBadgeRenderer extends DefaultTableCellRenderer {

    private DecisionTier currentTier = DecisionTier.CAUTION;
    private boolean isCellSelected = false;

    public DecisionBadgeRenderer() {
        setOpaque(false);
        setHorizontalAlignment(SwingConstants.CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        this.isCellSelected = isSelected;

        if (value instanceof DecisionTier) {
            currentTier = (DecisionTier) value;
            setText(currentTier.getLabel());
        } else if (value != null) {
            currentTier = DecisionTier.fromString(value.toString());
            setText(currentTier.getLabel());
        } else {
            currentTier = DecisionTier.CAUTION;
            setText("Caution");
        }

        setFont(ThemeColors.FONT_BOLD);
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Optional row selection background
        if (isCellSelected) {
            g2.setColor(ThemeColors.CARD_BG_HOVER);
            g2.fillRect(0, 0, w, h);
        }

        // Pill dimensions
        int pillW = Math.min(w - 12, 110);
        int pillH = Math.min(h - 8, 24);
        int pillX = (w - pillW) / 2;
        int pillY = (h - pillH) / 2;

        Color bg = currentTier.getDarkBackground();
        Color fg = currentTier.getBadgeColor();

        // Fill pill
        g2.setColor(bg);
        g2.fillRoundRect(pillX, pillY, pillW, pillH, pillH, pillH);

        // Border outline
        g2.setColor(fg);
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawRoundRect(pillX, pillY, pillW, pillH, pillH, pillH);

        // Text
        g2.setColor(fg);
        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        String text = getText();
        int textX = pillX + (pillW - fm.stringWidth(text)) / 2;
        int textY = pillY + ((pillH - fm.getHeight()) / 2) + fm.getAscent();
        g2.drawString(text, textX, textY);

        g2.dispose();
    }
}
