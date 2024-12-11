package com.kraken.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
        @JsonProperty("transactions")
        List<Transaction> transactionList
) {
}
