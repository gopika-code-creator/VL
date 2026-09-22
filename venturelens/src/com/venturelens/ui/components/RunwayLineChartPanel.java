package com.venturelens.ui.components;

import com.venturelens.utils.ThemeColors;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Native Line Chart implemented with pure Java Swing and Graphics2D.
 * Overrides paintComponent(Graphics g) to render the monthly cash-depletion curve,
 * zero-cash threshold, runway indicators, and formatted financial intervals.
 */
public class RunwayLineChartPanel extends JPanel {

    private final List<Double> monthlyBalances = new ArrayList<>();
    private final DecimalFormat currencyFormat = new DecimalFormat("$#,##0");

    public RunwayLineChartPanel() {
        setBackground(ThemeColors.CARD_BG);
        setOpaque(true);
        setPreferredSize(new Dimension(460, 240));
    }

    public void updateRunwayData(BigDecimal startingCash, BigDecimal netMonthlyBurn, int projectionMonths) {
        monthlyBalances.clear();
        double current = startingCash.doubleValue();
        double burn = netMonthlyBurn.doubleValue();

        monthlyBalances.add(Math.max(0.0, current));
        for (int m = 1; m <= projectionMonths; m++) {
            current -= burn;
            monthlyBalances.add(Math.max(0.0, current));
            if (current <= 0.0 && burn > 0.0) {
                break;
            }
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Padding
        int padLeft = 70;
        int padRight = 30;
        int padTop = 30;
        int padBottom = 40;

        int chartW = w - padLeft - padRight;
        int chartH = h - padTop - padBottom;

        if (chartW <= 0 || chartH <= 0 || monthlyBalances.isEmpty()) {
            g2.dispose();
            return;
        }

        // Find max balance for Y-axis scale
        double maxBal = 1.0;
        for (double b : monthlyBalances) {
            if (b > maxBal) maxBal = b;
        }

        // Draw horizontal grid lines & Y labels
        g2.setFont(ThemeColors.FONT_SMALL);
        FontMetrics fm = g2.getFontMetrics();
        int gridSteps = 4;
        for (int i = 0; i <= gridSteps; i++) {
            int y = padTop + (int) ((gridSteps - i) * (chartH / (double) gridSteps));
            double val = (maxBal / gridSteps) * i;

            g2.setColor(new Color(0x27, 0x38, 0x52));
            g2.drawLine(padLeft, y, padLeft + chartW, y);

            g2.setColor(ThemeColors.TEXT_MUTED);
            String label = currencyFormat.format(val);
            g2.drawString(label, padLeft - fm.stringWidth(label) - 8, y + 4);
        }

        // Zero-cash threshold warning line (dashed red at bottom)
        Stroke oldStroke = g2.getStroke();
        float[] dash = {6.0f, 4.0f};
        g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
        g2.setColor(new Color(0xEF, 0x44, 0x44, 180));
        int zeroY = padTop + chartH;
        g2.drawLine(padLeft, zeroY, padLeft + chartW, zeroY);
        g2.setStroke(oldStroke);

        // Compute points
        int n = monthlyBalances.size();
        int[] xPoints = new int[n];
        int[] yPoints = new int[n];

        for (int i = 0; i < n; i++) {
            xPoints[i] = padLeft + (int) (i * (chartW / (double) Math.max(1, n - 1)));
            double bal = monthlyBalances.get(i);
            yPoints[i] = padTop + (int) ((1.0 - (bal / maxBal)) * chartH);
        }

        // Fill area under curve with translucent emerald gradient
        GeneralPath area = new GeneralPath();
        area.moveTo(xPoints[0], padTop + chartH);
        for (int i = 0; i < n; i++) {
            area.lineTo(xPoints[i], yPoints[i]);
        }
        area.lineTo(xPoints[n - 1], padTop + chartH);
        area.closePath();

        GradientPaint gp = new GradientPaint(
                0, padTop, new Color(0x10, 0xB9, 0x81, 70),
                0, padTop + chartH, new Color(0x10, 0xB9, 0x81, 0)
        );
        g2.setPaint(gp);
        g2.fill(area);

        // Draw main depletion curve line
        g2.setColor(ThemeColors.ACCENT);
        g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < n - 1; i++) {
            g2.drawLine(xPoints[i], yPoints[i], xPoints[i + 1], yPoints[i + 1]);
        }
        g2.setStroke(oldStroke);

        // Draw Nodes and X axis month labels
        for (int i = 0; i < n; i++) {
            // Node circle
            g2.setColor(ThemeColors.CARD_BG);
            g2.fillOval(xPoints[i] - 5, yPoints[i] - 5, 10, 10);
            g2.setColor(ThemeColors.ACCENT);
            g2.drawOval(xPoints[i] - 5, yPoints[i] - 5, 10, 10);

            // X-axis label
            g2.setColor(ThemeColors.TEXT_MUTED);
            String xLabel = "M" + i;
            if (i == 0) xLabel = "Now";
            g2.drawString(xLabel, xPoints[i] - (fm.stringWidth(xLabel) / 2), padTop + chartH + 18);
        }

        g2.dispose();
    }
}
