package com.stream;

import com.model.financial.Invoice;
import com.model.financial.InvoiceStatus;

import java.util.List;

public class InvoiceStreamQueries {

    private InvoiceStreamQueries() {
    }

    /** Lọc theo trạng thái hóa đơn */
    public static List<Invoice> filterByStatus(List<Invoice> list, InvoiceStatus status) {
        return list.stream()
                .filter(i -> i.getStatus() == status)
                .toList();
    }

    /** Tìm theo tên học viên (không phân biệt hoa thường) */
    public static List<Invoice> searchByStudentName(List<Invoice> list, String keyword) {
        String kw = keyword.toLowerCase();
        return list.stream()
                .filter(i -> i.getStudent() != null
                        && i.getStudent().getFullName() != null
                        && i.getStudent().getFullName().toLowerCase().contains(kw))
                .toList();
    }

    /** Tìm theo tên lớp học (không phân biệt hoa thường) */
    public static List<Invoice> searchByClassName(List<Invoice> list, String keyword) {
        String kw = keyword.toLowerCase();
        return list.stream()
                .filter(i -> i.getAclass() != null
                        && i.getAclass().getClassName() != null
                        && i.getAclass().getClassName().toLowerCase().contains(kw))
                .toList();
    }

    /** Tìm theo tên học viên HOẶC tên lớp học */
    public static List<Invoice> search(List<Invoice> list, String keyword) {
        String kw = keyword.toLowerCase();
        return list.stream()
                .filter(i -> {
                    boolean matchStudent = i.getStudent() != null
                            && i.getStudent().getFullName() != null
                            && i.getStudent().getFullName().toLowerCase().contains(kw);
                    boolean matchClass = i.getAclass() != null
                            && i.getAclass().getClassName() != null
                            && i.getAclass().getClassName().toLowerCase().contains(kw);
                    return matchStudent || matchClass;
                })
                .toList();
    }
}
