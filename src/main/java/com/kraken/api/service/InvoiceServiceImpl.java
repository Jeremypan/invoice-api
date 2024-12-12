package com.kraken.api.service;

import com.kraken.api.exception.InvoiceServiceException;
import com.kraken.api.model.Invoice;
import com.kraken.api.model.InvoiceStatus;
import com.kraken.api.model.Status;
import com.kraken.api.model.Transaction;
import com.kraken.api.model.entity.InvoiceEntity;
import com.kraken.api.model.entity.TransactionEntity;
import com.kraken.api.repository.InvoiceRepository;
import com.kraken.api.utils.DateConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService{

    private final InvoiceRepository invoiceRepository;

    @Override
    public void createInvoice(final Invoice invoice) {
        invoiceRepository.saveAndFlush(covertToInvoiceEntity(invoice));
    }

    @Override
    public Invoice getInvoice(final String invoiceId) {
        final var invoiceEntity = invoiceRepository.getInvoiceEntityByInvoiceId(invoiceId);
        if (invoiceEntity.isPresent()) {
            return convertToInvoice(invoiceEntity.get());
        } else {
            log.error("There is no invoice with id={}", invoiceId);
            throw new InvoiceServiceException(HttpStatusCode.valueOf(400), "Get Invoice By Id=%s failed".formatted(invoiceId));
        }
    }

    @Override
    public List<Invoice> getAllInvoice() {
        return invoiceRepository.findAll().stream().map(
                this::convertToInvoice
        ).toList();
    }

    @Override
    public InvoiceStatus validInvoice(final String invoiceId) {
        final var invoice = getInvoice(invoiceId);

        if (!isValidNumberOfTransaction(invoice)) {
            return InvoiceStatus.builder().status(Status.INVALID).reason("The number of lines does not match the number of transactions").build();
        }

        if (!isValidTotalTransactionAmount(invoice)) {
            return InvoiceStatus.builder().status(Status.INVALID).reason("The total value of transaction lines does not add up to the invoice total").build();
        }

        return InvoiceStatus.builder().status(Status.VALID).build();
    }

    private boolean isValidNumberOfTransaction(Invoice invoice) {
        return invoice.totalNumTrxn() == invoice.transactionList().size();
    }

    private boolean isValidTotalTransactionAmount(Invoice invoice) {
        return BigDecimal.valueOf(invoice.netAmount()).equals(invoice.transactionList().stream().map(transaction -> BigDecimal.valueOf(transaction.netTransactionAmount())).reduce(BigDecimal.ZERO, BigDecimal::add))
                && BigDecimal.valueOf(invoice.gstAmount()).equals(invoice.transactionList().stream().map(transaction -> BigDecimal.valueOf(transaction.gstAmount())).reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    private InvoiceEntity covertToInvoiceEntity(final Invoice invoice) {
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

    private Invoice convertToInvoice(final InvoiceEntity invoiceEntity) {
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
                                    .billingPeriodStart(DateConverter.convertDateToString(transactionEntity.getBillingPeriodStart()))
                                    .billingPeriodEnd(DateConverter.convertDateToString(transactionEntity.getBillingPeriodEnd()))
                                    .netTransactionAmount(transactionEntity.getNetTransactionAmount())
                                    .gstAmount(transactionEntity.getGstAmount())
                                    .build();
                        }
                ).toList())
                .build();
    }
}
