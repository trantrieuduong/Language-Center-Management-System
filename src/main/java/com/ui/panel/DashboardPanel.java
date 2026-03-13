package com.ui.panel;

import com.model.financial.Invoice;
import com.model.financial.InvoiceStatus;
import com.repository.ClassRepository;
import com.repository.InvoiceRepository;
import com.repository.StudentRepository;
import com.stream.InvoiceStreamQueries;
import com.ui.chart.BarChartPanel;
import com.ui.chart.PieChartPanel;
import com.ui.util.UiUtil;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DashboardPanel extends JPanel {

    private final JLabel lblStudents = new JLabel("...");
    private final JLabel lblClasses = new JLabel("...");
    private final JLabel lblInvoices = new JLabel("...");

    private final PieChartPanel pieChartPanel = new PieChartPanel();
    private final BarChartPanel barChartPanel = new BarChartPanel();

    private final JComboBox<String> cboRevenueType = new JComboBox<>(
            new String[]{"7 ngày gần nhất", "4 tuần gần nhất", "6 tháng gần nhất"}
    );
    private final JLabel lblRevenueTitle = new JLabel("Doanh thu 7 ngày gần nhất");

    private List<Invoice> cachedInvoices = Collections.emptyList();

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

        JPanel chartGrid = new JPanel(new GridLayout(1, 2, 15, 0));
        chartGrid.setOpaque(false);
        chartGrid.add(buildChartCard("Tình trạng hóa đơn", pieChartPanel));
        chartGrid.add(buildRevenueChartCard());

        JPanel center = new JPanel(new BorderLayout(0, 20));
        center.setOpaque(false);
        center.add(cards, BorderLayout.NORTH);
        center.add(chartGrid, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);

        cboRevenueType.addActionListener(e -> refreshRevenueChart());

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

    private JPanel buildChartCard(String title, JComponent chart) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(new Color(60, 60, 60));

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(chart, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildRevenueChartCard() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JPanel top = new JPanel(new BorderLayout(10, 0));
        top.setOpaque(false);

        lblRevenueTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblRevenueTitle.setForeground(new Color(60, 60, 60));

        cboRevenueType.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        top.add(lblRevenueTitle, BorderLayout.WEST);
        top.add(cboRevenueType, BorderLayout.EAST);

        panel.add(top, BorderLayout.NORTH);
        panel.add(barChartPanel, BorderLayout.CENTER);

        return panel;
    }

    private void loadStats() {
        new SwingWorker<DashboardData, Void>() {
            @Override
            protected DashboardData doInBackground() {
                long students = new StudentRepository().findAll().size();
                long classes = new ClassRepository().findAll().size();
                List<Invoice> invoices = new InvoiceRepository().findAll();

                Map<String, Long> statusData = convertStatusMap(InvoiceStreamQueries.countByStatus(invoices));

                return new DashboardData(students, classes, invoices, statusData);
            }

            @Override
            protected void done() {
                try {
                    DashboardData data = get();

                    lblStudents.setText(String.valueOf(data.students));
                    lblClasses.setText(String.valueOf(data.classes));
                    lblInvoices.setText(String.valueOf(data.invoices.size()));

                    cachedInvoices = data.invoices;
                    pieChartPanel.setData(data.statusData);
                    refreshRevenueChart();

                } catch (Exception ignored) {
                    lblStudents.setText("N/A");
                    lblClasses.setText("N/A");
                    lblInvoices.setText("N/A");

                    cachedInvoices = Collections.emptyList();
                    pieChartPanel.setData(new LinkedHashMap<>());
                    barChartPanel.setData(new LinkedHashMap<>());
                }
            }
        }.execute();
    }

    private void refreshRevenueChart() {
        Map<String, BigDecimal> revenueData;

        int selected = cboRevenueType.getSelectedIndex();
        switch (selected) {
            case 0:
                lblRevenueTitle.setText("Doanh thu 7 ngày gần nhất");
                revenueData = InvoiceStreamQueries.revenueByDay(cachedInvoices);
                break;
            case 1:
                lblRevenueTitle.setText("Doanh thu 4 tuần gần nhất");
                revenueData = InvoiceStreamQueries.revenueByWeek(cachedInvoices);
                break;
            case 2:
            default:
                lblRevenueTitle.setText("Doanh thu 6 tháng gần nhất");
                revenueData = InvoiceStreamQueries.revenueByMonth(cachedInvoices);
                break;
        }

        barChartPanel.setData(revenueData);
    }

    private Map<String, Long> convertStatusMap(Map<InvoiceStatus, Long> raw) {
        Map<String, Long> result = new LinkedHashMap<>();
        result.put("PENDING", raw.getOrDefault(InvoiceStatus.PENDING, 0L));
        result.put("PAID", raw.getOrDefault(InvoiceStatus.PAID, 0L));
        result.put("CANCELED", raw.getOrDefault(InvoiceStatus.CANCELED, 0L));
        return result;
    }

    private static class DashboardData {
        long students;
        long classes;
        List<Invoice> invoices;
        Map<String, Long> statusData;

        DashboardData(long students, long classes, List<Invoice> invoices, Map<String, Long> statusData) {
            this.students = students;
            this.classes = classes;
            this.invoices = invoices;
            this.statusData = statusData;
        }
    }
}
