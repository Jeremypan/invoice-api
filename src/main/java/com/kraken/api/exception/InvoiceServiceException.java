package com.kraken.api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public final class InvoiceServiceException extends RuntimeException {
    private final HttpStatusCode code;
    private final String detail;

    public InvoiceServiceException(final HttpStatusCode code, final String detail) {
        super(detail);
        this.code = code;
        this.detail = detail;
    }
}
