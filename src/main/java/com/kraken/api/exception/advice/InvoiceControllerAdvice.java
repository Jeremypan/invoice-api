package com.kraken.api.exception.advice;

import com.kraken.api.exception.InvoiceServiceException;
import com.kraken.api.exception.model.Error;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class InvoiceControllerAdvice {

    @ExceptionHandler(InvoiceServiceException.class)
    public ResponseEntity<Error> processInvoiceServiceException(final HttpServletRequest request, final InvoiceServiceException exception) {
        return ResponseEntity.status(exception.getCode()).body(
                buildError(exception.getCode(), request.getRequestURI(), exception.getDetail()));
    }

    private Error buildError(final HttpStatusCode code, final String path, final String detail) {
        return Error.builder()
                .error(detail)
                .path(path)
                .status(String.valueOf(code.value()))
                .timestamp(Instant.now().toString()).build();
    }
}
