package com.kraken.api.web.rest.v1.validator

import com.kraken.api.web.rest.v1.validator.annotations.ValidDateTime
import jakarta.validation.ConstraintValidatorContext
import spock.lang.Specification
import spock.lang.Subject

class DateTimeValidatorTest extends Specification {

    @Subject
    DateTimeValidator dateTimeValidator

    def setup() {
        dateTimeValidator = new DateTimeValidator()
    }

    def 'should valid value with #scenario'() {
        given: "mock params for isValid method"
        ConstraintValidatorContext context = Mock()
        ValidDateTime validDateTime = Stub(ValidDateTime.class)
        validDateTime.pattern() >> "yyyy-MM-dd HH:mm:ss.SSS"

        when: "call isValid method"
        dateTimeValidator.initialize(validDateTime)
        def res = dateTimeValidator.isValid(dateTimeInput, context)

        then: "expect output from isValid"
        noExceptionThrown()
        res == output

        where: "scenario for dateTimeInput"
        scenario                | dateTimeInput             || output
        "valid dateTimeInput"   | "2005-12-31 00:00:00.000" || true
        "null"                  | null                      || true
        "empty string"          | " "                       || true
        "Invalid dateTimeInput" | "01-01-2025"              || false
    }
}
