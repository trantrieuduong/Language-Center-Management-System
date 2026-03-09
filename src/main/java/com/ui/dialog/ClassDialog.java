package com.ui.dialog;

import com.dto.ClassDTO;
import com.model.academic.Class;
import com.model.academic.ClassStatus;
import com.model.academic.Course;
import com.service.impl.ClassServiceImpl;
import com.service.impl.CourseServiceImpl;
import com.ui.util.JTextFieldPlaceholder;
import com.ui.util.MessageBox;
import com.ui.util.UiUtil;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ClassDialog extends JDialog {
    private final Class existing;
    @Getter
    private boolean isSuccess;
    private final ClassServiceImpl service = new ClassServiceImpl();
    private final CourseServiceImpl courseService = new CourseServiceImpl();

    // Information Fields
    private final JTextField tfName = new JTextField(25);
    private final JTextField tfMaxStudent = new JTextField(25);
    private final JTextFieldPlaceholder tfStartDate = new JTextFieldPlaceholder("dd/MM/yyyy");
    private final JTextFieldPlaceholder tfEndDate = new JTextFieldPlaceholder("dd/MM/yyyy");
    private final JComboBox<ClassStatus> cbStatus = new JComboBox<>(ClassStatus.values());
    private final JComboBox<Course> cbCourse = new JComboBox<>();
    private final JTextField tfRoomID = new JTextField(30);
    private final JTextField tfTeacherID = new JTextField(30);

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ClassDialog(Frame parent, Class existing) {
        super(parent, existing == null ? "Thêm lớp học" : "Sửa lớp học", true);
        this.existing = existing;

        loadCourseData();

        if (existing != null)
            prefill(existing);

        setLayout(new BorderLayout(10, 10));
        add(buildForm(), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);

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

        // --- Course info rows ---
        Object[][] infoRows = {
                { "Tên lớp học *", tfName },
                { "Học viên tối đa *", tfMaxStudent },
                { "Trạng thái *", cbStatus },
                { "Khóa học *", cbCourse },
                { "Mã giáo viên *", tfTeacherID },
                { "Mã phòng học *", tfRoomID },
                { "Ngày bắt đầu *", tfStartDate },
                { "Ngày kết thúc *", tfEndDate },
        };

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

        LocalDate startDate;
        LocalDate endDate;

        try {
            startDate = LocalDate.parse(tfStartDate.getText().trim(), formatter);
        } catch (DateTimeParseException e) {
            warn("Ngày bắt đầu không hợp lệ! (Vui lòng nhập theo định dạng (dd/MM/yyyy) và phải là ngày, tháng, năm hợp lệ!)");
            return;
        }

        if(startDate.isBefore(LocalDate.now())){
            warn("Ngày bắt đầu phải sau thời điểm hiện tại!");
            return;
        }

        try {
            endDate = LocalDate.parse(tfEndDate.getText().trim(), formatter);
        } catch (DateTimeParseException e) {
            warn("Ngày kết thúc không hợp lệ! (Vui lòng nhập theo định dạng (dd/MM/yyyy) và phải là ngày, tháng, năm hợp lệ!)");
            return;
        }

        if(endDate.isBefore(LocalDate.now())){
            warn("Ngày kết thúc phải sau thời điểm hiện tại!");
            return;
        }

        if (endDate.isBefore(startDate)) {
            warn("Ngày kết thúc phải sau ngày bắt đầu!");
            return;
        }

        dto.setStartDate(startDate);
        dto.setEndDate(endDate);

        new SwingWorker<Class, Void>() {
            @Override
            protected Class doInBackground() throws Exception {
                if(existing != null) {
                    dto.setClassID(existing.getClassID());
                    return service.update(dto.getClassID(), dto);
                }
                return service.save(dto);
            }

            @Override
            protected void done() {
                try {
                    get(); // kiểm tra doInBackground có lỗi không
                    if(existing != null)
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
        if (c.getStartDate() != null)
            tfStartDate.setText(c.getStartDate().format(formatter));
        if (c.getEndDate() != null)
            tfEndDate.setText(c.getEndDate().format(formatter));
    }
}
