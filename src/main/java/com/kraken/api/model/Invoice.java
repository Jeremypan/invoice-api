package com.kraken.api.model;

import lombok.Builder;

@Builder
public record Invoice(
        String invoiceId,
        String invoiceNumber,
        Double grossAmount,
        Double gstAmount,
        Double netAmount,
        String receiptDate,
        String paymentDueDate,
        Integer totalNumTrxn
) {
}
