package com.kraken.api.repository;

import com.kraken.api.model.entity.InvoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<InvoiceEntity, String> {

    Optional<InvoiceEntity> getInvoiceEntityByInvoiceId(String invoiceId);
}
