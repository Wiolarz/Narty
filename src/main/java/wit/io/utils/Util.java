package wit.io.utils;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Util {
    public static boolean isAnyArgumentNull(Object... objects) {
        for (Object obj : objects) {
            if (obj == null) return true;
        }
        return false;
    }

    public static String dateToString(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Const.DateFormat);
        return formatter.format(date);
    }

    public static LocalDate stringToDate(String string) throws ParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return LocalDate.parse(string, formatter);
    }

    // checks whether the first string contains the second string.
    // NOT CASE SENSITIVE
    public static boolean containsString(String string1, String string2) {
        return string1.toLowerCase().contains(string2.toLowerCase());
    }

    // checks if first string starts with the second string
    // NOT CASE SENSITIVE
    public static boolean startsWithString(String string1, String string2) {
        return string1.toLowerCase().startsWith(string2.toLowerCase());
    }


    public static boolean isDateRangeValid(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return false;
        }

        LocalDate now = LocalDate.now();
        if (startDate.isBefore(now)) {
            return false;
        }

        LocalDate fiveYearsFromNow = now.plusYears(5);
        if (endDate.isAfter(fiveYearsFromNow)) {
            return false;
        }

        long durationInMonths = ChronoUnit.MONTHS.between(startDate, endDate);
        if (durationInMonths > 6) {
            return false;
        }

        if (startDate.isAfter(endDate)) {
            return false;
        }

        return true;
    }

}
