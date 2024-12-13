package com.kraken.api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public final class InvoiceServiceException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final HttpStatusCode code;
    private final String detail;

    public InvoiceServiceException(final HttpStatus httpStatus, final HttpStatusCode code, final String detail) {
        super(detail);
        this.httpStatus = httpStatus;
        this.code = code;
        this.detail = detail;
    }
}
