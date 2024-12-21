package com.kraken.api.service

import com.kraken.api.exception.InvoiceServiceException
import com.kraken.api.model.Invoice
import com.kraken.api.model.Status
import com.kraken.api.model.Transaction
import com.kraken.api.model.entity.InvoiceEntity
import com.kraken.api.model.entity.TransactionEntity
import com.kraken.api.repository.InvoiceRepository
import com.kraken.api.utils.DateConverter
import org.spockframework.spring.SpringBean
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class InvoiceServiceImplTest extends Specification {

    @Shared
    String INVOICE_ID = "31620"
    @Shared
    String INVOICE_NUMBER = "588008550"
    @Shared
    Double GROSS_AMOUNT = 40.56
    @Shared
    Double GST_AMOUNT = 3.69
    @Shared
    Double NET_AMOUNT = 36.87
    @Shared
    String RECEIPT_DATE = "2007-12-12 18:30:52.000"
    @Shared
    String PAYMENT_DUE_DATE = "2006-04-28 00:00:00.000"
    @Shared
    Integer TOTAL_NUM_TRXN = 1

    @Shared
    String TRXN_ID = "728441"
    @Shared
    String DATE_RECEIVED = "2007-12-12 00:00:00.000"
    @Shared
    String TRXN_DATE = "2006-03-17 00:00:00.000"
    @Shared
    String BILLING_START = "2006-01-01 00:00:00.000"
    @Shared
    String BILLING_END = "2006-03-17 00:00:00.000"
    @Shared
    Double NET_TRXN_AMT = 32.00
    @Shared
    Double TRXN_GST_AMOUNT = 3.20

    @Shared
    String INVALID_REASON_TRXN_LINES = "The number of lines does not match the number of transactions"

    @Shared
    String INVALID_REASON_TRXN_AMT = "The total value of transaction lines does not add up to the invoice total"

    @SpringBean
    InvoiceRepository invoiceRepository

    @Subject
    InvoiceServiceImpl invoiceService


    def setup() {
        invoiceRepository = Stub()
        invoiceService = new InvoiceServiceImpl(invoiceRepository)
    }

    def "testCreateInvoiceSuccessful"() {
        when: "call create Invoice Service"
        invoiceService.createInvoice(buildStandardInvoiceWithTransaction())

        then: "no throw exception"
        noExceptionThrown()
    }

    def "testCreateInvoiceUnsuccessful for #scenario"() {
        given: "mock invoice repo exception"
        invoiceRepository.saveAndFlush(_ as InvoiceEntity) >> {
            throw exceptionInput
        }

        when: "call create Invoice Service"
        invoiceService.createInvoice(buildStandardInvoiceWithTransaction())

        then: "throw exception"
        def ex = thrown(InvoiceServiceException)
        with(ex) {
            ex.getHttpStatus() == statusOutput
            ex.getCode() == codeOutput
        }
        where: "different input and output on various scenarios"
        scenario                | exceptionInput                                                  || statusOutput                     | codeOutput
        "Bad Request"           | new DataIntegrityViolationException("invoice_pk duplicate key") || HttpStatus.BAD_REQUEST           | HttpStatusCode.valueOf(400)
        "Internal Server Error" | new RuntimeException()                                          || HttpStatus.INTERNAL_SERVER_ERROR | HttpStatusCode.valueOf(500)
    }

    def "getInvoiceSuccessful"() {
        when: "call get Invoice Service"
        def res = invoiceService.getInvoice(INVOICE_ID)

        then:
        1 * invoiceRepository.getInvoiceEntityByInvoiceId(_ as String) >> Optional.of(buildStandardInvoiceEntity())
        noExceptionThrown()
        res == buildStandardResponseInvoiceWithTransaction()

    }

    def "getInvoiceUnSuccessful"() {
        when: "call get Invoice Service"
        invoiceService.getInvoice(INVOICE_ID)

        then:
        1 * invoiceRepository.getInvoiceEntityByInvoiceId(_ as String) >> Optional.empty()
        def ex = thrown(InvoiceServiceException)
        ex.getMessage() == "Unable to find Invoice By Id=%s".formatted(INVOICE_ID)
    }

    def "getAllInvoiceSuccessful"() {
        when: "call get Invoice Service"
        def invoices = invoiceService.getAllInvoice(0, 1)
        then:
        noExceptionThrown()
        1 * invoiceRepository.findAll(_ as Pageable) >> new PageImpl<InvoiceEntity>([buildStandardInvoiceEntity()])
        invoices.size() == 1
        invoices.getFirst() == buildStandardResponseInvoiceWithTransaction()
    }

    def "validateInvoiceStatusSuccessful in #scenario"() {
        when: "call get validateInvoiceStatus"
        def invoiceStatus = invoiceService.validateInvoiceStatus(INVOICE_ID)
        then:
        1 * invoiceRepository.getInvoiceEntityByInvoiceId(INVOICE_ID) >> Optional.of(invoiceEntityOutput)
        noExceptionThrown()
        with(invoiceStatus) {
            if (it.status() == Status.INVALID) {
                it.reason() == reasonOutput
            }
            it.status() == statusOutput
        }
        where: "various scenario"
        scenario                | invoiceEntityOutput                                  || reasonOutput              | statusOutput
        "ValidInvoiceStatus"    | buildInvoiceEntity(TRXN_GST_AMOUNT, NET_TRXN_AMT, 1) || null                      | Status.VALID
        "InvalidInvoiceStatus1" | buildInvoiceEntity(TRXN_GST_AMOUNT, NET_TRXN_AMT, 2) || INVALID_REASON_TRXN_LINES | Status.INVALID
        "InvalidInvoiceStatus2" | buildStandardInvoiceEntity()                         || INVALID_REASON_TRXN_AMT   | Status.INVALID

    }


    def buildStandardResponseInvoiceWithTransaction() {
        return Invoice.builder()
                .invoiceId(INVOICE_ID)
                .invoiceNumber(INVOICE_NUMBER)
                .grossAmount(GROSS_AMOUNT)
                .gstAmount(GST_AMOUNT)
                .netAmount(NET_AMOUNT)
                .receiptDate(localDateTimeParser(RECEIPT_DATE))
                .paymentDueDate(localDateTimeParser(PAYMENT_DUE_DATE))
                .totalNumTrxn(TOTAL_NUM_TRXN)
                .transactionList(List.of(
                        buildStandardResponseTransaction()
                )).build()
    }

    def buildStandardResponseTransaction() {
        return Transaction.builder()
                .transactionId(TRXN_ID)
                .dateReceived(localDateTimeParser(DATE_RECEIVED))
                .transactionDate(localDateTimeParser(TRXN_DATE))
                .billingPeriodStart(localDateTimeParser(BILLING_START))
                .billingPeriodEnd(localDateTimeParser(BILLING_END))
                .netTransactionAmount(NET_TRXN_AMT)
                .gstAmount(TRXN_GST_AMOUNT)
                .build()
    }

    def localDateTimeParser(String dateStr) {
        return LocalDateTime
                .parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")).toLocalDate().toString()

    }

    def buildStandardInvoiceEntity() {
        InvoiceEntity invoiceEntity = new InvoiceEntity()
        invoiceEntity.setInvoiceId(INVOICE_ID)
        invoiceEntity.setInvoiceNum(INVOICE_NUMBER)
        invoiceEntity.setGrossAmount(GROSS_AMOUNT)
        invoiceEntity.setGstAmount(GST_AMOUNT)
        invoiceEntity.setNetAmount(NET_AMOUNT)
        invoiceEntity.setReceiptDate(DateConverter.convertStringToDate(RECEIPT_DATE))
        invoiceEntity.setPaymentDueDate(DateConverter.convertStringToDate(PAYMENT_DUE_DATE))
        invoiceEntity.setTotalNumberTransaction(TOTAL_NUM_TRXN)
        invoiceEntity.setTransactionEntities(List.of(buildStandardTransactionEntity(invoiceEntity)))
        return invoiceEntity
    }

    def buildInvoiceEntity(Double gstAmount, Double netAmount, Integer noOfTrxn) {
        InvoiceEntity invoiceEntity = new InvoiceEntity()
        invoiceEntity.setInvoiceId(INVOICE_ID)
        invoiceEntity.setInvoiceNum(INVOICE_NUMBER)
        invoiceEntity.setGrossAmount(GROSS_AMOUNT)
        invoiceEntity.setGstAmount(gstAmount)
        invoiceEntity.setNetAmount(netAmount)
        invoiceEntity.setReceiptDate(DateConverter.convertStringToDate(RECEIPT_DATE))
        invoiceEntity.setPaymentDueDate(DateConverter.convertStringToDate(PAYMENT_DUE_DATE))
        invoiceEntity.setTotalNumberTransaction(noOfTrxn)
        invoiceEntity.setTransactionEntities(List.of(buildStandardTransactionEntity(invoiceEntity)))
        return invoiceEntity
    }

    def buildStandardTransactionEntity(InvoiceEntity invoiceEntity) {
        TransactionEntity transactionEntity = new TransactionEntity()
        transactionEntity.setInvoice(invoiceEntity)
        transactionEntity.setInvoiceNumber(invoiceEntity.getInvoiceNum())
        transactionEntity.setTransactionId(TRXN_ID)
        transactionEntity.setTransactionDate(DateConverter.convertStringToDate(TRXN_DATE))
        transactionEntity.setDateReceived(DateConverter.convertStringToDate(DATE_RECEIVED))
        transactionEntity.setBillingPeriodStart(DateConverter.convertStringToDate(BILLING_START))
        transactionEntity.setBillingPeriodEnd(DateConverter.convertStringToDate(BILLING_END))
        transactionEntity.setNetTransactionAmount(NET_TRXN_AMT)
        transactionEntity.setGstAmount(TRXN_GST_AMOUNT)
        return transactionEntity
    }

    def buildStandardInvoiceWithTransaction() {
        return Invoice.builder()
                .invoiceId(INVOICE_ID)
                .invoiceNumber(INVOICE_NUMBER)
                .grossAmount(GROSS_AMOUNT)
                .gstAmount(GST_AMOUNT)
                .netAmount(NET_AMOUNT)
                .receiptDate(RECEIPT_DATE)
                .paymentDueDate(PAYMENT_DUE_DATE)
                .totalNumTrxn(TOTAL_NUM_TRXN)
                .transactionList(List.of(
                        buildStandardTransaction()
                )).build()
    }

    def buildStandardTransaction() {
        return Transaction.builder()
                .transactionId(TRXN_ID)
                .dateReceived(DATE_RECEIVED)
                .transactionDate(TRXN_DATE)
                .billingPeriodStart(BILLING_START)
                .billingPeriodEnd(BILLING_END)
                .netTransactionAmount(NET_TRXN_AMT)
                .gstAmount(TRXN_GST_AMOUNT)
                .build()
    }
}
