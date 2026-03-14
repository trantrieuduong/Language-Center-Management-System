package com.ui.panel;

import com.exception.AppException;
import com.model.academic.Class;
import com.model.academic.Result;
import com.security.CurrentUser;
import com.security.SecurityContext;
import com.service.impl.ClassServiceImpl;
import com.service.impl.ResultServiceImpl;
import com.ui.dialog.ResultDialog;
import com.ui.table.ResultTableModel;
import com.ui.util.MessageBox;
import com.ui.util.UiUtil;
import com.utils.GenericExcelExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class ResultsPanel extends JPanel {

    private static final Logger log = LoggerFactory.getLogger(ResultsPanel.class);

    private final ResultServiceImpl service = new ResultServiceImpl();
    private final ResultTableModel model = new ResultTableModel();
    private final JTable table = new JTable(model);
    private final JComboBox<Class> cbClass = new JComboBox<>();
    private final JButton btnFilter = UiUtil.primaryButton("Lọc");
    private final JButton btnEdit = UiUtil.primaryButton("Sửa");
    private final JButton btnRefresh = new JButton("Làm mới");
    private final JButton btnExport = new JButton("Xuất Excel");

    public ResultsPanel() {
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
        p.add(UiUtil.sectionTitle("Quản lý Điểm"), BorderLayout.WEST);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        filterPanel.setOpaque(false);
        filterPanel.add(new JLabel("Lớp:"));
        cbClass.setPreferredSize(new Dimension(180, 25));
        filterPanel.add(cbClass);
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
        p.add(btnExport);
        return p;
    }

    // ---- role visibility ----
    private void applyRoleVisibility() {
        CurrentUser u = SecurityContext.get();
        boolean canWrite = u != null && (u.isAdmin() || u.isTeacher());
        btnEdit.setVisible(canWrite);
        btnExport.setVisible(canWrite);
    }

    // ---- events ----
    private void wireEvents() {
        btnEdit.addActionListener(e -> onEdit());
        btnRefresh.addActionListener(e -> loadData());
        btnFilter.addActionListener(e -> loadData());
        btnExport.addActionListener(e -> onExportExcel());
    }

    private void onEdit() {
        int row = table.getSelectedRow();
        if (row < 0) {
            MessageBox.warn(this, "Vui lòng chọn một lịch sử chấm điểm để sửa.");
            return;
        }
        Result selected = model.getRow(row);

        ResultDialog dlg = new ResultDialog(getParentFrame(), selected);
        dlg.setVisible(true);

        if (dlg.isSuccess()) {
            loadData();
        }
    }

    private void loadData() {
        btnEdit.setEnabled(false);

        Class selectedClass = (Class) cbClass.getSelectedItem();
        Long classId = (selectedClass != null) ? selectedClass.getClassID() : null;

        new SwingWorker<java.util.List<Result>, Void>() {
            final CurrentUser u = SecurityContext.get();

            @Override
            protected List<Result> doInBackground() {
                return service.search(classId, u.relatedId(), u.role());
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

    private void onExportExcel() {
        List<Result> currentData = model.getData();
        if (currentData.isEmpty()) {
            MessageBox.warn(this, "Không có dữ liệu để xuất!");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn nơi lưu file Excel");
        fileChooser.setSelectedFile(new java.io.File("BangDiem_" + LocalDate.now() + ".xlsx"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {//hằng số để kiểm tra xem người dùng đã nhấn nút Save/Open hay chưa
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            if (!path.endsWith(".xlsx")) path += ".xlsx";

            GenericExcelExporter<Result> exporter = new GenericExcelExporter<>();
            String[] headers = {"Mã học viên", "Tên học viên", "Điểm"};

            String[] fields = {"student.studentID", "student.fullName", "score"};

            try {
                exporter.export(currentData, path, "Báo cáo điểm lớp " + currentData.getFirst().getAClass().getClassName(), headers, fields);
                MessageBox.info(this, "Xuất Excel thành công!");
            } catch (IOException ex) {
                MessageBox.error(this, "Lỗi xuất file: " + ex.getMessage());
            }
        }
    }

    private void handleException(Exception ex) {
        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
        if (cause instanceof com.exception.ValidationException || cause instanceof com.exception.BusinessException) {
            MessageBox.warn(this, ((AppException) cause).getUserMessage());
        } else {
            log.error("Error in ResultPanel", cause);
            MessageBox.error(this, "Lỗi hệ thống: " + cause.getMessage());
        }
    }

    private Frame getParentFrame() {
        return (Frame) SwingUtilities.getWindowAncestor(this);
    }
}


