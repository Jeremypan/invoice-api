package com.kraken.api.web.rest.v1.controller;

import com.kraken.api.model.Invoice;
import com.kraken.api.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @RequestMapping(
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            path = "/invoice"

    )
    public ResponseEntity<?> createInvoice(
            @RequestBody Invoice invoice
    ) {
        invoiceService.createInvoice(invoice);
        return ResponseEntity.status(201).build();
    }

    @RequestMapping(
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            path = "/invoice"
    )
    public ResponseEntity<?> getInvoice() {
        return ResponseEntity.status(200).body(invoiceService.getAllInvoice());
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
        return ResponseEntity.status(204).body(invoiceId);
    }


}
