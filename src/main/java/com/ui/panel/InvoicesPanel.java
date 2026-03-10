package com.ui.panel;

import com.exception.AppException;
import com.model.financial.Invoice;
import com.model.financial.InvoiceStatus;
import com.security.CurrentUser;
import com.security.SecurityContext;
import com.service.impl.InvoiceServiceImpl;
import com.stream.InvoiceStreamQueries;
import com.ui.table.InvoiceTableModel;
import com.ui.util.MessageBox;
import com.ui.util.UiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class InvoicesPanel extends JPanel {

    private static final Logger log = LoggerFactory.getLogger(InvoicesPanel.class);

    private final InvoiceServiceImpl service = new InvoiceServiceImpl();
    private final InvoiceTableModel model = new InvoiceTableModel();
    private final JTable table = new JTable(model);

    private final JTextField tfSearch = UiUtil.searchField("Tìm theo tên học viên / lớp học...");
    private final JComboBox<InvoiceStatus> cbStatus = buildStatusComboBox();
    private final JButton btnUpdate = UiUtil.primaryButton("Cập nhật trạng thái");
    private final JButton btnRefresh = new JButton("Làm mới");

    // ---- factory ----

    private static JComboBox<InvoiceStatus> buildStatusComboBox() {
        JComboBox<InvoiceStatus> cb = new JComboBox<>();
        cb.addItem(null); // "Tất cả"
        for (InvoiceStatus s : InvoiceStatus.values())
            cb.addItem(s);
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value == null ? "Tất cả" : switch ((InvoiceStatus) value) {
                    case PENDING -> "Chờ thanh toán";
                    case PAID -> "Đã thanh toán";
                    case CANCELED -> "Đã hủy";
                });
                return this;
            }
        });
        return cb;
    }

    // ---- constructor ----

    public InvoicesPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UiUtil.COLOR_BG);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
        add(buildToolbar(), BorderLayout.SOUTH);

        wireEvents();
        applyRoleVisibility();
        loadData(null);
    }

    // ---- builders ----

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setOpaque(false);
        p.add(UiUtil.sectionTitle("Quản lý Hóa đơn"), BorderLayout.WEST);

        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchBar.setOpaque(false);
        searchBar.add(new JLabel("Trạng thái:"));
        searchBar.add(cbStatus);
        searchBar.add(new JLabel("Tìm kiếm:"));
        searchBar.add(tfSearch);
        JButton btnSearch = UiUtil.primaryButton("Tìm");
        btnSearch.addActionListener(e -> loadData(tfSearch.getText().trim()));
        searchBar.add(btnSearch);

        p.add(searchBar, BorderLayout.EAST);
        return p;
    }

    private JScrollPane buildTable() {
        UiUtil.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        return sp;
    }

    private JPanel buildToolbar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        p.setOpaque(false);
        p.add(btnUpdate);
        p.add(btnRefresh);
        return p;
    }

    // ---- role visibility ----

    private void applyRoleVisibility() {
        CurrentUser u = SecurityContext.get();
        boolean canUpdate = u != null && (u.isAdmin() || u.isAccountant());
        btnUpdate.setVisible(canUpdate);
    }

    // ---- events ----

    private void wireEvents() {
        btnUpdate.addActionListener(e -> onUpdateStatus());
        btnRefresh.addActionListener(e -> {
            tfSearch.setText("");
            cbStatus.setSelectedItem(null);
            loadData(null);
        });
        tfSearch.addActionListener(e -> loadData(tfSearch.getText().trim()));
        cbStatus.addActionListener(e -> loadData(tfSearch.getText().trim()));
    }

    private void onUpdateStatus() {
        int row = table.getSelectedRow();
        if (row < 0) {
            MessageBox.warn(this, "Vui lòng chọn một hóa đơn để cập nhật.");
            return;
        }
        Invoice selected = model.getRow(row);

        // Chỉ cho chọn các status hợp lệ theo trạng thái hiện tại
        InvoiceStatus[] options = switch (selected.getStatus()) {
            case PENDING -> new InvoiceStatus[] { InvoiceStatus.PAID, InvoiceStatus.CANCELED };
            case PAID -> new InvoiceStatus[] { InvoiceStatus.CANCELED };
            case CANCELED -> null; // không còn cập nhật được
        };

        if (options == null || options.length == 0) {
            MessageBox.warn(this, "Hóa đơn đã bị hủy, không thể cập nhật thêm.");
            return;
        }

        // Render tiếng Việt cho dialog chọn
        String[] labels = java.util.Arrays.stream(options)
                .map(s -> switch (s) {
                    case PENDING -> "Chờ thanh toán";
                    case PAID -> "Đã thanh toán";
                    case CANCELED -> "Hủy hóa đơn";
                })
                .toArray(String[]::new);

        String chosen = (String) JOptionPane.showInputDialog(
                this,
                "Chọn trạng thái mới cho hóa đơn #" + selected.getInvoiceID()
                        + "\n(Học viên: " + selected.getStudent().getFullName() + ")",
                "Cập nhật trạng thái hóa đơn",
                JOptionPane.PLAIN_MESSAGE,
                null,
                labels,
                labels[0]);

        if (chosen == null)
            return;

        // Map label → enum
        InvoiceStatus newStatus = options[java.util.Arrays.asList(labels).indexOf(chosen)];

        new SwingWorker<Invoice, Void>() {
            @Override
            protected Invoice doInBackground() {
                return service.updateStatus(selected.getInvoiceID(), newStatus);
            }

            @Override
            protected void done() {
                try {
                    get();
                    MessageBox.info(InvoicesPanel.this,
                            "Đã cập nhật trạng thái hóa đơn #" + selected.getInvoiceID()
                                    + " → " + newStatus.name());
                    loadData(null);
                } catch (Exception ex) {
                    handleException(ex);
                }
            }
        }.execute();
    }

    // ---- load data ----

    private void loadData(String keyword) {
        btnUpdate.setEnabled(false);
        InvoiceStatus selectedStatus = (InvoiceStatus) cbStatus.getSelectedItem();

        new SwingWorker<List<Invoice>, Void>() {
            @Override
            protected List<Invoice> doInBackground() {
                List<Invoice> all = service.findAll();

                // filter by status
                if (selectedStatus != null)
                    all = InvoiceStreamQueries.filterByStatus(all, selectedStatus);

                // filter by keyword
                if (keyword != null && !keyword.isBlank())
                    all = InvoiceStreamQueries.search(all, keyword);

                return all;
            }

            @Override
            protected void done() {
                try {
                    model.setData(get());
                } catch (Exception ex) {
                    handleException(ex);
                } finally {
                    CurrentUser u = SecurityContext.get();
                    boolean canUpdate = u != null && (u.isAdmin() || u.isAccountant());
                    btnUpdate.setEnabled(canUpdate);
                }
            }
        }.execute();
    }

    // ---- helpers ----

    private void handleException(Exception ex) {
        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
        if (cause instanceof AppException ae) {
            MessageBox.warn(this, ae.getUserMessage());
        } else {
            log.error("Error in InvoicesPanel", cause);
            MessageBox.error(this, "Lỗi hệ thống: " + cause.getMessage());
        }
    }
}
