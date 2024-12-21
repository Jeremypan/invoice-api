package com.invoice.api.repository;

import com.invoice.api.model.entity.InvoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<InvoiceEntity, String> {
    Optional<InvoiceEntity> getInvoiceEntityByInvoiceId(String invoiceId);
}
