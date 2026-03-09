package com.ui.table;

import com.model.academic.Course;

import javax.swing.table.AbstractTableModel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CourseTableModel extends AbstractTableModel {
    private static final String[] COLUMNS = {
            "ID", "Tên", "Số Tiết / Tuần", "Học phí", "Cấp độ", "Trạng thái", "Mô tả"
    };

    private List<Course> data = new ArrayList<>();

    public void setData(List<Course> courses) {
        this.data = courses == null ? new ArrayList<>() : courses;
        fireTableDataChanged();
    }

    public Course getRow(int row) {
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
        Course s = data.get(row);
        DecimalFormat df = new DecimalFormat("#,##0.##");
        return switch (col) {
            case 0 -> s.getCourseID();
            case 1 -> s.getCourseName();
            case 2 -> s.getDuration() != null ? s.getDuration().toString() : "";
            case 3 -> s.getFee() != null ? df.format(s.getFee()) : "";
            case 4 -> s.getLevel() != null ? s.getLevel() : "";
            case 5 -> s.getStatus() != null ? s.getStatus().name() : "";
            case 6 -> s.getDescription() != null ? s.getDescription() : "";
            default -> "";
        };
    }
}
