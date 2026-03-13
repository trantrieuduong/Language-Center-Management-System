package com.stream;

import com.model.financial.Invoice;
import com.model.financial.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public final class InvoiceStreamQueries {

    private InvoiceStreamQueries() {
    }

    /** Lọc theo trạng thái hóa đơn */
    public static List<Invoice> filterByStatus(List<Invoice> list, InvoiceStatus status) {
        return list.stream()
                .filter(i -> i.getStatus() == status)
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

    /**
     * Đếm số hóa đơn theo từng trạng thái.
     */
    public static Map<InvoiceStatus, Long> countByStatus(List<Invoice> list) {
        return list.stream()
                .collect(Collectors.groupingBy(Invoice::getStatus, Collectors.counting()));
    }

    /**
     * Doanh thu 7 ngày gần nhất, mỗi ngày một entry.
     */
    public static Map<String, BigDecimal> revenueByDay(List<Invoice> list) {
        LocalDate today = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM");

        // Khởi tạo map với 7 ngày
        LinkedHashMap<String, BigDecimal> result = new LinkedHashMap<>();
        for (int i = 6; i >= 0; i--) {
            result.put(today.minusDays(i).format(fmt), BigDecimal.ZERO);
        }

        // Cộng dồn doanh thu PAID trong 7 ngày
        list.stream()
                .filter(i -> i.getStatus() == InvoiceStatus.PAID
                        && i.getIssuedAt() != null)
                .filter(i -> !i.getIssuedAt().toLocalDate().isBefore(today.minusDays(6)))
                .forEach(i -> {
                    String key = i.getIssuedAt().toLocalDate().format(fmt);
                    result.merge(key,
                            i.getTotalAmount() != null ? i.getTotalAmount() : BigDecimal.ZERO,
                            BigDecimal::add);
                });
        return result;
    }

    /**
     * Doanh thu 4 tuần gần nhất
     */
    public static Map<String, BigDecimal> revenueByWeek(List<Invoice> list) {
        LocalDate today = LocalDate.now();
        WeekFields wf = WeekFields.of(Locale.getDefault());

        LinkedHashMap<String, BigDecimal> result = new LinkedHashMap<>();
        for (int i = 3; i >= 0; i--) {
            LocalDate d = today.minusWeeks(i);
            String key = "T" + d.get(wf.weekOfWeekBasedYear()) + "/" + d.getYear();
            result.put(key, BigDecimal.ZERO);
        }

        LocalDate cutoff = today.minusWeeks(3).with(wf.dayOfWeek(), 1);
        list.stream()
                .filter(i -> i.getStatus() == InvoiceStatus.PAID
                        && i.getIssuedAt() != null)
                .filter(i -> !i.getIssuedAt().toLocalDate().isBefore(cutoff))
                .forEach(i -> {
                    LocalDate d = i.getIssuedAt().toLocalDate();
                    String key = "T" + d.get(wf.weekOfWeekBasedYear()) + "/" + d.getYear();
                    result.merge(key,
                            i.getTotalAmount() != null ? i.getTotalAmount() : BigDecimal.ZERO,
                            BigDecimal::add);
                });
        return result;
    }

    /**
     * Doanh thu 6 tháng gần nhất.
     */
    public static Map<String, BigDecimal> revenueByMonth(List<Invoice> list) {
        LocalDate today = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM/yyyy");

        LinkedHashMap<String, BigDecimal> result = new LinkedHashMap<>();
        for (int i = 5; i >= 0; i--) {
            result.put(today.minusMonths(i).format(fmt), BigDecimal.ZERO);
        }

        LocalDate cutoff = today.minusMonths(5).withDayOfMonth(1);
        list.stream()
                .filter(i -> i.getStatus() == InvoiceStatus.PAID
                        && i.getIssuedAt() != null)
                .filter(i -> !i.getIssuedAt().toLocalDate().isBefore(cutoff))
                .forEach(i -> {
                    String key = i.getIssuedAt().toLocalDate().format(fmt);
                    result.merge(key,
                            i.getTotalAmount() != null ? i.getTotalAmount() : BigDecimal.ZERO,
                            BigDecimal::add);
                });
        return result;
    }
}

