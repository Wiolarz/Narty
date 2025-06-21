package wit.io.utils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class Util {
    public static boolean isAnyArgumentNull(Object... objects) {
        for (Object obj : objects) {
            if (obj == null) return true;
        }
        return false;
    }

    public static String dateToString(Date date) {
        DateFormat formatter = new SimpleDateFormat(Const.DateFormat);
        return formatter.format(date);
    }

    public static Date stringToDate(String string) throws ParseException {
        DateFormat formatter = new SimpleDateFormat(Const.DateFormat);
        return formatter.parse(string);
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

    public static boolean isDateRangeValid(Date startDate, Date endDate) {
        // validate date if
        // startDate >= now(),
        // endDate <= 5 years ahead
        // end-start to max 0.5 years
        // startDate <= endDate

        if(startDate == null || endDate == null) {
            return false;
        }

        Date now = new Date();

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(now);
        calendar.add(Calendar.YEAR, 5);
        Date fiveYearLimit = calendar.getTime();

        calendar.setTime(startDate);
        calendar.add(Calendar.MONTH, 6);
        Date sixMonthDurationLimit = calendar.getTime();

        return !startDate.before(now) &&
                !startDate.after(endDate) &&
                !endDate.after(fiveYearLimit) &&
                !endDate.after(sixMonthDurationLimit);
    }

}
