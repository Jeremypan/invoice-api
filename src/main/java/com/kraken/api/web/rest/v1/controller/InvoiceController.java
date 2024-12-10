package com.kraken.api.web.rest.v1.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/invoice")
public class InvoiceController {

    @RequestMapping(
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            path = "/invoice"

    )
    public ResponseEntity<?> createInvoice() {
        return ResponseEntity.status(204).build();
    }

    @RequestMapping(
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            path = "/invoice"
    )
    public ResponseEntity<?> getInvoice() {
        return ResponseEntity.status(204).build();
    }

    @RequestMapping(
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            path = "/invoice/{invoiceId}"
    )
    public ResponseEntity<?> getInvoice(@PathVariable String invoiceId) {
        return ResponseEntity.status(204).body(invoiceId);
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
