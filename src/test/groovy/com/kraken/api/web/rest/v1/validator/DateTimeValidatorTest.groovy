package com.kraken.api.web.rest.v1.validator

import spock.lang.Specification
import spock.lang.Subject

class DateTimeValidatorTest extends Specification {

    @Subject
    DateTimeValidator dateTimeValidator

    def setup() {
        dateTimeValidator = new DateTimeValidator();
    }

}
