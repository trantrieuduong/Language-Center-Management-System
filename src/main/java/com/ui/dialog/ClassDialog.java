package com.ui.dialog;

import com.dto.ClassDTO;
import com.model.academic.Class;
import com.model.academic.ClassStatus;
import com.model.academic.Course;
import com.model.academic.Level;
import com.ui.util.UiUtil;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ClassDialog extends JDialog {
    private final boolean isEdit;

    @Getter
    private ClassDTO result;

    // Information Fields
    private final JTextField tfName = new JTextField(25);
    private final JTextField tfEndDate = new JTextField(15);
    private final JTextField tfMaxStudent = new JTextField(25);
    private final JTextField tfStartDate = new JTextField(25);
    private final JComboBox<ClassStatus> cbStatus = new JComboBox<>(ClassStatus.values());
    private final JTextField tfCourseID = new JTextField(30);
    private final JTextField tfRoomID = new JTextField(30);
    private final JTextField tfTeacherID = new JTextField(30);

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ClassDialog(Frame parent, Class existing) {
        super(parent, existing == null ? "Thêm lớp học" : "Sửa lớp học", true);
        isEdit = existing != null;

        if (isEdit)
            prefill(existing);

        setLayout(new BorderLayout(10, 10));
        add(buildForm(), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);

        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
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
                { "Trạng thái", cbStatus },
                { "Mã khóa học *", tfCourseID },
                { "Mã phòng học *", tfRoomID },
                { "Mã giáo viên *", tfTeacherID },
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
        JButton btnOk = UiUtil.primaryButton(isEdit ? "Cập nhật" : "Lưu");
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

        try {
            dto.setCourseID(Long.parseLong(tfCourseID.getText().trim()));
        } catch (Exception e) {
            warn("Mã khóa học không hợp lệ!");
            return;
        }

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

        try {
            dto.setStartDate(LocalDate.parse(tfStartDate.getText().trim(), formatter));
        } catch (DateTimeParseException e) {
            warn("Ngày bắt đầu không hợp lệ! Vui lòng nhập theo định dạng (dd/MM/yyyy)!");
            return;
        }

        try {
            dto.setEndDate(LocalDate.parse(tfEndDate.getText().trim(), formatter));
        } catch (DateTimeParseException e) {
            warn("Ngày kết thúc không hợp lệ! Vui lòng nhập theo định dạng (dd/MM/yyyy)!");
            return;
        }

        result = dto;
        dispose();
    }

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
    }

    private void prefill(Class c) {
        tfName.setText(c.getClassName());
        if (c.getMaxStudent() != null)
            tfMaxStudent.setText(String.valueOf(c.getMaxStudent()));
        if (c.getCourse() != null)
            tfCourseID.setText(String.valueOf(c.getCourse().getCourseID()));
        if (c.getTeacher() != null)
            tfTeacherID.setText(String.valueOf(c.getTeacher().getTeacherID()));
        if (c.getRoom() != null)
            tfMaxStudent.setText(String.valueOf(c.getRoom().getRoomID()));
        if (c.getStatus() != null)
            cbStatus.setSelectedItem(c.getStatus());
        if (c.getStartDate() != null)
            tfStartDate.setText(c.getStartDate().format(formatter));
        if (c.getEndDate() != null)
            tfEndDate.setText(c.getEndDate().format(formatter));
    }
}
