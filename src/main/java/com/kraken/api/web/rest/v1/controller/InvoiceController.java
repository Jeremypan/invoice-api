package com.kraken.api.web.rest.v1.controller;

import com.kraken.api.model.Invoice;
import com.kraken.api.service.InvoiceService;
import jakarta.validation.Valid;
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
            @RequestBody @Valid Invoice invoice
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
    public ResponseEntity<?> getInvoice(@RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResponseEntity.status(200).body(invoiceService.getAllInvoice(pageNo, pageSize));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            path = "/invoice/{invoiceId}"
    )
    public ResponseEntity<?> getInvoice(@PathVariable String invoiceId) {
        return ResponseEntity.status(200).body(invoiceService.getInvoice(invoiceId));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            path = "/invoice/{invoiceId}/status"
    )
    public ResponseEntity<?> getInvoiceStatus(@PathVariable String invoiceId) {
        return ResponseEntity.status(200).body(invoiceService.validInvoice(invoiceId));
    }
}
