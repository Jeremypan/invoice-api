package com.kraken.api.service;

import com.kraken.api.model.Invoice;
import com.kraken.api.model.InvoiceStatus;

import java.util.List;

public interface InvoiceService {

    void createInvoice(Invoice invoice);

    Invoice getInvoice(String invoiceId);

    List<Invoice> getAllInvoice();

    InvoiceStatus validInvoice(String invoiceId);
}
