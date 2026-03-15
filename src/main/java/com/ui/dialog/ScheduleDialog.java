package com.ui.dialog;

import com.dto.ScheduleDTO;
import com.model.operation.Period;
import com.model.operation.Schedule;
import com.service.impl.ScheduleServiceImpl;
import com.ui.util.JTextFieldPlaceholder;
import com.ui.util.MessageBox;
import com.ui.util.UiUtil;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class ScheduleDialog extends JDialog {
    private final Schedule existing;
    @Getter
    private boolean isSuccess;
    private final ScheduleServiceImpl service = new ScheduleServiceImpl();

    // Information Fields
    private final JTextField tfClassID = new JTextField(30);
    private final JTextField tfClassName = new JTextField(30);
    private final JTextField tfRoomID = new JTextField(30);
    private final JTextFieldPlaceholder tfDate = new JTextFieldPlaceholder("dd/MM/yyyy");
    private final ButtonGroup periodGroup = new ButtonGroup();
    private final List<JRadioButton> periodRadioButtons = new ArrayList<>();

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ScheduleDialog(Frame parent, Schedule existing) {
        super(parent, "Sửa lịch học", true);
        this.existing = existing;

        setLayout(new BorderLayout(10, 10));
        add(buildForm(), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);

        if (existing != null)
            prefill(existing);

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
                {"Mã lớp học", tfClassID},
                {"Tên lớp học", tfClassName},
                {"Mã phòng học *", tfRoomID},
                {"Ngày học *", tfDate},
        };

        tfClassID.setEditable(false);
        tfClassName.setEditable(false);

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
        JButton btnOk = UiUtil.primaryButton("Cập nhật");
        JButton btnCancel = new JButton("Hủy");
        btnOk.addActionListener(e -> onOk());
        btnCancel.addActionListener(e -> dispose());
        p.add(btnCancel);
        p.add(btnOk);
        return p;
    }

    private void onOk() {
        String classID = tfClassID.getText().trim();

        String roomID = tfRoomID.getText().trim();
        if (roomID.isEmpty()) {
            warn("Mã phòng học không được để trống!");
            return;
        }

        String dateStr = tfDate.getText().trim();
        if (dateStr.isEmpty()) {
            warn("Ngày học không được để trống!");
            return;
        }

        ScheduleDTO dto = new ScheduleDTO();

        try {
            dto.setClassID(Long.parseLong(classID));
        } catch (Exception e) {
            warn("Mã lớp học không hợp lệ!");
            return;
        }

        try {
            dto.setRoomID(Long.parseLong(roomID));
        } catch (Exception e) {
            warn("Mã phòng học không hợp lệ!");
            return;
        }

        LocalDate date;
        try {
            date = LocalDate.parse(dateStr, dateFormatter);
            dto.setDate(date);
        } catch (DateTimeParseException e) {
            warn("Ngày học không hợp lệ! (Vui lòng nhập theo định dạng (dd/MM/yyyy) và phải là ngày, tháng, năm hợp lệ!)");
            return;
        }

        ButtonModel selectedModel = periodGroup.getSelection();
        if (selectedModel == null) {
            warn("Vui lòng chọn một ca học!");
            return;
        }

        Period selectedPeriod = Period.valueOf(selectedModel.getActionCommand());
        if (date.isBefore(LocalDate.now())
                || (date.isEqual(LocalDate.now()) && selectedPeriod.getStartTime().isBefore(LocalTime.now()))) {
            warn("Thời gian học hoặc ca học không hợp lệ (bắc buộc phải từ hiện tại trở đi)!");
            return;
        }
        dto.setDate(date);
        dto.setStartTime(selectedPeriod.getStartTime());
        dto.setEndTime(selectedPeriod.getEndTime());

        new SwingWorker<Schedule, Void>() {
            @Override
            protected Schedule doInBackground() {
                dto.setScheduleID(existing.getScheduleID());
                return service.update(dto);
            }

            @Override
            protected void done() {
                try {
                    get(); // kiểm tra doInBackground có lỗi không
                    MessageBox.info(ScheduleDialog.this, "Cập nhật lịch học thành công.");

                    isSuccess = true; // Đặt flag để bên panel biết mà reload data
                    dispose();

                } catch (Exception e) {
                    String msg = e.getCause().getMessage();
                    MessageBox.warn(ScheduleDialog.this, msg);
                }
            }
        }.execute();
    }

    public void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
    }

    private void prefill(Schedule s) {
        if (s.getAClass() != null) {
            tfClassID.setText(s.getAClass().getClassID().toString());
            tfClassName.setText(s.getAClass().getClassName());
        }
        if (s.getRoom() != null)
            tfRoomID.setText(String.valueOf(s.getRoom().getRoomID()));
        if (s.getDate() != null)
            tfDate.setText(s.getDate().format(dateFormatter));
        for (JRadioButton rb : periodRadioButtons) {
            Period p = Period.valueOf(rb.getActionCommand());
            if (p.getStartTime().equals(s.getStartTime())) {
                rb.setSelected(true);
                break;
            }
        }
    }
}
