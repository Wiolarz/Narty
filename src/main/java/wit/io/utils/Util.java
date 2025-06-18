package wit.io.utils;

import exceptions.SkiAppException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

}
