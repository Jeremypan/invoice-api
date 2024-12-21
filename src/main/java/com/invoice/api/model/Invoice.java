package com.invoice.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.invoice.api.web.rest.v1.validator.annotations.ValidDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
@JsonIgnoreProperties
public record Invoice(
        @NotBlank(message = "invoiceId must not be blank")
        String invoiceId,
        @NotBlank(message = "invoiceNumber must not be blank")
        String invoiceNumber,
        @NotNull(message = "grossAmount must not be empty")
        BigDecimal grossAmount,
        @NotNull(message = "gstAmount must not be empty")
        BigDecimal gstAmount,
        @NotNull(message = "netAmount must not be empty")
        BigDecimal netAmount,
        @NotBlank(message = "receiptDate must not be blank")
        @ValidDateTime
        String receiptDate,
        @NotBlank(message = "paymentDueDate must not be blank")
        @ValidDateTime
        String paymentDueDate,
        @NotNull(message = "totalNumTrxn must not be empty")
        Integer totalNumTrxn,
        @JsonProperty("transactions")
        @NotNull(message = "transactionList must not be null")
        List<@Valid Transaction> transactionList
) {
}
