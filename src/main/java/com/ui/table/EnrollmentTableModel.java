package com.ui.table;

import com.model.academic.Enrollment;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentTableModel extends AbstractTableModel {
    private static final String[] COLUMNS = {
            "ID", "ID học viên", "ID lớp học", "Trạng thái"
    };

    private List<Enrollment> data = new ArrayList<>();

    public void setData(List<Enrollment> enrollmentes) {
        this.data = enrollmentes == null ? new ArrayList<>() : enrollmentes;
        fireTableDataChanged();
    }

    public Enrollment getRow(int row) {
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
        Enrollment e = data.get(row);
        return switch (col) {
            case 0 -> e.getEnrollmentID();
            case 1 -> e.getStudent() != null ? e.getStudent().getStudentID() + " - " + e.getStudent().getFullName() : "";
            case 2 -> e.getAclass() != null ? e.getAclass().getClassID() + " - " +  e.getAclass().getClassName() : "";
            case 3 -> e.getStatus() != null ? e.getStatus() : "";
            default -> "";
        };
    }
}


