package com.kraken.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kraken.api.web.rest.v1.validator.annotations.ValidDateTime;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
@JsonIgnoreProperties
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Invoice(
        @NotBlank(message = "invoiceId must not be blank")
        String invoiceId,
        @NotBlank(message = "invoiceNumber must not be blank")
        String invoiceNumber,
        @NotNull(message = "grossAmount must not be empty")
        Double grossAmount,
        @NotNull(message = "gstAmount must not be empty")
        Double gstAmount,
        @NotNull(message = "netAmount must not be empty")
        Double netAmount,
        @NotBlank(message = "receiptDate must not be blank")
        @ValidDateTime
        String receiptDate,
        @NotBlank(message = "paymentDueDate must not be blank")
        @ValidDateTime
        String paymentDueDate,
        @NotNull(message = "totalNumTrxn must not be empty")
        Integer totalNumTrxn,
        @Nullable
        @JsonProperty("transactions")
        List<@Valid Transaction> transactionList
) {
}
