package com.ui.table;

import com.model.operation.Attendance;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AttendanceTableModel extends AbstractTableModel {
    // Có thêm cột "Giờ bắt đầu" do một ngày một lớp có thể có nhiều giờ học
    private static final String[] COLUMNS = {
            "Mã học viên", "Tên học viên", "Tên lớp học", "Ngày học", "Giờ bắt đầu", "Trạng thái"
    };

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");


    private List<Attendance> data = new ArrayList<>();

    public void setData(List<Attendance> Attendances) {
        this.data = Attendances == null ? new ArrayList<>() : Attendances;
        fireTableDataChanged();
    }

    public Attendance getRow(int row) {
        return data.get(row);
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int col) {
        return COLUMNS[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        Attendance a = data.get(row);
        return switch (col) {
            case 0 -> a.getStudent() != null ? a.getStudent().getStudentID() : "";
            case 1 -> a.getStudent() != null ? a.getStudent().getFullName() : "";
            case 2 -> a.getSchedule() != null && a.getSchedule().getAClass() != null 
                    ? a.getSchedule().getAClass().getClassName() : "";
            case 3 -> a.getSchedule() != null ? a.getSchedule().getDate().format(dateFormatter) : "";
            case 4 -> a.getSchedule() != null ? a.getSchedule().getStartTime().format(timeFormatter) : "";
            case 5 -> a.getStatus() != null ? a.getStatus() : "";
            default -> "";
        };
    }
}


