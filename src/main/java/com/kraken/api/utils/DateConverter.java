package com.kraken.api.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DateConverter {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private DateConverter() {

    }

    public static String convertDateToString(final LocalDate date) {
        if (date != null) {
            return date.format(DATE_FORMATTER);
        }
        return null;
    }

    public static LocalDate convertStringToDate(final String dateStr) {
        if (!dateStr.isBlank()) {
            return LocalDateTime.parse(dateStr, DATE_TIME_FORMATTER).toLocalDate();
        }
        return null;
    }
}
