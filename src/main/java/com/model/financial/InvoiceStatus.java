package com.model.financial;

public enum InvoiceStatus {
    PENDING, // Chờ thanh toán (tự động tạo khi đăng ký lớp)
    PAID, // Đã thanh toán
    CANCELED // Đã hủy
}
