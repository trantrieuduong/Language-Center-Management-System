package com.ui.chart;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;


public class BarChartPanel extends JPanel {

    private static final Color BAR_COLOR = new Color(41, 128, 185);
    private static final Color BAR_HOVER_COLOR = new Color(52, 152, 219);
    private static final Color GRID_COLOR = new Color(220, 220, 220);
    private static final Color AXIS_COLOR = new Color(100, 100, 100);

    private static final int PAD_LEFT = 72;
    private static final int PAD_RIGHT = 20;
    private static final int PAD_TOP = 20;
    private static final int PAD_BOTTOM = 48;
    private static final int BAR_GAP = 8;

    private static final NumberFormat VND_SHORT = NumberFormat.getInstance(Locale.forLanguageTag("vi-VN"));

    private Map<String, BigDecimal> data = new LinkedHashMap<>();
    private String emptyMessage = "Không có dữ liệu";
    private int hoveredBar = -1;

    public BarChartPanel() {
        setOpaque(false);
        setPreferredSize(new Dimension(500, 220));

        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                int newHover = barIndexAt(e.getX(), e.getY());
                if (newHover != hoveredBar) {
                    hoveredBar = newHover;
                    repaint();
                }
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                hoveredBar = -1;
                repaint();
            }
        });
    }

    public void setData(Map<String, BigDecimal> data) {
        this.data = data != null ? data : new LinkedHashMap<>();
        hoveredBar = -1;
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

        if (data.isEmpty()) {
            drawEmpty(g2, w, h);
            g2.dispose();
            return;
        }

        String[] labels = data.keySet().toArray(new String[0]);
        BigDecimal[] values = data.values().toArray(new BigDecimal[0]);
        int n = labels.length;

        BigDecimal maxVal = java.util.Arrays.stream(values)
                .reduce(BigDecimal.ZERO, BigDecimal::max);
        if (maxVal.compareTo(BigDecimal.ZERO) == 0) maxVal = BigDecimal.ONE;

        int chartW = w - PAD_LEFT - PAD_RIGHT;
        int chartH = h - PAD_TOP - PAD_BOTTOM;

        // --- Grid lines (5 horizontal) ---
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        FontMetrics fm = g2.getFontMetrics();
        int gridLines = 4;
        for (int i = 0; i <= gridLines; i++) {
            int y = PAD_TOP + chartH - (int) ((double) i / gridLines * chartH);
            g2.setColor(GRID_COLOR);
            g2.setStroke(new BasicStroke(i == 0 ? 1.5f : 0.8f));
            g2.drawLine(PAD_LEFT, y, PAD_LEFT + chartW, y);

            // Y-axis label
            BigDecimal labelVal = maxVal.multiply(BigDecimal.valueOf((double) i / gridLines));
            String yLbl = formatShort(labelVal);
            g2.setColor(AXIS_COLOR);
            int lw = fm.stringWidth(yLbl);
            g2.drawString(yLbl, PAD_LEFT - lw - 6, y + fm.getAscent() / 2);
        }

        // --- Bars ---
        int barW = (chartW - (n + 1) * BAR_GAP) / n;
        barW = Math.max(barW, 4);

        for (int i = 0; i < n; i++) {
            int x = PAD_LEFT + BAR_GAP + i * (barW + BAR_GAP);
            double ratio = values[i].doubleValue() / maxVal.doubleValue();
            int barH = (int) (ratio * chartH);
            int y = PAD_TOP + chartH - barH;

            // Bar fill
            Color fillColor = (i == hoveredBar) ? BAR_HOVER_COLOR : BAR_COLOR;
            g2.setColor(fillColor);
            if (barH > 0) {
                int arc = Math.min(6, barW / 2);
                g2.fill(new RoundRectangle2D.Double(x, y, barW, barH, arc, arc));
                // Clip bottom corners square
                g2.fillRect(x, y + barH / 2, barW, barH / 2);
            }

            // Value label above bar (show only on hover or if few bars)
            if (i == hoveredBar || n <= 7) {
                String valLbl = formatShort(values[i]);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                FontMetrics vfm = g2.getFontMetrics();
                int vw = vfm.stringWidth(valLbl);
                g2.setColor(new Color(50, 50, 50));
                if (i == hoveredBar) g2.setColor(BAR_COLOR);
                g2.drawString(valLbl, x + (barW - vw) / 2, Math.max(y - 3, PAD_TOP + vfm.getAscent()));
            }

            // X-axis label
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            FontMetrics xfm = g2.getFontMetrics();
            int xlw = xfm.stringWidth(labels[i]);
            g2.setColor(AXIS_COLOR);
            g2.drawString(labels[i], x + (barW - xlw) / 2, PAD_TOP + chartH + xfm.getAscent() + 4);
        }

        // --- Y-axis line ---
        g2.setColor(new Color(180, 180, 180));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(PAD_LEFT, PAD_TOP, PAD_LEFT, PAD_TOP + chartH);

        g2.dispose();
    }

    private int barIndexAt(int mx, int my) {
        int n = data.size();
        if (n == 0) return -1;
        int w = getWidth();
        int chartW = w - PAD_LEFT - PAD_RIGHT;
        int chartH = getHeight() - PAD_TOP - PAD_BOTTOM;
        int barW = (chartW - (n + 1) * BAR_GAP) / n;
        barW = Math.max(barW, 4);

        for (int i = 0; i < n; i++) {
            int x = PAD_LEFT + BAR_GAP + i * (barW + BAR_GAP);
            if (mx >= x && mx <= x + barW && my >= PAD_TOP && my <= PAD_TOP + chartH) {
                return i;
            }
        }
        return -1;
    }

    private String formatShort(BigDecimal val) {
        double d = val.doubleValue();
        if (d >= 1_000_000_000) return String.format("%.1fB", d / 1_000_000_000);
        if (d >= 1_000_000) return String.format("%.1fM", d / 1_000_000);
        if (d >= 1_000) return String.format("%.0fK", d / 1_000);
        return VND_SHORT.format(val);
    }

    private void drawEmpty(Graphics2D g2, int w, int h) {
        g2.setColor(new Color(200, 200, 200));
        g2.setStroke(new BasicStroke(1.5f));

        // Draw X and Y axis
        g2.drawLine(PAD_LEFT, PAD_TOP, PAD_LEFT, h - PAD_BOTTOM);
        g2.drawLine(PAD_LEFT, h - PAD_BOTTOM, w - PAD_RIGHT, h - PAD_BOTTOM);

        g2.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        FontMetrics fm = g2.getFontMetrics();
        int sw = fm.stringWidth(emptyMessage);
        g2.drawString(emptyMessage, (w - sw) / 2, h / 2);
    }
}
