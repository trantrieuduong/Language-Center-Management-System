package com.ui.dialog;

import com.dto.AttendanceDTO;
import com.model.operation.Attendance;
import com.model.operation.AttendanceStatus;
import com.service.impl.AttendanceServiceImpl;
import com.ui.util.MessageBox;
import com.ui.util.UiUtil;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

public class AttendanceDialog extends JDialog {
    @Getter
    private boolean isSuccess;
    private final AttendanceServiceImpl service = new AttendanceServiceImpl();

    private final Attendance existing;

    // Information Fields
    private final JComboBox<AttendanceStatus> cbStatus = new JComboBox<>(AttendanceStatus.values());

    public AttendanceDialog(Frame parent, Attendance existing) {
        super(parent, "Sửa điểm  danh", true);

        prefill(existing);

        setLayout(new BorderLayout(10, 10));
        add(buildForm(), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);

        pack();
        setResizable(false);
        setLocationRelativeTo(parent);

        this.existing = existing;
    }

    private JPanel buildForm() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(15, 20, 5, 20));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 5, 4, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        // --- Course info rows ---
        Object[][] infoRows = {
                {"Trạng thái *", cbStatus},
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
        JButton btnOk = UiUtil.primaryButton("Cập nhật");
        JButton btnCancel = new JButton("Hủy");
        btnOk.addActionListener(e -> onOk());
        btnCancel.addActionListener(e -> dispose());
        p.add(btnCancel);
        p.add(btnOk);
        return p;
    }

    private void onOk() {
        AttendanceDTO dto = new AttendanceDTO();
        //dto.setClassID(existing.getAClass().getClassID());

        dto.setStatus((AttendanceStatus) cbStatus.getSelectedItem());

        new SwingWorker<Attendance, Void>() {
            @Override
            protected Attendance doInBackground() throws Exception {
                dto.setAttendanceID(existing.getAttendanceID());
                return service.update(dto);
            }

            @Override
            protected void done() {
                try {
                    get(); // kiểm tra doInBackground có lỗi không
                    MessageBox.info(AttendanceDialog.this, "Cập nhật điểm danh thành công.");

                    isSuccess = true; // Đặt flag để bên panel biết mà reload data
                    dispose();

                } catch (Exception e) {
                    String msg = e.getCause().getMessage();
                    MessageBox.warn(AttendanceDialog.this, msg);
                }
            }
        }.execute();
    }

    public void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
    }

    private void prefill(Attendance a) {
        cbStatus.setSelectedItem(a.getStatus());
    }
}

