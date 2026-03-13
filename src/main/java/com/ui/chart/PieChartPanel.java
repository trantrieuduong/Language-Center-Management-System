package com.ui.chart;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.util.LinkedHashMap;
import java.util.Map;


public class PieChartPanel extends JPanel {

    private static final Color[] DEFAULT_COLORS = {
            new Color(243, 156, 18), // – PENDING
            new Color(39, 174, 96), // – PAID
            new Color(192, 57, 43), // – CANCELED
    };

    private Map<String, Long> data = new LinkedHashMap<>();
    private String emptyMessage = "Không có dữ liệu";

    public PieChartPanel() {
        setOpaque(false);
        setPreferredSize(new Dimension(320, 220));
    }

    public void setData(Map<String, Long> data) {
        this.data = data != null ? data : new LinkedHashMap<>();
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

        long total = data.values().stream().mapToLong(Long::longValue).sum();

        if (total == 0) {
            drawEmpty(g2, w, h);
            g2.dispose();
            return;
        }

        // --- Pie dimensions ---
        int legendWidth = 130;
        int bottomSpace = 28;
        int pieSize = Math.min(w - legendWidth - 20, h - bottomSpace);
        pieSize = Math.max(pieSize, 60);

        int pieX = (w - legendWidth - pieSize) / 2;
        int pieY = Math.max(8, (h - bottomSpace - pieSize) / 2);

        // --- Draw slices ---
        double startAngle = 0;
        int colorIdx = 0;
        String[] labels = data.keySet().toArray(new String[0]);
        long[] counts = data.values().stream().mapToLong(Long::longValue).toArray();

        for (int i = 0; i < labels.length; i++) {
            double sweep = 360.0 * counts[i] / total;
            Color c = DEFAULT_COLORS[colorIdx % DEFAULT_COLORS.length];
            g2.setColor(c);
            g2.fill(new Arc2D.Double(pieX, pieY, pieSize, pieSize, startAngle, sweep, Arc2D.PIE));

            // thin white border between slices
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(1.5f));
            g2.draw(new Arc2D.Double(pieX, pieY, pieSize, pieSize, startAngle, sweep, Arc2D.PIE));

            startAngle += sweep;
            colorIdx++;
        }

        // --- Draw percentage labels inside slices ---
        startAngle = 0;
        colorIdx = 0;
        Font pctFont = new Font("Segoe UI", Font.BOLD, 11);
        g2.setFont(pctFont);
        int cx = pieX + pieSize / 2;
        int cy = pieY + pieSize / 2;
        int r = pieSize / 2;
        for (int i = 0; i < labels.length; i++) {
            if (counts[i] == 0) { startAngle += 0; colorIdx++; continue; }
            double sweep = 360.0 * counts[i] / total;
            double mid = Math.toRadians(startAngle + sweep / 2);
            int tx = cx + (int) (r * 0.6 * Math.cos(mid));
            int ty = cy - (int) (r * 0.6 * Math.sin(mid));

            String pct = String.format("%.0f%%", 100.0 * counts[i] / total);
            FontMetrics fm = g2.getFontMetrics();
            int sw = fm.stringWidth(pct);

            g2.setColor(new Color(0, 0, 0, 100));
            g2.drawString(pct, tx - sw / 2 + 1, ty + fm.getAscent() / 2 + 1);
            g2.setColor(Color.WHITE);
            g2.drawString(pct, tx - sw / 2, ty + fm.getAscent() / 2);

            startAngle += sweep;
            colorIdx++;
        }

        // --- Legend ---
        int legX = pieX + pieSize + 18;
        int legY = pieY + (pieSize - labels.length * 26) / 2;
        Font legFont = new Font("Segoe UI", Font.PLAIN, 12);
        g2.setFont(legFont);
        FontMetrics lfm = g2.getFontMetrics();

        for (int i = 0; i < labels.length; i++) {
            Color c = DEFAULT_COLORS[i % DEFAULT_COLORS.length];
            int y = legY + i * 26;

            // color dot
            g2.setColor(c);
            g2.fill(new Ellipse2D.Double(legX, y + 2, 12, 12));

            // label + count
            g2.setColor(new Color(50, 50, 50));
            String txt = labels[i] + ": " + counts[i];
            g2.drawString(txt, legX + 18, y + 2 + lfm.getAscent());
        }

        // --- Total in center ---
        Font totalFont = new Font("Segoe UI", Font.BOLD, 13);
        g2.setFont(totalFont);
        FontMetrics tfm = g2.getFontMetrics();
        String totalStr = "Tổng: " + total;
        int tsw = tfm.stringWidth(totalStr);
        g2.setColor(new Color(50, 50, 50));
        g2.drawString(totalStr, cx - tsw / 2, pieY + pieSize + 18);

        g2.dispose();
    }

    private void drawEmpty(Graphics2D g2, int w, int h) {
        g2.setColor(new Color(180, 180, 180));
        int size = Math.min(w - 150, h - 40);
        size = Math.max(size, 60);
        int px = (w - 150 - size) / 2;
        int py = (h - size) / 2;
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                0, new float[]{6, 4}, 0));
        g2.draw(new Ellipse2D.Double(px, py, size, size));

        g2.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        FontMetrics fm = g2.getFontMetrics();
        int sw = fm.stringWidth(emptyMessage);
        g2.drawString(emptyMessage, (w - sw) / 2, h / 2 + fm.getAscent() / 2);
    }
}
