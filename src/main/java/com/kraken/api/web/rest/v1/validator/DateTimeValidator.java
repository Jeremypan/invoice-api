package com.kraken.api.web.rest.v1.validator;

import com.kraken.api.web.rest.v1.validator.annotations.ValidDateTime;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateTimeValidator implements ConstraintValidator<ValidDateTime, String> {

    private String pattern;

    @Override
    public void initialize(ValidDateTime constraintAnnotation) {
        this.pattern = constraintAnnotation.pattern();
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        try {
            if(value==null || StringUtils.isBlank(value)) {
                return true;
            }
            LocalDateTime.parse(value, DateTimeFormatter.ofPattern(this.pattern));
            return true;
        } catch (DateTimeParseException exception) {
            return false;
        }
    }
}
