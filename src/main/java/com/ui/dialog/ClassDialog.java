package com.ui.dialog;

import com.model.operation.Period;
import com.model.operation.Schedule;
import com.service.impl.ScheduleServiceImpl;
import com.toedter.calendar.JDateChooser;

import com.dto.ClassDTO;
import com.model.academic.Class;
import com.model.academic.ClassStatus;
import com.model.academic.Course;
import com.service.impl.ClassServiceImpl;
import com.service.impl.CourseServiceImpl;

import java.time.LocalDate;

import com.ui.util.MessageBox;
import com.ui.util.UiUtil;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.util.*;
import java.util.List;

public class ClassDialog extends JDialog {
    private final Class existing;
    @Getter
    private boolean isSuccess;
    private final ClassServiceImpl service = new ClassServiceImpl();
    private final CourseServiceImpl courseService = new CourseServiceImpl();
    private final ScheduleServiceImpl scheduleService = new ScheduleServiceImpl();

    // Information Fields
    private final JComboBox<Course> cbCourse = new JComboBox<>();
    private final JTextField tfName = new JTextField(30);
    private final JTextField tfMaxStudent = new JTextField(25);
    private final JComboBox<ClassStatus> cbStatus = new JComboBox<>(ClassStatus.values());
    private final JTextField tfRoomID = new JTextField(30);
    private final JTextField tfTeacherID = new JTextField(30);
    private final List<JCheckBox> weekdaysCheckBoxes = new ArrayList<>();
    private final ButtonGroup periodGroup = new ButtonGroup();
    private final List<JRadioButton> periodRadioButtons = new ArrayList<>();
    private JDateChooser dtcStartDate;

    public ClassDialog(Frame parent, Class existing) {
        super(parent, existing == null ? "Thêm lớp học" : "Sửa lớp học", true);
        this.existing = existing;

        loadCourseData();
        updateClassName();

        setLayout(new BorderLayout(10, 10));
        add(buildForm(), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);

        if (existing != null)
            prefill(existing);

        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    private void loadCourseData() {
        List<Course> courses = courseService.findAll();
        for (Course course : courses) {
            cbCourse.addItem(course);
        }
    }

    private JPanel buildForm() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(15, 20, 5, 20));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 5, 4, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        dtcStartDate = new JDateChooser();
        dtcStartDate.setDateFormatString("dd/MM/yyyy");
        // Vô hiệu hóa khả năng chỉnh sửa của ô Text, chỉ cho phép chọn từ Button lịch
        ((JTextField) dtcStartDate.getDateEditor().getUiComponent()).setEditable(false);

        // --- Course info rows ---
        Object[][] infoRows = {
                {"Khóa học *", cbCourse},
                {"Tên lớp học *", tfName},
                {"Học viên tối đa *", tfMaxStudent},
                {"Trạng thái *", cbStatus},
                {"Mã giáo viên *", tfTeacherID},
                {"Mã phòng học *", tfRoomID},
                {"Ngày bắt đầu *", dtcStartDate},
        };

        cbCourse.addActionListener(e -> updateClassName());

