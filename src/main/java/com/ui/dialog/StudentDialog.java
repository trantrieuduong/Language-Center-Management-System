package com.ui.dialog;

import com.dto.StudentDTO;
import com.model.user.Gender;
import com.model.user.Student;
import com.model.user.UserStatus;
import com.ui.util.UiUtil;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class StudentDialog extends JDialog {

    private final boolean isEdit;

    @Getter
    private StudentDTO result;

    // Student info fields
    private final JTextField tfName = new JTextField(25);
    private final JTextField tfDob = new JTextField(10);
    private final JComboBox<Gender> cbGender = new JComboBox<>(Gender.values());
    private final JTextField tfPhone = new JTextField(15);
    private final JTextField tfEmail = new JTextField(25);
    private final JTextField tfAddress = new JTextField(30);
    private final JComboBox<UserStatus> cbStatus = new JComboBox<>(UserStatus.values());

    // Account credentials (only shown on create)
    private final JTextField tfUsername = new JTextField(20);
    private final JPasswordField pfPassword = new JPasswordField(20);

    public StudentDialog(Frame parent, Student existing) {
        super(parent, existing == null ? "Thêm học viên" : "Sửa học viên", true);
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

        // --- Student info rows ---
        Object[][] infoRows = {
                { "Họ và tên *", tfName },
                { "Ngày sinh (yyyy-MM-dd)", tfDob },
                { "Giới tính", cbGender },
                { "Điện thoại", tfPhone },
                { "Email", tfEmail },
                { "Địa chỉ", tfAddress },
                { "Trạng thái", cbStatus },
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

        // --- Account section (create only) ---
        if (!isEdit) {
            // Separator with title
            c.gridx = 0;
            c.gridy = row;
            c.gridwidth = 2;
            c.weightx = 1;
            c.insets = new Insets(12, 5, 4, 5);
            JLabel sep = new JLabel("Tài khoản đăng nhập");
            sep.setFont(UiUtil.FONT_BOLD);
            sep.setForeground(UiUtil.COLOR_PRIMARY);
            p.add(sep, c);
            c.gridwidth = 1;
            c.insets = new Insets(4, 5, 4, 5);
            row++;

            c.gridx = 0;
            c.gridy = row;
            c.weightx = 0;
            p.add(new JLabel("Tên đăng nhập *"), c);
            c.gridx = 1;
            c.weightx = 1;
            p.add(tfUsername, c);
            row++;

            c.gridx = 0;
            c.gridy = row;
            c.weightx = 0;
            p.add(new JLabel("Mật khẩu *"), c);
            c.gridx = 1;
            c.weightx = 1;
            p.add(pfPassword, c);
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
            warn("Họ tên không được để trống.");
            return;
        }

        // Validate account fields on create
        if (!isEdit) {
            if (tfUsername.getText().trim().isEmpty()) {
                warn("Tên đăng nhập không được để trống.");
                return;
            }
            if (new String(pfPassword.getPassword()).trim().isEmpty()) {
                warn("Mật khẩu không được để trống.");
                return;
            }
        }

        StudentDTO dto = new StudentDTO();
        dto.setFullName(name);
        dto.setPhone(tfPhone.getText().trim());
        dto.setEmail(tfEmail.getText().trim());
        dto.setAddress(tfAddress.getText().trim());
        dto.setGender((Gender) cbGender.getSelectedItem());
        dto.setStatus((UserStatus) cbStatus.getSelectedItem());

        String dobText = tfDob.getText().trim();
        if (!dobText.isEmpty()) {
            try {
                dto.setDateOfBirth(LocalDate.parse(dobText));
            } catch (DateTimeParseException ex) {
                warn("Ngày sinh không hợp lệ. Dùng định dạng yyyy-MM-dd.");
                return;
            }
        }

        if (!isEdit) {
            dto.setUsername(tfUsername.getText().trim());
            dto.setPassword(new String(pfPassword.getPassword()));
        }

        result = dto;
        dispose();
    }

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
    }

    private void prefill(Student s) {
        tfName.setText(s.getFullName());
        if (s.getDateOfBirth() != null)
            tfDob.setText(s.getDateOfBirth().toString());
        if (s.getGender() != null)
            cbGender.setSelectedItem(s.getGender());
        if (s.getPhone() != null)
            tfPhone.setText(s.getPhone());
        if (s.getEmail() != null)
            tfEmail.setText(s.getEmail());
        if (s.getAddress() != null)
            tfAddress.setText(s.getAddress());
        if (s.getStatus() != null)
            cbStatus.setSelectedItem(s.getStatus());
    }
}
