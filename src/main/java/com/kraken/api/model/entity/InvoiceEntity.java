package com.kraken.api.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import org.springframework.data.domain.Persistable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "invoice")
@Data
public class InvoiceEntity implements Persistable<String> {

    @Id
    @Column(name = "invoice_id", nullable = false)
    String invoiceId;

    @Column(name = "invoice_number", nullable = false, unique = true)
    String invoiceNum;

    @Column(name = "gross_amount", nullable = false)
    private BigDecimal grossAmount;

    @Column(name = "gst_amount", nullable = false)
    private BigDecimal gstAmount;

    @Column(name = "net_amount", nullable = false)
    private BigDecimal netAmount;

    @Column(name = "receipt_date", nullable = false)
    private LocalDate receiptDate;

    @Column(name = "payment_due_date", nullable = false)
    private LocalDate paymentDueDate;

    @Column(name = "total_number_transaction", nullable = false)
    private Integer totalNumberTransaction;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    private List<TransactionEntity> transactionEntities;

    @Transient
    private boolean isNewEntry = true;

    @Override
    public String getId() {
        return this.invoiceId;
    }

    @Override
    public boolean isNew() {
        return this.isNewEntry;
    }
}
