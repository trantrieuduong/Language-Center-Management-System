package com.ui.panel;

import com.exception.AppException;
import com.model.operation.Attendance;
import com.security.CurrentUser;
import com.security.SecurityContext;
import com.service.impl.AttendanceServiceImpl;
import com.ui.dialog.AttendanceDialog;
import com.ui.table.AttendanceTableModel;
import com.ui.util.MessageBox;
import com.ui.util.UiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AttendancePanel extends JPanel {

    private static final Logger log = LoggerFactory.getLogger(AttendancePanel.class);

    private final AttendanceServiceImpl service = new AttendanceServiceImpl();
    private final AttendanceTableModel model = new AttendanceTableModel();
    private final JTable table = new JTable(model);
    private final JTextField tfSearch = UiUtil.searchField("Tìm theo mã lớp...");
    private final JButton btnEdit = UiUtil.primaryButton("Sửa");
    private final JButton btnRefresh = new JButton("Làm mới");

    public AttendancePanel() {
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
        p.add(UiUtil.sectionTitle("Quản lý Điểm danh"), BorderLayout.WEST);

        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchBar.setOpaque(false);
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
        p.add(btnEdit);
        p.add(btnRefresh);
        return p;
    }

    // ---- role visibility ----

    private void applyRoleVisibility() {
        CurrentUser u = SecurityContext.get();
        boolean canWrite = u != null && (u.isAdmin() || u.isTeacher());
        btnEdit.setVisible(canWrite);
    }

    // ---- events ----

    private void wireEvents() {
        btnEdit.addActionListener(e -> onEdit());
        btnRefresh.addActionListener(e -> loadData(null));
        tfSearch.addActionListener(e -> loadData(tfSearch.getText().trim()));
    }

    private void onEdit() {
        int row = table.getSelectedRow();
        if (row < 0) {
            MessageBox.warn(this, "Vui lòng chọn một lịch sử điểm danh để sửa.");
            return;
        }
        Attendance selected = model.getRow(row);

        AttendanceDialog dlg = new AttendanceDialog(getParentFrame(), selected);
        dlg.setVisible(true);

        if (dlg.isSuccess()) {
            loadData(null);
        }
    }

    private void loadData(String keyword) {
        btnEdit.setEnabled(false);

        new SwingWorker<java.util.List<Attendance>, Void>() {
            @Override
            protected List<Attendance> doInBackground() {
                return (keyword == null || keyword.isBlank())
                        ? service.findAll()
                        : service.search(keyword);
            }

            @Override
            protected void done() {
                try {
                    model.setData(get());
                } catch (Exception ex) {
                    handleException(ex);
                } finally {
                    CurrentUser u = SecurityContext.get();
                    boolean canWrite = u != null && (u.isAdmin() || u.isTeacher());
                    btnEdit.setEnabled(canWrite);
                }
            }
        }.execute();
    }

    private void handleException(Exception ex) {
        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
        if (cause instanceof com.exception.ValidationException || cause instanceof com.exception.BusinessException) {
            MessageBox.warn(this, ((AppException) cause).getUserMessage());
        } else {
            log.error("Error in AttendancePanel", cause);
            MessageBox.error(this, "Lỗi hệ thống: " + cause.getMessage());
        }
    }

    private Frame getParentFrame() {
        return (Frame) SwingUtilities.getWindowAncestor(this);
    }
}

