package com.ui.table;

import com.model.user.Student;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class StudentTableModel extends AbstractTableModel {

    private static final String[] COLUMNS = {
            "ID", "Họ tên", "Ngày sinh", "Giới tính", "Điện thoại", "Email", "Trạng thái"
    };

    private List<Student> data = new ArrayList<>();

    public void setData(List<Student> students) {
        this.data = students == null ? new ArrayList<>() : students;
        fireTableDataChanged();
    }

    public Student getRow(int row) {
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
        Student s = data.get(row);
        return switch (col) {
            case 0 -> s.getStudentID();
            case 1 -> s.getFullName();
            case 2 -> s.getDateOfBirth() != null ? s.getDateOfBirth().toString() : "";
            case 3 -> s.getGender() != null ? s.getGender().name() : "";
            case 4 -> s.getPhone() != null ? s.getPhone() : "";
            case 5 -> s.getEmail() != null ? s.getEmail() : "";
            case 6 -> s.getStatus() != null ? s.getStatus().name() : "";
            default -> "";
        };
    }
}
