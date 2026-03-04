package com.ui.panel;

import com.dto.ClassDTO;
import com.dto.CourseDTO;
import com.exception.AppException;
import com.model.academic.Class;
import com.model.academic.Course;
import com.security.CurrentUser;
import com.security.SecurityContext;
import com.service.impl.ClassServiceImpl;
import com.ui.dialog.ClassDialog;
import com.ui.table.ClassTableModel;
import com.ui.util.MessageBox;
import com.ui.util.UiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ClassesPanel extends JPanel {

    private static final Logger log = LoggerFactory.getLogger(ClassesPanel.class);

    private final ClassServiceImpl service = new ClassServiceImpl();
    private final ClassTableModel model = new ClassTableModel();
    private final JTable table = new JTable(model);
    private final JTextField tfSearch = UiUtil.searchField("Tìm theo tên...");
    private final JButton btnAdd = UiUtil.primaryButton("Thêm");
    private final JButton btnEdit = UiUtil.primaryButton("Sửa");
    private final JButton btnDelete = UiUtil.dangerButton("Xóa");
    private final JButton btnRefresh = new JButton("Làm mới");

    public ClassesPanel() {
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
        p.add(UiUtil.sectionTitle("Quản lý Khóa học"), BorderLayout.WEST);

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
        ClassDialog dlg = new ClassDialog(getParentFrame(), null);
        dlg.setVisible(true);
        ClassDTO dto = dlg.getResult();
        if (dto == null)
            return;

        new SwingWorker<Class, Void>() {
            @Override
            protected Class doInBackground() {
                return service.save(dto);
            }

            @Override
            protected void done() {
                try {
                    get();
                    MessageBox.info(ClassesPanel.this, "Thêm lớp học thành công.");
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
            MessageBox.warn(this, "Vui lòng chọn một khóa học để sửa.");
            return;
        }
        Class selected = model.getRow(row);

        ClassDialog dlg = new ClassDialog(getParentFrame(), selected);
        dlg.setVisible(true);
        ClassDTO dto = dlg.getResult();
        if (dto == null)
            return;

        new SwingWorker<Class, Void>() {
            @Override
            protected Class doInBackground() {
                return service.update(selected.getClassID(), dto);
            }

            @Override
            protected void done() {
                try {
                    get();
                    MessageBox.info(ClassesPanel.this, "Cập nhật khóa học thành công.");
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
            MessageBox.warn(this, "Vui lòng chọn một lớp học để xóa.");
            return;
        }
        Class selected = model.getRow(row);

        if (!MessageBox.confirm(this, "Bạn có chắc muốn xóa lớp học: " + selected.getClassName() + "?"))
            return;

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                service.delete(selected.getClassID());
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    MessageBox.info(ClassesPanel.this, "Đã xóa lớp học.");
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

        new SwingWorker<java.util.List<Class>, Void>() {
            @Override
            protected List<Class> doInBackground() {
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
            log.error("Error in CoursesPanel", cause);
            MessageBox.error(this, "Lỗi hệ thống: " + cause.getMessage());
        }
    }

    private Frame getParentFrame() {
        return (Frame) SwingUtilities.getWindowAncestor(this);
    }
}
