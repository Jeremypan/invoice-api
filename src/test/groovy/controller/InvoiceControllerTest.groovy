package controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.kraken.api.InvoiceApplication
import com.kraken.api.exception.model.Error
import com.kraken.api.model.Invoice
import com.kraken.api.model.Transaction
import com.kraken.api.service.InvoiceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import spock.lang.Shared
import spock.lang.Specification

import static com.kraken.api.constant.InvoiceApiConstants.AUTHORIZATION

@SpringBootTest(classes = InvoiceApplication.class)
@AutoConfigureMockMvc
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
    String BILLING_STRAT = "2006-01-01 00:00:00.000"
    @Shared
    String BILLING_END = "2006-03-17 00:00:00.000"
    @Shared
    Double NET_TRXN_AMT = 32.00
    @Shared
    Double TRXN_GST_AMOUNT = 3.20


    @MockitoBean
    private InvoiceService invoiceService

    @Autowired
    MockMvc mockMvc

    @Autowired
    ObjectMapper objectMapper

    def 'Given a invoice to be created and expect 201 response returned'() {
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
                .build()
        and: "mock invoice service and mock request"
        invoiceService.createInvoice(_ as Invoice) >> {}
        def res = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/invoice")
                .header(AUTHORIZATION, AUTHORIZATION_VALUE)
                .content(objectMapper.writeValueAsString(invoice))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn()

        then: "expect status is returned"
        res.getResponse().getStatus() == 201
    }

    def 'Given a invalid invoice to be created and expect 400 response returned in #scenarion'() {
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
                .build()
        and: "mock invoice service and mock request"
        invoiceService.createInvoice(_ as Invoice) >> {}
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
            it.error() == errorInput
        }

        where: "have different scenarios"
        scenarion          | invoiceIdInput | invoiceNumberInput | grossAmountInput | gstAmountInput | netAmountInput | receiptDateInput | paymentDueDateInput | totalNumTrxnInput || errorInput
        "InvalidInvoiceId" | ""             | INVOICE_NUMBER     | GROSS_AMOUNT     | GST_AMOUNT     | NET_AMOUNT     | RECEIPT_DATE     | PAYMENT_DUE_DATE    | TOTAL_NUM_TRXN    || "invoiceId must not be blank"
        "InvalidInvNumber" | INVOICE_ID     | ""                 | GROSS_AMOUNT     | GST_AMOUNT     | NET_AMOUNT     | RECEIPT_DATE     | PAYMENT_DUE_DATE    | TOTAL_NUM_TRXN    || "invoiceNumber must not be blank"
        "InvalidGrossAmt"  | INVOICE_ID     | INVOICE_NUMBER     | null             | GST_AMOUNT     | NET_AMOUNT     | RECEIPT_DATE     | PAYMENT_DUE_DATE    | TOTAL_NUM_TRXN    || "grossAmount must not be empty"
        "InvalidGstAmt"    | INVOICE_ID     | INVOICE_NUMBER     | GROSS_AMOUNT     | null           | NET_AMOUNT     | RECEIPT_DATE     | PAYMENT_DUE_DATE    | TOTAL_NUM_TRXN    || "gstAmount must not be empty"
        "InvalidNetAmt"    | INVOICE_ID     | INVOICE_NUMBER     | GROSS_AMOUNT     | GST_AMOUNT     | null           | RECEIPT_DATE     | PAYMENT_DUE_DATE    | TOTAL_NUM_TRXN    || "netAmount must not be empty"
        "InvalidRecDate1"  | INVOICE_ID     | INVOICE_NUMBER     | GROSS_AMOUNT     | GST_AMOUNT     | NET_AMOUNT     | ""               | PAYMENT_DUE_DATE    | TOTAL_NUM_TRXN    || "receiptDate must not be blank"
        "InvalidRecDate2"  | INVOICE_ID     | INVOICE_NUMBER     | GROSS_AMOUNT     | GST_AMOUNT     | NET_AMOUNT     | "01-01-2025"     | PAYMENT_DUE_DATE    | TOTAL_NUM_TRXN    || "Invalid Date Time Format"
        "InvalidPmtDue1"   | INVOICE_ID     | INVOICE_NUMBER     | GROSS_AMOUNT     | GST_AMOUNT     | NET_AMOUNT     | RECEIPT_DATE     | null                | TOTAL_NUM_TRXN    || "paymentDueDate must not be blank"
        "InvalidPmtDue2"   | INVOICE_ID     | INVOICE_NUMBER     | GROSS_AMOUNT     | GST_AMOUNT     | NET_AMOUNT     | RECEIPT_DATE     | "01-01-2025"        | TOTAL_NUM_TRXN    || "Invalid Date Time Format"
        "InvalidNumTrxn"   | INVOICE_ID     | INVOICE_NUMBER     | GROSS_AMOUNT     | GST_AMOUNT     | NET_AMOUNT     | RECEIPT_DATE     | PAYMENT_DUE_DATE    | null              || "totalNumTrxn must not be empty"
    }

    def 'Given a invoice with transactions to be created and expect 400 response returned in #scenarion'() {
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
                ))
                .build()
        and: "mock invoice service and mock request"
        invoiceService.createInvoice(_ as Invoice) >> {}
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
            it.error() == errorInput
        }

        where: "have different scenario"
        scenarion              | trxnInput | dateReceivedInput | trxnDateInput | billingPeriodStartInput | billingPeriodEndInput | netTrxnAmtInput | gstAmtInput     || errorInput
        "InvalidTrxn"          | ""        | DATE_RECEIVED     | TRXN_DATE     | BILLING_STRAT           | BILLING_END           | NET_TRXN_AMT    | TRXN_GST_AMOUNT || "transactionId must not be blank"
        "InvalidDateReceived1" | TRXN_ID   | ""                | TRXN_DATE     | BILLING_STRAT           | BILLING_END           | NET_TRXN_AMT    | TRXN_GST_AMOUNT || "dateReceived must not be blank"
        "InvalidDateReceived2" | TRXN_ID   | "01-01-2025"      | TRXN_DATE     | BILLING_STRAT           | BILLING_END           | NET_TRXN_AMT    | TRXN_GST_AMOUNT || "Invalid Date Time Format"
        "InvalidTrxnDate1"     | TRXN_ID   | DATE_RECEIVED     | null          | BILLING_STRAT           | BILLING_END           | NET_TRXN_AMT    | TRXN_GST_AMOUNT || "transactionDate must not be blank"
        "InvalidTrxnDate2"     | TRXN_ID   | DATE_RECEIVED     | "01-01-2025"  | BILLING_STRAT           | BILLING_END           | NET_TRXN_AMT    | TRXN_GST_AMOUNT || "Invalid Date Time Format"
        "InvalidBillingStart"  | TRXN_ID   | DATE_RECEIVED     | TRXN_DATE     | "01-01-2025"            | BILLING_END           | NET_TRXN_AMT    | TRXN_GST_AMOUNT || "Invalid Date Time Format"
        "InvalidBillingEnd"    | TRXN_ID   | DATE_RECEIVED     | TRXN_DATE     | BILLING_STRAT           | "01-01-2025"          | NET_TRXN_AMT    | TRXN_GST_AMOUNT || "Invalid Date Time Format"
        "InvalidNetTrxnAmt"    | TRXN_ID   | DATE_RECEIVED     | TRXN_DATE     | BILLING_STRAT           | BILLING_END           | null            | TRXN_GST_AMOUNT || "netTrxnAmount must not be empty"
        "InvalidGstAmt"        | TRXN_ID   | DATE_RECEIVED     | TRXN_DATE     | BILLING_STRAT           | BILLING_END           | NET_TRXN_AMT    | null            || "gstAmount must not be empty"
    }

    def 'Given a request to get All Invoice and expect 200 response returned' () {

    }
}
