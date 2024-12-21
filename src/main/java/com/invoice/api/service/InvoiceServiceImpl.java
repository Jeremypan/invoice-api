package com.invoice.api.service;

import com.invoice.api.exception.InvoiceServiceException;
import com.invoice.api.model.Invoice;
import com.invoice.api.model.InvoiceStatus;
import com.invoice.api.model.Status;
import com.invoice.api.model.Transaction;
import com.invoice.api.model.entity.InvoiceEntity;
import com.invoice.api.model.entity.TransactionEntity;
import com.invoice.api.repository.InvoiceRepository;
import com.invoice.api.utils.DateConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;

    @Override
    public void createInvoice(final Invoice invoice) {
        try {
            invoiceRepository.saveAndFlush(covertToInvoiceEntity(invoice));
        } catch (DataIntegrityViolationException exception) {
            logErrorMessageForCreatingInvoice(exception);
            if (exception.getMessage().contains("duplicate key")
                    && (exception.getMessage().contains("invoice_pk")
                    || exception.getMessage().contains("invoice_number_unique"))) {
                throw new InvoiceServiceException(HttpStatus.BAD_REQUEST, HttpStatusCode.valueOf(400),
                        "Unable to create invoice due to the duplicate record-invoice");
            }
            if (exception.getMessage().contains("duplicate key")
                    && exception.getMessage().contains("transaction_pk")) {
                throw new InvoiceServiceException(HttpStatus.BAD_REQUEST, HttpStatusCode.valueOf(400),
                        "Unable to create invoice due to the duplicate record-transaction");
            }
            throw new InvoiceServiceException(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatusCode.valueOf(500),
                    "Unable to create invoice");
        } catch (Exception exception) {
            logErrorMessageForCreatingInvoice(exception);
            throw new InvoiceServiceException(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatusCode.valueOf(500),
                    "Unable to create invoice");
        }
    }

    @Override
    public Invoice getInvoice(final String invoiceId) {
        final var invoiceEntity = invoiceRepository.getInvoiceEntityByInvoiceId(invoiceId);
        if (invoiceEntity.isPresent()) {
            return convertToInvoice(invoiceEntity.get());
        } else {
            log.error("There is no invoice with id={}", invoiceId);
            throw new InvoiceServiceException(HttpStatus.BAD_REQUEST, HttpStatusCode.valueOf(400),
                    "Unable to find Invoice By Id=%s".formatted(invoiceId));
        }
    }

    @Override
    public List<Invoice> getAllInvoice(final Integer pageNo, final Integer pageSize) {
        Pageable pageable = Pageable.ofSize(pageSize).withPage(pageNo);
        log.info("getting all invoice");
        Page<InvoiceEntity> invoiceEntities = invoiceRepository.findAll(pageable);
        log.info("getting all invoice={}", invoiceEntities);
        return invoiceEntities.stream().map(this::convertToInvoice).toList();
    }

    @Override
    public InvoiceStatus validateInvoiceStatus(final String invoiceId) {
        final var invoice = getInvoice(invoiceId);

        if (!isValidNumberOfTransaction(invoice)) {
            return InvoiceStatus.builder()
                    .status(Status.INVALID)
                    .reason("The number of lines does not match the number of transactions").build();
        }

        if (!isValidTotalTransactionAmount(invoice)) {
            return InvoiceStatus.builder()
                    .status(Status.INVALID)
                    .reason("The total value of transactions does not add up to the invoice total").build();
        }

        return InvoiceStatus.builder().status(Status.VALID).build();
    }

    private boolean isValidNumberOfTransaction(final Invoice invoice) {
        return invoice.totalNumTrxn() == invoice.transactionList().size();
    }

    private boolean isValidTotalTransactionAmount(final Invoice invoice) {
        return invoice.netAmount().setScale(2, RoundingMode.HALF_UP).equals(
                invoice.transactionList().stream()
                        .map(Transaction::netTransactionAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP))
                && invoice.gstAmount().setScale(2, RoundingMode.HALF_UP)
                .equals(invoice.transactionList().stream().map(Transaction::gstAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP))
                && invoice.grossAmount().setScale(2, RoundingMode.HALF_UP)
                .equals(invoice.transactionList().stream()
                        .map(transaction -> transaction.gstAmount().add(transaction.netTransactionAmount()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP));


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

    private void logErrorMessageForCreatingInvoice(final Exception exception) {
        log.error("Unable to create invoice due to the exception={}", exception.getMessage());
    }
}
