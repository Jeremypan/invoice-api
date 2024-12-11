package com.kraken.api.model;

import lombok.Builder;

@Builder
public record Transaction(
        String transactionId,
        String dateReceived,
        String transactionDate,
        String billingPeriodStart,
        String billingPeriodEnd,
        Double netTransactionAmount,
        Double gstAmount
) {
}
