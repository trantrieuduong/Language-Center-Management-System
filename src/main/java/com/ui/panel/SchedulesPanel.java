package com.ui.panel;

import com.exception.AppException;
import com.model.academic.EnrollmentStatus;
import com.model.operation.Schedule;
import com.security.CurrentUser;
import com.security.SecurityContext;
import com.service.impl.ScheduleServiceImpl;
import com.toedter.calendar.JDateChooser;
import com.ui.dialog.ScheduleDialog;
import com.ui.util.TimetableCellRenderer;
import com.ui.table.ScheduleTableModel;
import com.ui.util.MessageBox;
import com.ui.util.UiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class SchedulesPanel extends JPanel {
    private static final Logger log = LoggerFactory.getLogger(SchedulesPanel.class);

    private final ScheduleServiceImpl service = new ScheduleServiceImpl();
    private final ScheduleTableModel model = new ScheduleTableModel();

    private final JTable table = new JTable(model);
    private final JButton btnEdit = UiUtil.primaryButton("Sửa");
    private final JButton btnRefresh = new JButton("Làm mới");
    private final JButton btnPrev = new JButton("< Tuần trước");
    private final JButton btnToday = new JButton("Hôm nay");
    private final JDateChooser dcFilter = new JDateChooser();
    private final JButton btnNext = new JButton("Tuần sau >");
    private final JTextField tfClassSearch = UiUtil.searchField("Tên lớp...");

    public SchedulesPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UiUtil.COLOR_BG);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        add(UiUtil.sectionTitle("Quản lý Lịch học"), BorderLayout.NORTH);
        add(buildTimetableTab(), BorderLayout.CENTER);

        wireEvents();
        applyRoleVisibility();

        loadData(null);
    }

    private JPanel buildTimetableTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        // Nav ở trên đầu
        panel.add(buildTimetableNav(), BorderLayout.NORTH);

        // Bảng ở giữa
        panel.add(buildTable(), BorderLayout.CENTER);

        // Toolbar (Nút Sửa/Refresh) ở dưới cùng
        panel.add(buildToolbar(), BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buildTimetableNav() {
        JPanel mainNav = new JPanel(new BorderLayout(10, 0));
        mainNav.setOpaque(false);

        // Cụm điều hướng
        JPanel pnlWeekNav = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pnlWeekNav.setOpaque(false);
        dcFilter.setDateFormatString("dd/MM/yyyy");
        dcFilter.setPreferredSize(new Dimension(120, 25));
        pnlWeekNav.add(btnPrev);
        pnlWeekNav.add(btnToday);
        pnlWeekNav.add(dcFilter);
        pnlWeekNav.add(btnNext);

        // Cụm lọc lớp
        JPanel pnlClassFilter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        pnlClassFilter.setOpaque(false);
        pnlClassFilter.add(new JLabel("Lọc lớp:"));
        tfClassSearch.setPreferredSize(new Dimension(150, 25));
        pnlClassFilter.add(tfClassSearch);

        JButton btnFilter = UiUtil.primaryButton("Lọc");
        btnFilter.addActionListener(e -> loadData(tfClassSearch.getText().trim()));
        pnlClassFilter.add(btnFilter);

        mainNav.add(pnlWeekNav, BorderLayout.WEST);
        mainNav.add(pnlClassFilter, BorderLayout.EAST);

        return mainNav;
    }

    private JScrollPane buildTable() {
        UiUtil.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setDefaultRenderer(Object.class, new TimetableCellRenderer());
        // Tăng chiều cao dòng để hiển thị được HTML 2 dòng
        table.setRowHeight(50);
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
        boolean canWrite = u != null && (u.isAdmin() || u.isConsultant());
        btnEdit.setVisible(canWrite);
    }

    // ---- events ----
    private void wireEvents() {
        btnEdit.addActionListener(e -> onEdit());
        btnRefresh.addActionListener(e -> loadData(tfClassSearch.getText().trim()));

        // Sự kiện nút Lọc và Enter trên ô tìm kiếm
        tfClassSearch.addActionListener(e -> loadData(tfClassSearch.getText().trim()));

        // Điều hướng tuần
        btnPrev.addActionListener(e -> {
            model.setWeek(model.getMondayOfSelectedWeek().minusWeeks(1));
            loadData(tfClassSearch.getText().trim());
        });

        btnNext.addActionListener(e -> {
            model.setWeek(model.getMondayOfSelectedWeek().plusWeeks(1));
            loadData(tfClassSearch.getText().trim());
        });

        btnToday.addActionListener(e -> {
            model.setWeek(LocalDate.now());
            dcFilter.setDate(null); // Reset bộ lọc ngày về trống
            loadData(tfClassSearch.getText().trim());
        });

        // Sự kiện chọn ngày trên JDateChooser
        dcFilter.addPropertyChangeListener("date", evt -> {
            if ("date".equals(evt.getPropertyName()) && evt.getNewValue() != null) {
                LocalDate selected = new java.sql.Date(((java.util.Date) evt.getNewValue()).getTime()).toLocalDate();
                model.setWeek(selected);
                loadData(tfClassSearch.getText().trim());
            }
        });
    }

    private void onEdit() {
        int row = table.getSelectedRow();
        int col = table.getSelectedColumn();
        if (row < 0 || col < 0) {//
            MessageBox.warn(this, "Vui lòng chọn một ô lịch học trên bảng để sửa.");
            return;
        }

        List<Schedule> scheduleList = model.getSchedulesAt(row, col);

        if (scheduleList.isEmpty()) {
            MessageBox.warn(this, "Ô bạn chọn không có lịch học.");
            return;
        }

        Schedule selected;
        if (scheduleList.size() == 1) {
            selected = scheduleList.getFirst();
        } else {
            // Cho user chọn khi có nhiều lịch trong ô
            String[] options = scheduleList.stream()
                    .map(s -> s.getAClass().getClassName() + " - " + s.getRoom().getRoomName())
                    .toArray(String[]::new);
            int choice = JOptionPane.showOptionDialog(this, "Chọn lịch để sửa:",
                    "Nhiều lịch trong ô", JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (choice < 0) return;
            selected = scheduleList.get(choice);
        }

        ScheduleDialog dlg = new ScheduleDialog(getParentFrame(), selected);
        dlg.setVisible(true);

        if (dlg.isSuccess()) {
            loadData(tfClassSearch.getText().trim());
        }
    }

    private void loadData(String keyword) {
        CurrentUser u = SecurityContext.get();
        // Vô hiệu hóa nút bấm để tránh người dùng click loạn khi đang load
        btnEdit.setEnabled(false);
        String finalKeyword = (keyword == null) ? "" : keyword.trim();

        new SwingWorker<List<Schedule>, Void>() {
            @Override
            protected List<Schedule> doInBackground() {
                // Lấy khoảng ngày của tuần đang chọn trên UI
                LocalDate start = model.getMondayOfSelectedWeek();
                LocalDate end = start.plusDays(6);

                if(u.isStudent())
                    return service.findSchedulesByRangeAndClassName(start, end, finalKeyword)
                        .stream().filter(s -> s.getAClass()
                                .getEnrollments().stream().
                                anyMatch(e -> e.getStudent().getStudentID().equals(u.relatedId())
                                        && e.getStatus() == EnrollmentStatus.ACCEPT)).toList();
                else return service.findSchedulesByRangeAndClassName(start, end, finalKeyword);
            }

            @Override
            protected void done() {
                try {
                    // 2. Cập nhật dữ liệu vào Model
                    model.setData(get());

                    // 3. Ép lại Style sau khi cấu trúc bảng ổn định
                    SwingUtilities.invokeLater(() -> applyTableStyles());

                } catch (Exception ex) {
                    handleException(ex);
                } finally {
                    CurrentUser u = SecurityContext.get();
                    boolean canWrite = u != null && (u.isAdmin() || u.isConsultant());
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
            log.error("Error in SchedulePanel", cause);
            MessageBox.error(this, "Lỗi hệ thống: " + cause.getMessage());
        }
    }

    private Frame getParentFrame() {
        return (Frame) SwingUtilities.getWindowAncestor(this);
    }

    private void applyTableStyles() {
        // Luôn set Renderer cho Header (để giữ màu tiêu đề)
        table.getTableHeader().setPreferredSize(new Dimension(0, 45));

        // Ép Renderer cho từng cột (quan trọng nhất để giữ màu xanh)
        TimetableCellRenderer renderer = new TimetableCellRenderer();
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        // Độ rộng cột khung giờ
        if (table.getColumnCount() > 0) {
            table.getColumnModel().getColumn(0).setPreferredWidth(100);
            table.getColumnModel().getColumn(0).setMinWidth(100);
            table.getColumnModel().getColumn(0).setMaxWidth(120);
        }


        // TỰ ĐỘNG TÍNH CHIỀU CAO DÒNG
        for (int row = 0; row < table.getRowCount(); row++) {
            int maxPreferredHeight = 60; // Chiều cao tối thiểu

            for (int col = 1; col < table.getColumnCount(); col++) {
                Object value = table.getValueAt(row, col);
                if (value != null && !value.toString().isEmpty()) {
                    // Sử dụng Renderer để tính chiều cao ưu thích của ô đó
                    Component comp = table.prepareRenderer(table.getCellRenderer(row, col), row, col);

                    // Ép chiều rộng hiện tại của cột để component tính toán việc xuống dòng
                    comp.setSize(table.getColumnModel().getColumn(col).getWidth(), 1000);

                    int preferredHeight = comp.getPreferredSize().height + 10; // +10 cho padding
                    if (preferredHeight > maxPreferredHeight) {
                        maxPreferredHeight = preferredHeight;
                    }
                }
            }
            table.setRowHeight(row, maxPreferredHeight);
        }
    }
}

