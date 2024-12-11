package com.kraken.api.service;

import com.kraken.api.model.Invoice;

import java.util.List;

public interface InvoiceService {

    void createInvoice(Invoice invoice);

    Invoice getInvoice(String invoiceId);

    List<Invoice> getAllInvoice();
}