        dtcStartDate.addPropertyChangeListener("date", new java.beans.PropertyChangeListener() {
            @Override
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                if ("date".equals(evt.getPropertyName())) {
                    updateCheckboxFromDate();
                }
            }
        });

        int row = 0;
        for (Object[] r : infoRows) {
            c.gridx = 0;
            c.gridy = row;
            c.weightx = 0;
            p.add(new JLabel((String) r[0]), c);
            c.gridx = 1;
            c.weightx = 1;
            p.add((Component) r[1], c);
            row++;
        }

        // PHẦN CHỌN THỨ
        // Tạo Label cho hàng mới
        c.gridx = 0;
        c.gridy = row;
        c.weightx = 0;
        p.add(new JLabel("Lịch học *"), c);

        // Tạo Panel chứa các Checkbox
        JPanel panelCheckBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        String[][] daysData = {
                {"T2", "MONDAY"}, {"T3", "TUESDAY"}, {"T4", "WEDNESDAY"},
                {"T5", "THURSDAY"}, {"T6", "FRIDAY"}, {"T7", "SATURDAY"}, {"CN", "SUNDAY"}
        };

        for (String[] day : daysData) {
            JCheckBox cb = new JCheckBox(day[0]);
            cb.setActionCommand(day[1]);
            cb.setEnabled(false); // MẶC ĐỊNH KHÓA KHI CHƯA CÓ NGÀY
            weekdaysCheckBoxes.add(cb);
            panelCheckBox.add(cb);
        }

        c.gridx = 1;
        c.weightx = 1;
        p.add(panelCheckBox, c);

        row++;

        // PHẦN CHỌN CA HỌC (Periods)
        c.gridx = 0;
        c.gridy = row;
        p.add(new JLabel("Ca học *"), c);

        JPanel panelPeriods = new JPanel(new GridLayout(0, 3, 5, 2));
        for (Period period : Period.values()) {
            JRadioButton rb = new JRadioButton(period.toString());
            rb.setActionCommand(period.name()); // Lưu tên Enum (ví dụ: PERIOD_1)
            periodGroup.add(rb);
            periodRadioButtons.add(rb);
            panelPeriods.add(rb);
        }
        c.gridx = 1;
        p.add(panelPeriods, c);
        return p;
    }

    private JPanel buildButtons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnOk = UiUtil.primaryButton(existing != null ? "Cập nhật" : "Lưu");
        JButton btnCancel = new JButton("Hủy");
        btnOk.addActionListener(e -> onOk());
        btnCancel.addActionListener(e -> dispose());
        p.add(btnCancel);
        p.add(btnOk);
        return p;
    }

    private void onOk() {
        String name = tfName.getText().trim();
        if (name.isEmpty()) {
            warn("Tên lớp học không được để trống!");
            return;
        }

        ClassDTO dto = new ClassDTO();
        dto.setClassName(name);
        try {
            dto.setMaxStudent(Integer.parseInt(tfMaxStudent.getText().trim()));
        } catch (Exception e) {
            warn("Số học viên tối đa không hợp lệ!");
            return;
        }

        dto.setStatus((ClassStatus) cbStatus.getSelectedItem());

        dto.setCourseID(((Course) cbCourse.getSelectedItem()).getCourseID());

        try {
            dto.setRoomID(Long.parseLong(tfRoomID.getText().trim()));
        } catch (Exception e) {
            warn("Mã phòng học không hợp lệ!");
            return;
        }

        try {
            dto.setTeacherID(Long.parseLong(tfTeacherID.getText().trim()));
        } catch (Exception e) {
            warn("Mã giáo viên không hợp lệ!");
            return;
        }

        java.util.Date dateFromChooser = dtcStartDate.getDate();
        if (dateFromChooser == null) {
            warn("Vui lòng chọn ngày bắt đầu!");
            return;
        }

        dto.setDaysOfWeek(getSelectedDaysString(weekdaysCheckBoxes));

        ButtonModel selectedModel = periodGroup.getSelection();
        if (selectedModel == null) {
            warn("Vui lòng chọn một ca học!");
            return;
        }

        LocalDate startDate = changeDateToLocalDate(dateFromChooser);
        Period selectedPeriod = Period.valueOf(selectedModel.getActionCommand());
        if (existing == null
                && (startDate.isBefore(LocalDate.now()) || (startDate.isEqual(LocalDate.now()) && selectedPeriod.getStartTime().isBefore(LocalTime.now())))) {
            warn("Thời gian bắt đầu tại ngày hoặc ca học không hợp lệ (bắc buộc phải từ hiện tại trở đi)!");
            return;
        }
        dto.setStartDate(startDate);
        dto.setStartTime(selectedPeriod.getStartTime());
        dto.setEndTime(selectedPeriod.getEndTime());

        new SwingWorker<Class, Void>() {
            @Override
            protected Class doInBackground() throws Exception {
                if (existing != null) {
                    dto.setClassID(existing.getClassID());
                    return service.update(dto.getClassID(), dto);
                }
                return service.save(dto);
            }

            @Override
            protected void done() {
                try {
                    get(); // kiểm tra doInBackground có lỗi không
                    if (existing != null)
                        MessageBox.info(ClassDialog.this, "Cập nhật lớp học thành công.");
                    else
                        MessageBox.info(ClassDialog.this, "Thêm lớp học thành công.");

                    isSuccess = true; // Đặt flag để bên panel biết mà reload data
                    dispose();

                } catch (Exception e) {
                    String msg = e.getCause().getMessage();
                    MessageBox.warn(ClassDialog.this, msg);
                }
            }
        }.execute();
    }

    public void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
    }

    private void prefill(Class c) {
        tfName.setText(c.getClassName());
        if (c.getMaxStudent() != null)
            tfMaxStudent.setText(String.valueOf(c.getMaxStudent()));
        if (c.getCourse() != null)
            cbCourse.setSelectedItem(c.getCourse());
        if (c.getTeacher() != null)
            tfTeacherID.setText(String.valueOf(c.getTeacher().getTeacherID()));
        if (c.getRoom() != null)
            tfRoomID.setText(String.valueOf(c.getRoom().getRoomID()));
        if (c.getStatus() != null)
            cbStatus.setSelectedItem(c.getStatus());
        if (c.getStartDate() != null) {
            // Chuyển từ LocalDate sang Date
            java.util.Date date = java.util.Date.from(c.getStartDate()
                    .atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());

            // B1: Set ngày (Lúc này PropertyChangeListener sẽ chạy updateCheckboxFromDate)
            dtcStartDate.setDate(date);

            // B2: Tick lại các ngày học bổ sung từ DB (vì B1 đã xóa sạch tick)
            if (c.getDaysOfWeek() != null) {
                String[] savedDays = c.getDaysOfWeek().split(", ");
                for (String day : savedDays) {
                    for (JCheckBox cb : weekdaysCheckBoxes) {
                        if (cb.getActionCommand().equals(day)) {
                            cb.setSelected(true);
                        }
                    }
                }
            }
        }

        List<Schedule> schedules = scheduleService.searchByClassID(c.getClassID().toString());
        if (!schedules.isEmpty()) {
            LocalTime classStart = schedules.getFirst().getStartTime();
            for (JRadioButton rb : periodRadioButtons) {
                Period p = Period.valueOf(rb.getActionCommand());
                if (p.getStartTime().equals(classStart)) {
                    rb.setSelected(true);
                    break;
                }
            }
        }
    }

    // Hàm để duyệt và nối chuỗi
    private String getSelectedDaysString(List<JCheckBox> checkBoxes) {
        StringJoiner joiner = new StringJoiner(", ");
        for (JCheckBox cb : checkBoxes) {
            if (cb.isSelected()) {
                joiner.add(cb.getActionCommand());
            }
        }
        return joiner.toString();
    }

    private LocalDate changeDateToLocalDate(Date date) {
        // Chuyển từ java.util.Date sang java.time.LocalDate
        return date.toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate();
    }

    private void updateCheckboxFromDate() {
        java.util.Date selectedDate = dtcStartDate.getDate();

        if (selectedDate == null) {
            for (JCheckBox cb : weekdaysCheckBoxes) {
                cb.setSelected(false);
                cb.setEnabled(false);
            }
            return;
        }

        LocalDate localDate = changeDateToLocalDate(selectedDate);
        String dayOfWeek = localDate.getDayOfWeek().name(); // MONDAY, TUESDAY...

        for (JCheckBox cb : weekdaysCheckBoxes) {
            // Xóa lựa chọn cũ mỗi khi đổi ngày
            cb.setSelected(false);

            // Mở khóa cho phép chọn
            cb.setEnabled(true);

            // Nếu trùng với thứ của ngày bắt đầu
            if (cb.getActionCommand().equalsIgnoreCase(dayOfWeek)) {
                cb.setSelected(true); // Tự động chọn thứ bắt đầu
                cb.setEnabled(false); // Khóa lại không cho bỏ chọn
            }
        }

        for (JRadioButton rb : periodRadioButtons) {
            rb.setEnabled(true);
        }
    }

    private void updateClassName() {
        if (existing != null) return; // không auto đổi khi đang edit
        Course selected = (Course) cbCourse.getSelectedItem();
        if (selected != null)
            tfName.setText(service.generateClassName(selected));
    }
}
