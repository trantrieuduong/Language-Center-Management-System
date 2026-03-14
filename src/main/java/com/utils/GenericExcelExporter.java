package com.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

public class GenericExcelExporter<T> {

    public void export(List<T> data, String filePath, String title, String[] headers, String[] fieldNames) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Data");

            // 1. Title Style
            CellStyle titleStyle = createStyle(workbook, (short) 16, true, HorizontalAlignment.CENTER);
            titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            titleStyle.setWrapText(true);

            // Set width cột dựa trên title trước — tránh bị autoSize ghi đè
            int minColWidth = 6000;
            int titleTotalWidth = title.length() * 400; // ước lượng pixel theo độ dài
            int colWidthFromTitle = titleTotalWidth / headers.length;
            int finalColWidth = Math.max(minColWidth, colWidthFromTitle);

            for (int i = 0; i < headers.length; i++) {
                sheet.setColumnWidth(i, finalColWidth);
            }

            // Tính chiều cao title tự động theo wrap
            int charsPerLine = (finalColWidth / 256) * headers.length; // 256 units ≈ 1 ký tự
            int linesNeeded = (int) Math.ceil((double) title.length() / Math.max(charsPerLine, 1));
            float rowHeight = Math.max(40f, linesNeeded * 22f);

            Row titleRow = sheet.createRow(0);
            titleRow.setHeightInPoints(rowHeight); // chiều cao động

            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(title.toUpperCase());
            titleCell.setCellStyle(titleStyle);

            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headers.length - 1));

            // 2. Header
            CellStyle headerStyle = createStyle(workbook, (short) 12, true, HorizontalAlignment.CENTER);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Row headerRow = sheet.createRow(2);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 3. Data
            int rowIdx = 3;
            for (T item : data) {
                Row row = sheet.createRow(rowIdx++);
                for (int i = 0; i < fieldNames.length; i++) {
                    Cell cell = row.createCell(i);
                    Object value = getFieldValue(item, fieldNames[i]);
                    if (value != null) {
                        cell.setCellValue(value.toString());
                    }
                }
            }

            // 4. autoSize chỉ áp dụng cho cột data/header, sau đó giữ tối thiểu bằng finalColWidth
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                if (sheet.getColumnWidth(i) < finalColWidth) {
                    sheet.setColumnWidth(i, finalColWidth); // giữ width đủ cho title
                }
            }

            try (FileOutputStream out = new FileOutputStream(filePath)) {
                workbook.write(out);
            }
        }
    }

    // Hàm bổ trợ lấy giá trị của trường bằng Reflection
    private Object getFieldValue(T item, String fieldName) {
        try {
            // Hỗ trợ truy cập lồng nhau (ví dụ: "schedule.date")
            String[] parts = fieldName.split("\\.");
            Object current = item;
            for (String part : parts) {
                if (current == null) return null;
                Field field = current.getClass().getDeclaredField(part);
                field.setAccessible(true);
                current = field.get(current);
            }
            return current;
        } catch (Exception e) {
            return ""; // Trả về trống nếu không tìm thấy trường
        }
    }

    private CellStyle createStyle(Workbook wb, short fontSize, boolean bold, HorizontalAlignment align) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontHeightInPoints(fontSize);
        font.setBold(bold);
        style.setFont(font);
        style.setAlignment(align);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
}