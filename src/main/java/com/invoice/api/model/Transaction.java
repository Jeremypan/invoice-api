package com.invoice.api.model;

import com.invoice.api.web.rest.v1.validator.annotations.ValidDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record Transaction(
        @NotBlank(message = "transactionId must not be blank")
        String transactionId,
        @NotBlank(message = "dateReceived must not be blank")
        @ValidDateTime
        String dateReceived,
        @NotBlank(message = "transactionDate must not be blank")
        @ValidDateTime
        String transactionDate,
        @ValidDateTime
        String billingPeriodStart,
        @ValidDateTime
        String billingPeriodEnd,
        @NotNull(message = "netTrxnAmount must not be empty")
        BigDecimal netTransactionAmount,
        @NotNull(message = "gstAmount must not be empty")
        BigDecimal gstAmount
) {
}
