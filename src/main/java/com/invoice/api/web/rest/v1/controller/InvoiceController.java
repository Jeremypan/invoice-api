package com.invoice.api.web.rest.v1.controller;

import com.invoice.api.exception.model.Error;
import com.invoice.api.model.Invoice;
import com.invoice.api.model.InvoiceStatus;
import com.invoice.api.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    @Operation(
            summary = "Create Invoice",
            description = "Create Invoice",
            responses = {@ApiResponse(
                    responseCode = "201",
                    description = "No Response Content if it is successful")
            }
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
    @Operation(
            summary = "Get a list of all invoice by optional params",
            description = "Get a list of invoices: default params: pageNo=0 and pageSize=10",
            responses = {@ApiResponse(
                    responseCode = "200",
                    description = "The response for all invoices result",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = Invoice.class))
                            )
                    }),
                    @ApiResponse(responseCode = "400",
                            description = "The response for bad request",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = Error.class)
                                    )
                            })
            }
    )
    public ResponseEntity<?> getInvoice(@RequestParam(defaultValue = "0")
                                        @Min(value = 0, message = "pageNo must not be less than 0")
                                            final Integer pageNo,
                                        @RequestParam(defaultValue = "10")
                                        @Min(value = 1, message = "pageSize must be bigger than 0")
                                        final Integer pageSize) {
        final var invoices = invoiceService.getAllInvoice(pageNo, pageSize);
        return ResponseEntity.status(200).body(invoices);
    }

    @RequestMapping(
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            path = "/invoice/{invoiceId}"
    )
    @Operation(
            summary = "Get invoice by invoiceId",
            description = "Get invoice by invoiceId eg: 123456",
            responses = {@ApiResponse(
                    responseCode = "200",
                    description = "The response for single invoice",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Invoice.class)
                            )
                    }),
                    @ApiResponse(responseCode = "400",
                            description = "The response for bad request",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = Error.class)
                                    )
                            })
            }
    )
    public ResponseEntity<?> getInvoice(@PathVariable final String invoiceId) {
        return ResponseEntity.status(200).body(invoiceService.getInvoice(invoiceId));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            path = "/invoice/{invoiceId}/status"
    )
    @Operation(
            summary = "Get invoice Status by invoiceId",
            description = "Get invoice status by invoiceId eg: 123456",
            responses = {@ApiResponse(
                    responseCode = "200",
                    description = "The response for single invoice status",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = InvoiceStatus.class)
                            )
                    }),
                    @ApiResponse(responseCode = "400",
                            description = "The response for bad request",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = Error.class)
                                    )
                            })
            }
    )
    public ResponseEntity<?> getInvoiceStatus(final @PathVariable String invoiceId) {
        return ResponseEntity.status(200).body(invoiceService.validateInvoiceStatus(invoiceId));
    }
}
