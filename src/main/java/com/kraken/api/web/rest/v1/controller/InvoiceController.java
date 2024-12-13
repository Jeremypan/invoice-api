package com.kraken.api.web.rest.v1.controller;

import com.kraken.api.model.Invoice;
import com.kraken.api.service.InvoiceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Validated
public class InvoiceController {

    private final InvoiceService invoiceService;

    @RequestMapping(
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            path = "/invoice"

    )
    public ResponseEntity<?> createInvoice(
            @RequestBody @Valid final Invoice invoice
    ) {
        log.info("Creating Invoice");
        invoiceService.createInvoice(invoice);
        return ResponseEntity.status(201).build();
    }

    @RequestMapping(
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            path = "/invoice"
    )
    public ResponseEntity<?> getInvoice(@RequestParam(defaultValue = "0")
                                        @Min(value = 0, message = "pageNo must not be less than 0") final Integer pageNo,
                                        @RequestParam(defaultValue = "10")
                                        @Min(value = 1, message = "pageSize must be bigger than 0") final Integer pageSize) {
        final var invoices = invoiceService.getAllInvoice(pageNo, pageSize);
        return ResponseEntity.status(200).body(invoices);
    }

    @RequestMapping(
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            path = "/invoice/{invoiceId}"
    )
    public ResponseEntity<?> getInvoice(@PathVariable final String invoiceId) {
        return ResponseEntity.status(200).body(invoiceService.getInvoice(invoiceId));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            path = "/invoice/{invoiceId}/status"
    )
    public ResponseEntity<?> getInvoiceStatus(@PathVariable String invoiceId) {
        return ResponseEntity.status(200).body(invoiceService.validateInvoiceStatus(invoiceId));
    }
}
