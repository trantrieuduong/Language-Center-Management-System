package com.ui.table;

import com.model.academic.Class;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class ClassTableModel extends AbstractTableModel {
    private static final String[] COLUMNS = {
            "ID", "Tên", "Học viên tối đa", "Trạng thái", "Tên khóa học", "Tên giáo viên [Mã]", "Tên phòng học [Mã]", "Ngày bắt đầu", "Ngày kết thúc"
    };

    private List<Class> data = new ArrayList<>();

    public void setData(List<Class> classes) {
        this.data = classes == null ? new ArrayList<>() : classes;
        fireTableDataChanged();
    }

    public Class getRow(int row) {
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
        Class c = data.get(row);
        return switch (col) {
            case 0 -> c.getClassID();
            case 1 -> c.getClassName();
            case 2 -> c.getMaxStudent() != null ? c.getMaxStudent().toString() : "";
            case 3 -> c.getStatus() != null ? c.getStatus() : "";
            case 4 -> c.getCourse() != null ? c.getCourse().getCourseName() : "";
            case 5 -> c.getTeacher() != null ? c.getTeacher().getFullName() +" [" + c.getTeacher().getTeacherID() +"]" : "";
            case 6 -> c.getRoom() != null ? c.getRoom().getRoomName() + " [" + c.getRoom().getRoomID()+"]" : "";
            case 7 -> c.getStartDate() != null ? c.getStartDate() : "";
            case 8 -> c.getEndDate() != null ? c.getEndDate() : "";
            default -> "";
        };
    }
}

