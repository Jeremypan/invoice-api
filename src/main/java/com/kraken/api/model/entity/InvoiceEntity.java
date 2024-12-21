package com.kraken.api.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "invoice")
@Data
public class InvoiceEntity implements Persistable<String> {

    @Id
    @Column(name="invoice_id", nullable = false)
    String invoiceId;

    @Column(name="invoice_number", nullable = false, unique = true)
    String invoiceNum;

    @Column(name = "gross_amount", nullable = false)
    private Double grossAmount;

    @Column(name = "gst_amount", nullable = false)
    private Double gstAmount;

    @Column(name = "net_amount", nullable = false)
    private Double netAmount;

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
