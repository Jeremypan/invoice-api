package com.kraken.api.repository;

import com.kraken.api.model.entity.InvoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<InvoiceEntity, String> {

    InvoiceEntity getInvoiceEntityByInvoiceId(String invoiceId);
}
