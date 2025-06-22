package wit.io.utils;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

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


    public static boolean isDateRangeValid(LocalDate startDate, LocalDate endDate, LocalDate now) {
        if (startDate == null || endDate == null) {
            return false;
        }

//        now = (now == null) ? LocalDate.now() : now;
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

    public static String uuidToString(UUID uuid) {
        return uuid.toString();
    }

    public static UUID stringToUUID(String str) {
        return UUID.nameUUIDFromBytes(str.getBytes());
    }



    // you may be curious, why I'm using .toString here, the answer is:
    // our objects have custom .equals methods, thus if I were to compare the objects by themselves here
    // it would only compare their models.
    // the same is logic is used by orderAndCompareListsOfObjectsByStringValue()
    public static boolean compareObjectsByStringValue(Object obj1, Object obj2) {
        if (obj1 == null && obj2 == null) {
            return true;
        }
        if (obj1 == null || obj2 == null) {
            return false;
        }
        return obj1.toString().equals(obj2.toString());
    }

    public static boolean orderAndCompareListsOfObjectsByStringValue(List<?> list1, List<?> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }

        List<String> stringList1 = list1.stream()
                .map(Object::toString)
                .collect(Collectors.toList());

        List<String> stringList2 = list2.stream()
                .map(Object::toString)
                .collect(Collectors.toList());

        stringList1.sort(String::compareTo);
        stringList2.sort(String::compareTo);

        return stringList1.equals(stringList2);
    }

    public static boolean orderAndCompareSetsOfObjectsByStringValue(Set<?> set1, Set<?> set2) {
        return orderAndCompareListsOfObjectsByStringValue(new ArrayList<>(set1), new ArrayList<>(set2));
    }

    public static boolean isInListOfObjectsByString(List<?> list, Object obj) {
        if (list == null || list.isEmpty()) {
            return false;
        }

        return list.stream()
                .anyMatch(itemInList -> Objects.toString(itemInList).equals(obj.toString()));
    }

}
