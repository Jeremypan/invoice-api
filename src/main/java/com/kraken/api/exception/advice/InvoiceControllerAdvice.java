package com.kraken.api.exception.advice;

import com.kraken.api.exception.InvoiceServiceException;
import com.kraken.api.exception.model.Error;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class InvoiceControllerAdvice {

    @ExceptionHandler(InvoiceServiceException.class)
    public ResponseEntity<Error> processInvoiceServiceException(final HttpServletRequest request,
                                                                final InvoiceServiceException exception) {
        return ResponseEntity.status(exception.getCode()).body(
                buildError(exception.getHttpStatus(),
                        exception.getCode(),
                        request.getRequestURI(),
                        exception.getDetail()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Error> processValidationRequestException(final HttpServletRequest request,
                                                                   final MethodArgumentNotValidException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                buildError(HttpStatus.BAD_REQUEST,
                        HttpStatusCode.valueOf(400),
                        request.getRequestURI(),
                        exception.getAllErrors().getFirst().getDefaultMessage()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Error> processConstraintViolationException(final HttpServletRequest request,
                                                                     final ConstraintViolationException exception) {
        final var constraintViolations = exception.getConstraintViolations();
        if (constraintViolations.stream().findFirst().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildError(HttpStatus.BAD_REQUEST,
                    HttpStatusCode.valueOf(400), request.getRequestURI(), exception.getMessage()));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildError(HttpStatus.BAD_REQUEST,
                    HttpStatusCode.valueOf(400), request.getRequestURI(),
                    constraintViolations.stream().findFirst().get().getMessage()));
        }
    }

    private Error buildError(final HttpStatus httpStatus,
                             final HttpStatusCode code,
                             final String path,
                             final String detail) {
        return Error.builder()
                .detail(detail)
                .error(httpStatus.getReasonPhrase())
                .path(path)
                .status(String.valueOf(code.value()))
                .timestamp(Instant.now().toString()).build();
    }
}
