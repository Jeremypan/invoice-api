package com.kraken.api.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateConverter {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String convertDateToString (LocalDate date) {
        if (date != null) {
            return date.format(formatter);
        }
        return null;
    }

    public static LocalDate convertStringToDate(String dateStr) {
        if (!dateStr.isBlank()) {
            return LocalDate.parse(dateStr, formatter);
        }
        return null;
    }
}
