package com.kraken.api.service;

import com.kraken.api.model.Invoice;
import com.kraken.api.model.Transaction;
import com.kraken.api.model.entity.InvoiceEntity;
import com.kraken.api.model.entity.TransactionEntity;
import com.kraken.api.repository.InvoiceRepository;
import com.kraken.api.utils.DateConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService{

    private final InvoiceRepository invoiceRepository;

    @Override
    public void createInvoice(Invoice invoice) {
        invoiceRepository.saveAndFlush(covertToInvoiceEntity(invoice));
    }

    @Override
    public Invoice getInvoice(String invoiceId) {
        return convertToInvoice(invoiceRepository.getInvoiceEntityByInvoiceId((invoiceId)));
    }

    @Override
    public List<Invoice> getAllInvoice() {
        return invoiceRepository.findAll().stream().map(
                this::convertToInvoice
        ).toList();
    }

    private InvoiceEntity covertToInvoiceEntity(Invoice invoice) {
        InvoiceEntity invoiceEntity = new InvoiceEntity();
        invoiceEntity.setInvoiceId(invoice.invoiceId());
        invoiceEntity.setInvoiceNum(invoice.invoiceNumber());
        invoiceEntity.setGrossAmount(invoice.grossAmount());
        invoiceEntity.setGstAmount(invoice.gstAmount());
        invoiceEntity.setNetAmount(invoice.netAmount());
        invoiceEntity.setReceiptDate(DateConverter.convertStringToDate(invoice.receiptDate()));
        invoiceEntity.setPaymentDueDate(DateConverter.convertStringToDate(invoice.paymentDueDate()));
        invoiceEntity.setTotalNumberTransaction(invoice.totalNumTrxn());
        invoiceEntity.setTransactionEntities(invoice.transactionList().stream().map(
                transaction -> {
                    TransactionEntity transactionEntity = new TransactionEntity();
                    transactionEntity.setTransactionId(transaction.transactionId());
                    transactionEntity.setTransactionDate(DateConverter.convertStringToDate(transaction.transactionDate()));
                    transactionEntity.setDateReceived(DateConverter.convertStringToDate(transaction.dateReceived()));
                    transactionEntity.setInvoice(invoiceEntity);
                    transactionEntity.setInvoiceNumber(invoice.invoiceNumber());
                    transactionEntity.setBillingPeriodStart(DateConverter.convertStringToDate(transaction.billingPeriodStart()));
                    transactionEntity.setBillingPeriodEnd(DateConverter.convertStringToDate(transaction.billingPeriodEnd()));
                    transactionEntity.setNetTransactionAmount(transaction.netTransactionAmount());
                    transactionEntity.setGstAmount(transaction.gstAmount());
                    return transactionEntity;
                }
        ).toList());
        return invoiceEntity;
    }

    private Invoice convertToInvoice(InvoiceEntity invoiceEntity) {
        return Invoice.builder()
                .invoiceId(invoiceEntity.getInvoiceId())
                .invoiceNumber(invoiceEntity.getInvoiceNum())
                .grossAmount(invoiceEntity.getGrossAmount())
                .gstAmount(invoiceEntity.getGstAmount())
                .netAmount(invoiceEntity.getNetAmount())
                .receiptDate(DateConverter.convertDateToString(invoiceEntity.getReceiptDate()))
                .paymentDueDate(DateConverter.convertDateToString(invoiceEntity.getPaymentDueDate()))
                .totalNumTrxn(invoiceEntity.getTotalNumberTransaction())
                .transactionList(invoiceEntity.getTransactionEntities().stream().map(
                        transactionEntity -> {
                            return Transaction.builder()
                                    .transactionId(transactionEntity.getTransactionId())
                                    .dateReceived(DateConverter.convertDateToString(transactionEntity.getDateReceived()))
                                    .transactionDate(DateConverter.convertDateToString(transactionEntity.getTransactionDate()))
                                    .billingPeriodEnd(DateConverter.convertDateToString(transactionEntity.getBillingPeriodStart()))
                                    .billingPeriodEnd(DateConverter.convertDateToString(transactionEntity.getBillingPeriodEnd()))
                                    .netTransactionAmount(transactionEntity.getNetTransactionAmount())
                                    .gstAmount(transactionEntity.getGstAmount())
                                    .build();
                        }
                ).toList())
                .build();
    }
}
