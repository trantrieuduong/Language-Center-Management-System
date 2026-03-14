package com.ui.table;

import com.model.academic.Result;
import lombok.Getter;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class ResultTableModel extends AbstractTableModel {
    // Có thêm cột "Giờ bắt đầu" do một ngày một lớp có thể có nhiều giờ học 
    private static final String[] COLUMNS = {
            "Mã học viên", "Tên học viên", "Tên lớp học [mã]", "Điểm", "Nhận xét"
    };


    @Getter
    private List<Result> data = new ArrayList<>();

    public void setData(List<Result> results) {
        this.data = results == null ? new ArrayList<>() : results;
        fireTableDataChanged();
    }

    public Result getRow(int row) {
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
        Result r = data.get(row);
        return switch (col) {
            case 0 -> r.getStudent() != null ? r.getStudent().getStudentID() : "";
            case 1 -> r.getStudent() != null ? r.getStudent().getFullName() : "";
            case 2 -> r.getAClass() != null ? r.getAClass().getClassName() + "[" + r.getAClass().getClassID() + "]" : "";
            case 3 -> r.getScore() != null ? r.getScore() : "";
            case 4 -> r.getComment() != null ? r.getComment() : "";
            default -> "";
        };
    }
}



