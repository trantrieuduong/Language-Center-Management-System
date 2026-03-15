package com.ui.panel;

import com.exception.AppException;
import com.model.academic.Class;
import com.model.operation.Attendance;
import com.security.CurrentUser;
import com.security.SecurityContext;
import com.service.impl.AttendanceServiceImpl;
import com.service.impl.ClassServiceImpl;
import com.toedter.calendar.JDateChooser;
import com.ui.dialog.AttendanceDialog;
import com.ui.table.AttendanceTableModel;
import com.ui.util.MessageBox;
import com.ui.util.UiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class AttendancePanel extends JPanel {

    private static final Logger log = LoggerFactory.getLogger(AttendancePanel.class);

    private final AttendanceServiceImpl service = new AttendanceServiceImpl();
    private final AttendanceTableModel model = new AttendanceTableModel();
    private final JTable table = new JTable(model);
    private final JComboBox<Class> cbClass = new JComboBox<>();
    private final JDateChooser dcAttendanceDate = new JDateChooser();
    private final JButton btnFilter = UiUtil.primaryButton("Lọc");
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
        loadClassList();
        loadData();
    }

    // ---- builders ----

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setOpaque(false);
        p.add(UiUtil.sectionTitle("Quản lý Điểm danh"), BorderLayout.WEST);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterPanel.setOpaque(false);

        // 1. Lọc theo lớp
        filterPanel.add(new JLabel("Lớp:"));
        cbClass.setPreferredSize(new Dimension(180, 25));
        filterPanel.add(cbClass);

        // 2. Lọc theo ngày
        filterPanel.add(new JLabel("Ngày:"));
        dcAttendanceDate.setDateFormatString("dd/MM/yyyy");
        dcAttendanceDate.setPreferredSize(new Dimension(130, 25));
        dcAttendanceDate.setDate(new java.util.Date()); // Mặc định là hôm nay
        filterPanel.add(dcAttendanceDate);

        // 3. Nút lọc
        filterPanel.add(btnFilter);

        p.add(filterPanel, BorderLayout.EAST);
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
        btnRefresh.addActionListener(e -> {
            dcAttendanceDate.setDate(new java.util.Date());
            loadClassList();
            loadData();
        });
        btnFilter.addActionListener(e -> loadData());
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
            loadData();
        }
    }

    private void loadData() {
        btnEdit.setEnabled(false);

        // Lấy thông tin từ bộ lọc
        Class selectedClass = (Class) cbClass.getSelectedItem();
        Date selectedDate = dcAttendanceDate.getDate();

        Long classId = (selectedClass != null) ? selectedClass.getClassID() : null;
        LocalDate attendanceDate = (selectedDate != null)
                ? new java.sql.Date(selectedDate.getTime()).toLocalDate()
                : null;

        new SwingWorker<java.util.List<Attendance>, Void>() {
            final CurrentUser u = SecurityContext.get();
            @Override
            protected List<Attendance> doInBackground() {
                return service.search(classId, attendanceDate, u.relatedId(), u.role());
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

    private void loadClassList() {
        // Xóa toàn bộ item cũ để tránh trùng lặp khi nạp lại
        cbClass.removeAllItems();

        new ClassServiceImpl().findAll().forEach(cbClass::addItem);
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

