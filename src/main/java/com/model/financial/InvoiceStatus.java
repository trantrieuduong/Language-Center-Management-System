package com.model.financial;

public enum InvoiceStatus {
    DRAFT,//Hóa đơn mới chỉ được khởi tạo, đang trong quá trình soạn thảo
    ISSUED,//Hóa đơn đã được chốt và gửi đến sinh viên/phụ huynh
    PAID,
    CANCELED // hủy bỏ do sai sót
}
