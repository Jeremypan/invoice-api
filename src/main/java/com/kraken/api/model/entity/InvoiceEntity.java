package com.kraken.api.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "invoice")
@Data
public class InvoiceEntity {

    @Id
    @Column(name="invoice_id", nullable = false)
    String invoiceId;

    @Column(name="invoice_num", nullable = false)
    String invoiceNum;

    @Column(name = "gross_amount", nullable = false)
    private Double grossAmount;

    @Column(name = "gst_amount", nullable = false)
    private Double gstAmount;

    @Column(name = "net_amount", nullable = false)
    private Double netAmount;

    @Column(name = "receipt_date", nullable = false)
    private Double receiptDate;

    @Column(name = "payment_due_date", nullable = false)
    private Double paymentDueDate;

    @Column(name = "total_number_transaction", nullable = false)
    private Double totalNumberTransaction;

    @OneToMany(mappedBy = "invoice")
    private List<TransactionEntity> transactionEntities;

}
