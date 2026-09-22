package com.venturelens.ui.components;

import com.venturelens.utils.ThemeColors;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Native Donut / Pie Chart implemented with pure Java Swing and Graphics2D.
 * Overrides paintComponent(Graphics g) with high-fidelity antialiased arcs (g2.fillArc).
 * Renders equity distribution and financial percentages with a clean color legend.
 */
public class DonutChartPanel extends JPanel {

    public static class Slice {
        private final String label;
        private final double percentage;
        private final Color color;

        public Slice(String label, double percentage, Color color) {
            this.label = label;
            this.percentage = percentage;
            this.color = color;
        }

        public String getLabel() {
            return label;
        }

        public double getPercentage() {
            return percentage;
        }

        public Color getColor() {
            return color;
        }
    }

    private final List<Slice> slices = new ArrayList<>();
    private final DecimalFormat df = new DecimalFormat("#0.0'%'");
    private String centerTitle = "CapTable";
    private String centerSubtitle = "100%";

    public DonutChartPanel() {
        setBackground(ThemeColors.CARD_BG);
        setOpaque(true);
        setPreferredSize(new Dimension(320, 260));
    }

    public void setCenterText(String title, String subtitle) {
        this.centerTitle = title;
        this.centerSubtitle = subtitle;
        repaint();
    }

    public void setSlices(List<Slice> newSlices) {
        slices.clear();
        if (newSlices != null) {
            slices.addAll(newSlices);
        }
        repaint();
    }

    public void updateCapTableData(BigDecimal f1, BigDecimal f2, BigDecimal esop, BigDecimal investor) {
        slices.clear();
        slices.add(new Slice("Founder 1", f1.doubleValue(), new Color(0x10, 0xB9, 0x81))); // Emerald
        slices.add(new Slice("Founder 2", f2.doubleValue(), new Color(0x3B, 0x82, 0xF6))); // Blue
        slices.add(new Slice("ESOP Pool", esop.doubleValue(), new Color(0x8B, 0x5C, 0xF6))); // Purple
        if (investor.doubleValue() > 0.0) {
            slices.add(new Slice("Investors", investor.doubleValue(), new Color(0xF5, 0x9E, 0x0B))); // Amber
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        // Enable high quality rendering hints
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int width = getWidth();
        int height = getHeight();

        // Draw background
        g2.setColor(getBackground());
        g2.fillRect(0, 0, width, height);

        if (slices.isEmpty()) {
            g2.setColor(ThemeColors.TEXT_MUTED);
            g2.setFont(ThemeColors.FONT_BODY);
            FontMetrics fm = g2.getFontMetrics();
            String msg = "No Equity Data Available";
            g2.drawString(msg, (width - fm.stringWidth(msg)) / 2, height / 2);
            g2.dispose();
            return;
        }

        // Layout: Donut on Left/Center, Legend on Right
        int donutSize = Math.min(width / 2, height - 40);
        donutSize = Math.max(120, Math.min(donutSize, 180));
        int donutX = 30;
        int donutY = (height - donutSize) / 2;

        double totalPct = 0.0;
        for (Slice s : slices) {
            totalPct += s.getPercentage();
        }
        if (totalPct <= 0.0) totalPct = 100.0;

        // Draw Slices
        double curAngle = 90.0;
        for (Slice s : slices) {
            double extent = (s.getPercentage() / totalPct) * 360.0;
            g2.setColor(s.getColor());
            g2.fillArc(donutX, donutY, donutSize, donutSize, (int) Math.round(curAngle), (int) Math.round(-extent));
            curAngle -= extent;
        }

        // Inner Cutout for Donut Hole
        int holeSize = (int) (donutSize * 0.60);
        int holeX = donutX + (donutSize - holeSize) / 2;
        int holeY = donutY + (donutSize - holeSize) / 2;
        g2.setColor(ThemeColors.CARD_BG);
        g2.fill(new Ellipse2D.Double(holeX, holeY, holeSize, holeSize));

        // Center Text
        g2.setColor(ThemeColors.TEXT_PRIMARY);
        g2.setFont(ThemeColors.FONT_SECTION);
        FontMetrics fmTitle = g2.getFontMetrics();
        g2.drawString(centerTitle, holeX + (holeSize - fmTitle.stringWidth(centerTitle)) / 2, holeY + holeSize / 2 - 2);

        g2.setColor(ThemeColors.TEXT_MUTED);
        g2.setFont(ThemeColors.FONT_SMALL);
        FontMetrics fmSub = g2.getFontMetrics();
        g2.drawString(centerSubtitle, holeX + (holeSize - fmSub.stringWidth(centerSubtitle)) / 2, holeY + holeSize / 2 + 16);

        // Draw Legend on the Right
        int legendX = donutX + donutSize + 30;
        int legendY = (height - (slices.size() * 26)) / 2 + 14;

        g2.setFont(ThemeColors.FONT_BODY);
        FontMetrics fmL = g2.getFontMetrics();

        for (Slice s : slices) {
            // Color marker pill
            g2.setColor(s.getColor());
            g2.fillRoundRect(legendX, legendY - 10, 14, 14, 4, 4);

            // Label & percentage
            g2.setColor(ThemeColors.TEXT_PRIMARY);
            String labelText = s.getLabel();
            g2.drawString(labelText, legendX + 22, legendY + 2);

            g2.setColor(ThemeColors.TEXT_MUTED);
            String pctText = df.format(s.getPercentage());
            g2.drawString(pctText, legendX + 130, legendY + 2);

            legendY += 26;
        }

        g2.dispose();
    }
}
