package com.kraken.api.model.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name="transaction")
public class TransactionEntity {

    @Id
    @Column(name = "transaction_id", nullable = false)
    private String transactionId;

    @Column(name = "date_received", nullable = false)
    private LocalDate dateReceived;

    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invoice_id", nullable = false)
    private InvoiceEntity invoice;

    @Column(name = "invoice_number", nullable = false)
    private String invoiceNumber;

    @Column(name = "billing_period_start")
    private LocalDate billingPeriodStart;

    @Column(name = "billing_period_end")
    private LocalDate billingPeriodEnd;

    @Column(name = "net_transaction_amount", nullable = false)
    private Float netTransactionAmount;

    @Column(name = "gst_amount", nullable = false)
    private Float gstAmount;
}
