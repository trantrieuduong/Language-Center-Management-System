package com.dto;

import com.model.financial.InvoiceStatus;
import lombok.Data;

@Data
public class InvoiceDTO {
    private Long invoiceID;
    private InvoiceStatus status;
}
