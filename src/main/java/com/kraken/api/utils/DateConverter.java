package com.kraken.api.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateConverter {

    private static final DateTimeFormatter dateTimeformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String convertDateToString (LocalDate date) {
        if (date != null) {
            return date.format(dateFormatter);
        }
        return null;
    }

    public static LocalDate convertStringToDate(String dateStr) {
        if (!dateStr.isBlank()) {
            return LocalDateTime.parse(dateStr, dateTimeformatter).toLocalDate();
        }
        return null;
    }
}
