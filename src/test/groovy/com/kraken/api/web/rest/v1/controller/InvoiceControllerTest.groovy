package com.kraken.api.web.rest.v1.controller

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.kraken.api.exception.model.Error
import com.kraken.api.model.Invoice
import com.kraken.api.model.InvoiceStatus
import com.kraken.api.model.Status
import com.kraken.api.model.Transaction
import com.kraken.api.service.InvoiceService
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import spock.lang.Shared
import spock.lang.Specification

import static com.kraken.api.constant.InvoiceApiConstants.AUTHORIZATION

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class InvoiceControllerTest extends Specification {

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
    String AUTHORIZATION_VALUE = "Basic dXNlcjpwYXNzd29yZA=="

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
    InvoiceService invoiceService=Mock(InvoiceService.class);

    @Autowired
    MockMvc mockMvc

    @Autowired
    ObjectMapper objectMapper

    def 'Given a invoice #scenario to be created and expect 201 response returned'() {
        given:"mockInvoiceService"
        invoiceService.createInvoice(_ as Invoice) >> {}

        when: "build a invoice create request"
        Invoice invoice = buildStandardInvoiceWithTransaction()
        and: "mock request"
        def res = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/invoice")
                .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                .content(objectMapper.writeValueAsString(invoice))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn()

        then: "expect status is returned"
        res.getResponse().getStatus() == 201
    }

    def 'Given a invalid invoice to be created and expect 400 response returned in #scenario'() {
        when: "build a invoice create request"
        Invoice invoice = Invoice.builder()
                .invoiceId(invoiceIdInput)
                .invoiceNumber(invoiceNumberInput)
                .grossAmount(grossAmountInput)
                .gstAmount(gstAmountInput)
                .netAmount(netAmountInput)
                .receiptDate(receiptDateInput)
                .paymentDueDate(paymentDueDateInput)
                .totalNumTrxn(totalNumTrxnInput)
                .transactionList(transactionListInput)
                .build()

        def res = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/invoice")
                .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                .content(objectMapper.writeValueAsString(invoice))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn()

        def error = objectMapper.readValue(res.getResponse().getContentAsString(), Error.class)
        then: "expect status is returned"
        res.getResponse().getStatus() == 400
        with(error) {
            it.path() == "/api/v1/invoice"
            it.status() == String.valueOf(HttpStatus.BAD_REQUEST.value())
            it.error() == HttpStatus.BAD_REQUEST.getReasonPhrase()
            it.detail() == errorInput
        }

        where: "have different scenarios"
        scenario           | invoiceIdInput | invoiceNumberInput | grossAmountInput | gstAmountInput | netAmountInput | receiptDateInput | paymentDueDateInput | totalNumTrxnInput | transactionListInput                || errorInput
        "InvalidInvoiceId" | ""             | INVOICE_NUMBER     | GROSS_AMOUNT     | GST_AMOUNT     | NET_AMOUNT     | RECEIPT_DATE     | PAYMENT_DUE_DATE    | TOTAL_NUM_TRXN    | List.of(buildStandardTransaction()) || "invoiceId must not be blank"
        "InvalidInvNumber" | INVOICE_ID     | ""                 | GROSS_AMOUNT     | GST_AMOUNT     | NET_AMOUNT     | RECEIPT_DATE     | PAYMENT_DUE_DATE    | TOTAL_NUM_TRXN    | List.of(buildStandardTransaction()) || "invoiceNumber must not be blank"
        "InvalidGrossAmt"  | INVOICE_ID     | INVOICE_NUMBER     | null             | GST_AMOUNT     | NET_AMOUNT     | RECEIPT_DATE     | PAYMENT_DUE_DATE    | TOTAL_NUM_TRXN    | List.of(buildStandardTransaction()) || "grossAmount must not be empty"
        "InvalidGstAmt"    | INVOICE_ID     | INVOICE_NUMBER     | GROSS_AMOUNT     | null           | NET_AMOUNT     | RECEIPT_DATE     | PAYMENT_DUE_DATE    | TOTAL_NUM_TRXN    | List.of(buildStandardTransaction()) || "gstAmount must not be empty"
        "InvalidNetAmt"    | INVOICE_ID     | INVOICE_NUMBER     | GROSS_AMOUNT     | GST_AMOUNT     | null           | RECEIPT_DATE     | PAYMENT_DUE_DATE    | TOTAL_NUM_TRXN    | List.of(buildStandardTransaction()) || "netAmount must not be empty"
        "InvalidRecDate1"  | INVOICE_ID     | INVOICE_NUMBER     | GROSS_AMOUNT     | GST_AMOUNT     | NET_AMOUNT     | ""               | PAYMENT_DUE_DATE    | TOTAL_NUM_TRXN    | List.of(buildStandardTransaction()) || "receiptDate must not be blank"
        "InvalidRecDate2"  | INVOICE_ID     | INVOICE_NUMBER     | GROSS_AMOUNT     | GST_AMOUNT     | NET_AMOUNT     | "01-01-2025"     | PAYMENT_DUE_DATE    | TOTAL_NUM_TRXN    | List.of(buildStandardTransaction()) || "Invalid Date Time Format"
        "InvalidPmtDue1"   | INVOICE_ID     | INVOICE_NUMBER     | GROSS_AMOUNT     | GST_AMOUNT     | NET_AMOUNT     | RECEIPT_DATE     | null                | TOTAL_NUM_TRXN    | List.of(buildStandardTransaction()) || "paymentDueDate must not be blank"
        "InvalidPmtDue2"   | INVOICE_ID     | INVOICE_NUMBER     | GROSS_AMOUNT     | GST_AMOUNT     | NET_AMOUNT     | RECEIPT_DATE     | "01-01-2025"        | TOTAL_NUM_TRXN    | List.of(buildStandardTransaction()) || "Invalid Date Time Format"
        "InvalidNumTrxn"   | INVOICE_ID     | INVOICE_NUMBER     | GROSS_AMOUNT     | GST_AMOUNT     | NET_AMOUNT     | RECEIPT_DATE     | PAYMENT_DUE_DATE    | null              | List.of(buildStandardTransaction()) || "totalNumTrxn must not be empty"
        "InvalidNumTrxn"   | INVOICE_ID     | INVOICE_NUMBER     | GROSS_AMOUNT     | GST_AMOUNT     | NET_AMOUNT     | RECEIPT_DATE     | PAYMENT_DUE_DATE    | TOTAL_NUM_TRXN    | null                                || "transactionList must not be null"
    }

    def 'Given a invoice with transactions to be created and expect 400 response returned in #scenario'() {
        when: "build a invoice create request"
        Invoice invoice = Invoice.builder()
                .invoiceId(INVOICE_ID)
                .invoiceNumber(INVOICE_NUMBER)
                .grossAmount(GROSS_AMOUNT)
                .gstAmount(GST_AMOUNT)
                .netAmount(NET_AMOUNT)
                .receiptDate(RECEIPT_DATE)
                .paymentDueDate(PAYMENT_DUE_DATE)
                .totalNumTrxn(TOTAL_NUM_TRXN)
                .transactionList(List.of(Transaction.builder()
                        .transactionId(trxnInput)
                        .dateReceived(dateReceivedInput)
                        .transactionDate(trxnDateInput)
                        .billingPeriodStart(billingPeriodStartInput)
                        .billingPeriodEnd(billingPeriodEndInput)
                        .netTransactionAmount(netTrxnAmtInput)
                        .gstAmount(gstAmtInput)
                        .build()
                )).build()

        def res = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/invoice")
                .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                .content(objectMapper.writeValueAsString(invoice))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn()

        def error = objectMapper.readValue(res.getResponse().getContentAsString(), Error.class)
        then: "expect status is returned"
        res.getResponse().getStatus() == 400
        with(error) {
            it.path() == "/api/v1/invoice"
            it.status() == String.valueOf(HttpStatus.BAD_REQUEST.value())
            it.error() == HttpStatus.BAD_REQUEST.getReasonPhrase()
            it.detail() == errorInput
        }

        where: "have different scenario"
        scenario | trxnInput | dateReceivedInput | trxnDateInput | billingPeriodStartInput | billingPeriodEndInput | netTrxnAmtInput | gstAmtInput || errorInput
        "InvalidTrxn"          | ""        | DATE_RECEIVED     | TRXN_DATE     | BILLING_START | BILLING_END  | NET_TRXN_AMT | TRXN_GST_AMOUNT || "transactionId must not be blank"
        "InvalidDateReceived1" | TRXN_ID   | ""                | TRXN_DATE     | BILLING_START | BILLING_END  | NET_TRXN_AMT | TRXN_GST_AMOUNT || "dateReceived must not be blank"
        "InvalidDateReceived2" | TRXN_ID   | "01-01-2025"      | TRXN_DATE     | BILLING_START | BILLING_END  | NET_TRXN_AMT | TRXN_GST_AMOUNT || "Invalid Date Time Format"
        "InvalidTrxnDate1"     | TRXN_ID   | DATE_RECEIVED     | null          | BILLING_START | BILLING_END  | NET_TRXN_AMT | TRXN_GST_AMOUNT || "transactionDate must not be blank"
        "InvalidTrxnDate2"     | TRXN_ID   | DATE_RECEIVED     | "01-01-2025"  | BILLING_START | BILLING_END  | NET_TRXN_AMT | TRXN_GST_AMOUNT || "Invalid Date Time Format"
        "InvalidBillingStart"  | TRXN_ID   | DATE_RECEIVED     | TRXN_DATE     | "01-01-2025"            | BILLING_END           | NET_TRXN_AMT    | TRXN_GST_AMOUNT || "Invalid Date Time Format"
        "InvalidBillingEnd"    | TRXN_ID   | DATE_RECEIVED     | TRXN_DATE     | BILLING_START | "01-01-2025" | NET_TRXN_AMT | TRXN_GST_AMOUNT || "Invalid Date Time Format"
        "InvalidNetTrxnAmt"    | TRXN_ID   | DATE_RECEIVED     | TRXN_DATE     | BILLING_START | BILLING_END  | null         | TRXN_GST_AMOUNT || "netTrxnAmount must not be empty"
        "InvalidGstAmt"        | TRXN_ID   | DATE_RECEIVED     | TRXN_DATE     | BILLING_START | BILLING_END  | NET_TRXN_AMT | null            || "gstAmount must not be empty"
    }

    def 'Given a request with Page params to get All Invoice and expect 200 response returned'() {
        given: "stub response for invoice service"

        List<Invoice> mockInvoices = [buildStandardInvoiceWithTransaction()]
        invoiceService.getAllInvoice(_ as Integer, _ as Integer) >> mockInvoices

        when: "build request"
        def res = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/invoice")
                .param("PageNo", "0")
                .param("pageSize", "1")
                .header(AUTHORIZATION, AUTHORIZATION_VALUE)).andReturn()


        def invoices = objectMapper.readValue(res.getResponse().getContentAsString(), new TypeReference<List<Invoice>>() {
        })

        then: "expect status is return"

        res.getResponse().getStatus() == 200
        invoices.size() == 1
    }

    def 'Given a request in #scenario to get All Invoice and expect 400 response returned'() {
        when: "build request"
        def res = mockMvc.perform(MockMvcRequestBuilders
                .get(urlInput)
                .header(AUTHORIZATION, AUTHORIZATION_VALUE)).andReturn()

        def error = objectMapper.readValue(res.getResponse().getContentAsString(), Error.class)
        then: "expect status is return"

        res.getResponse().getStatus() == 400
        with(error) {
            it.path() == "/api/v1/invoice"
            it.status() == String.valueOf(HttpStatus.BAD_REQUEST.value())
            it.error() == HttpStatus.BAD_REQUEST.getReasonPhrase()
            it.detail() == errorInput
        }

        where: "various scenario"

        scenario              | urlInput                               || errorInput
        "invalid pageNoInput" | "/api/v1/invoice?pageNo=-1&pageSize=1" || "pageNo must not be less than 0"
        "invalid pageSzInput" | "/api/v1/invoice?pageNo=0&pageSize=0"  || "pageSize must be bigger than 0"
    }

    def 'Given a request to get Invoice By Invoice Id and expect 200 response returned'() {
        given: "stub response for invoice service"

        invoiceService.getInvoice(_ as String) >> buildStandardInvoiceWithTransaction()

        when: "build request"
        def res = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/invoice/%s".formatted(INVOICE_ID))
                .header(AUTHORIZATION, AUTHORIZATION_VALUE)).andReturn()


        def invoice = objectMapper.readValue(res.getResponse().getContentAsString(), Invoice.class)

        then: "expect status is return"

        res.getResponse().getStatus() == 200
        with(invoice) {
            it.invoiceId() == INVOICE_ID
            it.invoiceNumber() == INVOICE_NUMBER
            it.grossAmount() == GROSS_AMOUNT
            it.gstAmount() == GST_AMOUNT
            it.netAmount() == NET_AMOUNT
            it.receiptDate() == RECEIPT_DATE
            it.paymentDueDate() == PAYMENT_DUE_DATE
            it.totalNumTrxn() == TOTAL_NUM_TRXN
            it.transactionList().size() == 1
            with(it.transactionList().getFirst()) {
                it.transactionId() == TRXN_ID
                it.dateReceived() == DATE_RECEIVED
                it.transactionDate() == TRXN_DATE
                it.billingPeriodStart() == BILLING_START
                it.billingPeriodEnd() == BILLING_END
                it.netTransactionAmount() == NET_TRXN_AMT
                it.gstAmount() == TRXN_GST_AMOUNT
            }
        }
    }

    def 'Given a request to validate status in #scenario and expect 200 response returned'() {
        given: "stub response for invoice service"

        invoiceService.validateInvoiceStatus(_ as String) >> invoiceStatusOutput

        when: "build request"
        def res = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/invoice/%s/status".formatted(INVOICE_ID))
                .header(AUTHORIZATION, AUTHORIZATION_VALUE)).andReturn()


        def invoiceStatus = objectMapper.readValue(res.getResponse().getContentAsString(), InvoiceStatus.class)

        then: "expect status is return"

        res.getResponse().getStatus() == 200
        with(invoiceStatus) {
            if (it.status() != Status.VALID) {
                it.reason() == reasonOutput
            }
            it.status() == statusOutput
        }

        where: "various scenarios"
        scenario           | invoiceStatusOutput                                         || reasonOutput              | statusOutput
        "Invalid Status 1" | buildInvoiceInvalidInvoiceStatus(INVALID_REASON_TRXN_LINES) || INVALID_REASON_TRXN_LINES | Status.INVALID
        "Invalid Status 2" | buildInvoiceInvalidInvoiceStatus(INVALID_REASON_TRXN_AMT)   || INVALID_REASON_TRXN_AMT   | Status.INVALID
        "Valid Status"     | buildInvoiceValidInvoiceStatus()                            || null                      | Status.VALID
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

    def buildInvoiceValidInvoiceStatus() {
        InvoiceStatus.builder().status(Status.VALID).build()
    }

    def buildInvoiceInvalidInvoiceStatus(String reason) {
        InvoiceStatus.builder().status(Status.INVALID).reason(reason).build()
    }
}
