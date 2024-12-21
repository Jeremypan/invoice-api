package com.invoice.api.service;

import com.invoice.api.model.Invoice;
import com.invoice.api.model.InvoiceStatus;

import java.util.List;

public interface InvoiceService {

    void createInvoice(Invoice invoice);

    Invoice getInvoice(String invoiceId);

    List<Invoice> getAllInvoice(Integer pageNo, Integer pageSize);

    InvoiceStatus validateInvoiceStatus(String invoiceId);
}
