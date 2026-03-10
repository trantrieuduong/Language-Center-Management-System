package com.ui.table;

import com.model.financial.Invoice;

import javax.swing.table.AbstractTableModel;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class InvoiceTableModel extends AbstractTableModel {

    private static final String[] COLS = {
            "#", "Học viên", "Lớp học", "Khóa học", "Tổng tiền", "Trạng thái", "Ngày tạo"
    };

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final NumberFormat CURRENCY = NumberFormat.getCurrencyInstance(Locale.of("vi", "VN"));

    private List<Invoice> data = new ArrayList<>();

    public void setData(List<Invoice> list) {
        this.data = list == null ? new ArrayList<>() : list;
        fireTableDataChanged();
    }

    public Invoice getRow(int row) {
        return data.get(row);
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return COLS.length;
    }

    @Override
    public String getColumnName(int col) {
        return COLS[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        Invoice inv = data.get(row);
        return switch (col) {
            case 0 -> inv.getInvoiceID();
            case 1 -> inv.getStudent() != null ? inv.getStudent().getFullName() : "";
            case 2 -> inv.getAclass() != null ? inv.getAclass().getClassName() : "";
            case 3 -> (inv.getAclass() != null && inv.getAclass().getCourse() != null)
                    ? inv.getAclass().getCourse().getCourseName()
                    : "";
            case 4 -> inv.getTotalAmount() != null
                    ? CURRENCY.format(inv.getTotalAmount())
                    : "";
            case 5 -> inv.getStatus() != null ? inv.getStatus().name() : "";
            case 6 -> inv.getIssuedAt() != null ? inv.getIssuedAt().format(DATE_FMT) : "";
            default -> null;
        };
    }
}
