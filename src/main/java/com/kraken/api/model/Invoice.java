package com.kraken.api.model;

import lombok.Builder;

import java.util.List;

@Builder
public record Invoice(
        String invoiceId,
        String invoiceNumber,
        Double grossAmount,
        Double gstAmount,
        Double netAmount,
        String receiptDate,
        String paymentDueDate,
        Integer totalNumTrxn,
        List<Transaction> transactionList
) {
}
