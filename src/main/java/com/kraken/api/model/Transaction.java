package com.kraken.api.model;

import lombok.Builder;

@Builder
public record Transaction(
        String transactionId,
        String dateReceived,
        String transactionDate,
        String invoiceId,
        String invoiceNumber,
        String billingPeriodStart,
        String billingPeriodEnd,
        Double netAmount,
        Double gstAmount
) {
}
