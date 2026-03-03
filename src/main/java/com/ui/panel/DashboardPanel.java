package com.ui.panel;

import com.repository.StudentRepository;
import com.repository.ClassRepository;
import com.repository.InvoiceRepository;
import com.ui.util.UiUtil;

import javax.swing.*;
import java.awt.*;

/**
 * Dashboard overview panel – shows summary stats.
 * Stats are loaded in background via SwingWorker.
 */
public class DashboardPanel extends JPanel {

    private final JLabel lblStudents = new JLabel("...");
    private final JLabel lblClasses = new JLabel("...");
    private final JLabel lblInvoices = new JLabel("...");

    public DashboardPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UiUtil.COLOR_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = UiUtil.sectionTitle("Tổng quan hệ thống");
        add(title, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(1, 3, 15, 0));
        cards.setOpaque(false);
        cards.add(buildCard("Học viên", lblStudents, new Color(52, 152, 219)));
        cards.add(buildCard("Lớp học", lblClasses, new Color(46, 204, 113)));
        cards.add(buildCard("Hóa đơn", lblInvoices, new Color(230, 126, 34)));

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(cards, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);

        loadStats();
    }

    private JPanel buildCard(String label, JLabel valueLabel, Color color) {
        JPanel p = new JPanel(new GridLayout(2, 1, 0, 8));
        p.setBackground(color);
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lbl = new JLabel(label, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(new Color(220, 220, 220));

        p.add(valueLabel);
        p.add(lbl);
        return p;
    }

    private void loadStats() {
        new SwingWorker<long[], Void>() {
            @Override
            protected long[] doInBackground() {
                long students = new StudentRepository().findAll().size();
                long classes = new ClassRepository().findAll().size();
                long invoices = new InvoiceRepository().findAll().size();
                return new long[] { students, classes, invoices };
            }

            @Override
            protected void done() {
                try {
                    long[] stats = get();
                    lblStudents.setText(String.valueOf(stats[0]));
                    lblClasses.setText(String.valueOf(stats[1]));
                    lblInvoices.setText(String.valueOf(stats[2]));
                } catch (Exception ignored) {
                    lblStudents.setText("N/A");
                    lblClasses.setText("N/A");
                    lblInvoices.setText("N/A");
                }
            }
        }.execute();
    }
}
