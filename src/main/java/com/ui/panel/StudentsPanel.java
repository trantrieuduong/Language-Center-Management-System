package com.ui.panel;

import com.dto.StudentDTO;
import com.exception.AppException;

import com.model.user.Student;
import com.security.CurrentUser;
import com.security.SecurityContext;
import com.service.StudentService;
import com.service.impl.StudentServiceImpl;
import com.ui.dialog.StudentDialog;
import com.ui.table.StudentTableModel;
import com.ui.util.MessageBox;
import com.ui.util.UiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Students management panel – fully implements the CRUD + search pattern.
 * All DB operations use SwingWorker to keep the UI responsive.
 */
public class StudentsPanel extends JPanel {

    private static final Logger log = LoggerFactory.getLogger(StudentsPanel.class);

    private final StudentService service = new StudentServiceImpl();
    private final StudentTableModel model = new StudentTableModel();
    private final JTable table = new JTable(model);
    private final JTextField tfSearch = UiUtil.searchField("Tìm theo tên...");
    private final JButton btnAdd = UiUtil.primaryButton("Thêm");
    private final JButton btnEdit = UiUtil.primaryButton("Sửa");
    private final JButton btnDelete = UiUtil.dangerButton("Xóa");
    private final JButton btnRefresh = new JButton("Làm mới");

    public StudentsPanel() {
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
        p.add(UiUtil.sectionTitle("Quản lý Học viên"), BorderLayout.WEST);

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
        p.add(btnAdd);
        p.add(btnEdit);
        p.add(btnDelete);
        p.add(btnRefresh);
        return p;
    }

    // ---- role visibility ----

    private void applyRoleVisibility() {
        CurrentUser u = SecurityContext.get();
        boolean canWrite = u != null && (u.isAdmin() || u.isConsultant());
        btnAdd.setVisible(canWrite);
        btnEdit.setVisible(canWrite);
        btnDelete.setVisible(canWrite);
    }

    // ---- events ----

    private void wireEvents() {
        btnAdd.addActionListener(e -> onAdd());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());
        btnRefresh.addActionListener(e -> loadData(null));
        tfSearch.addActionListener(e -> loadData(tfSearch.getText().trim()));
    }

    private void onAdd() {
        StudentDialog dlg = new StudentDialog(getParentFrame(), null);
        dlg.setVisible(true);
        StudentDTO dto = dlg.getResult();
        if (dto == null)
            return;

        new SwingWorker<Student, Void>() {
            @Override
            protected Student doInBackground() {
                return service.save(dto);
            }

            @Override
            protected void done() {
                try {
                    get();
                    MessageBox.info(StudentsPanel.this, "Thêm học viên thành công.");
                    loadData(null);
                } catch (Exception ex) {
                    handleException(ex);
                }
            }
        }.execute();
    }

    private void onEdit() {
        int row = table.getSelectedRow();
        if (row < 0) {
            MessageBox.warn(this, "Vui lòng chọn một học viên để sửa.");
            return;
        }
        Student selected = model.getRow(row);

        StudentDialog dlg = new StudentDialog(getParentFrame(), selected);
        dlg.setVisible(true);
        StudentDTO dto = dlg.getResult();
        if (dto == null)
            return;

        new SwingWorker<Student, Void>() {
            @Override
            protected Student doInBackground() {
                return service.update(selected.getStudentID(), dto);
            }

            @Override
            protected void done() {
                try {
                    get();
                    MessageBox.info(StudentsPanel.this, "Cập nhật học viên thành công.");
                    loadData(null);
                } catch (Exception ex) {
                    handleException(ex);
                }
            }
        }.execute();
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            MessageBox.warn(this, "Vui lòng chọn một học viên để xóa.");
            return;
        }
        Student selected = model.getRow(row);

        if (!MessageBox.confirm(this, "Bạn có chắc muốn xóa học viên: " + selected.getFullName() + "?"))
            return;

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                service.softDelete(selected.getStudentID());
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    MessageBox.info(StudentsPanel.this, "Đã xóa học viên (soft delete).");
                    loadData(null);
                } catch (Exception ex) {
                    handleException(ex);
                }
            }
        }.execute();
    }

    private void loadData(String keyword) {
        btnAdd.setEnabled(false);
        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);

        new SwingWorker<List<Student>, Void>() {
            @Override
            protected List<Student> doInBackground() {
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
                    boolean canWrite = u != null && (u.isAdmin() || u.isConsultant());
                    btnAdd.setEnabled(canWrite);
                    btnEdit.setEnabled(canWrite);
                    btnDelete.setEnabled(canWrite);
                }
            }
        }.execute();
    }

    private void handleException(Exception ex) {
        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
        if (cause instanceof com.exception.ValidationException || cause instanceof com.exception.BusinessException) {
            MessageBox.warn(this, ((AppException) cause).getUserMessage());
        } else {
            log.error("Error in StudentsPanel", cause);
            MessageBox.error(this, "Lỗi hệ thống: " + cause.getMessage());
        }
    }

    private Frame getParentFrame() {
        return (Frame) SwingUtilities.getWindowAncestor(this);
    }
}
