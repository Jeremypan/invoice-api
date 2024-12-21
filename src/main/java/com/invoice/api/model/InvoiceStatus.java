package com.invoice.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record InvoiceStatus(
        Status status,
        String reason
) {

}
