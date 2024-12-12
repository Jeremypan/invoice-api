package com.kraken.api.web.rest.v1.validator.annotations;

import com.kraken.api.web.rest.v1.validator.DateTimeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

@Documented
@Constraint(validatedBy = DateTimeValidator.class)
@Target({FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDateTime {

    String message() default "Invalid Date Time Format";

    String pattern() default "yyyy-MM-dd HH:mm:ss.SSS";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
