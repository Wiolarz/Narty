package wit.io.utils;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility class providing common helper methods for various operations
 * All methods are static
 */
public class Util {

    /**
     * Checks if any of the provided arguments are null.
     * @param objects collection of objects to check
     * @return true if any argument is null, false otherwise
     */
    public static boolean isAnyArgumentNull(Object... objects) {
        for (Object obj : objects) {
            if (obj == null) return true;
        }
        return false;
    }

    /**
     * Converts a LocalDate instance to a string using the format defined in Const.DateFormat.
     * @param date LocalDate instance to convert
     * @return Formatted string representation of the date
     */
    public static String dateToString(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Const.DateFormat);
        return formatter.format(date);
    }

    /**
     * Converts a string to a LocalDate instance using the format defined in Const.DateFormat.
     * Returns null if parsing fails instead of throwing an exception.
     * @param string String to parse into a date
     * @throws ParseException if date is not saved in format specified in Const
     * @return LocalDate object if parsing succeeds, null otherwise
     */
    public static LocalDate stringToDate(String string) throws ParseException {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Const.DateFormat);
            return LocalDate.parse(string, formatter);
        } catch (DateTimeParseException e) {
            return null; // frontend moment
        }
    }

    /**
     * Checks if the first string contains the second string (case-insensitive).
     * @param string1 The string to search in
     * @param string2 The string to search for
     * @return true if string1 contains string2 (case-insensitive), false otherwise
     */
    public static boolean containsString(String string1, String string2) {
        return string1.toLowerCase().contains(string2.toLowerCase());
    }

    /**
     * Checks if the first string starts with the second string (case-insensitive).
     * @param string1 The string to check
     * @param string2 The prefix to look for
     * @return true if string1 starts with string2 (case-insensitive), false otherwise
     */
    public static boolean startsWithString(String string1, String string2) {
        return string1.toLowerCase().startsWith(string2.toLowerCase());
    }

    /**
     * Validates a date range against current date and predefined constraints.
     * @param startDate The start date of the range
     * @param endDate The end date of the range
     * @param now Optional current date reference (null uses current date)
     * @return true if the date range is valid according to these rules:
     *         - Both dates must not be null
     *         - Start date must not be before current date
     *         - End date must not be more than 5 years from current date
     *         - Duration must not exceed 6 months
     *         - Start date must not be after end date
     */
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

    /**
     * Converts a UUID to its string representation.
     * @param uuid UUID to convert
     * @return String representation of the UUID
     */
    public static String uuidToString(UUID uuid) {
        return uuid.toString();
    }

    /**
     * Creates a UUID from a string
     * @param str String to convert to UUID
     * @return UUID created from the string's bytes
     */
    public static UUID stringToUUID(String str) {
        return UUID.nameUUIDFromBytes(str.getBytes());
    }



    /**
     * Compares two objects by their string representation.
     * This method is specifically designed for objects that have overriden equals() methods
     * that compare only their models, not the full model.
     * @param obj1 First object to compare
     * @param obj2 Second object to compare
     * @return true if both objects are null or their string representations are equal
     */
    public static boolean compareObjectsByStringValue(Object obj1, Object obj2) {
        if (obj1 == null && obj2 == null) {
            return true;
        }
        if (obj1 == null || obj2 == null) {
            return false;
        }
        return obj1.toString().equals(obj2.toString());
    }

    /**
     * Compares two lists of objects by converting them to sorted lists of strings.
     * This method is specifically designed for objects that have overriden equals() methods
     * that compare only their models, not the full model.
     * @param list1 First list to compare
     * @param list2 Second list to compare
     * @return true if the lists contain the same objects in any order
     */
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

    /**
     * Compares two sets of objects by converting them to sorted lists of strings.
     * This method is specifically designed for objects that have overriden equals() methods
     * that compare only their models, not the full model.
     * @param set1 First set to compare
     * @param set2 Second set to compare
     * @return true if the sets contain the same objects
     */
    public static boolean orderAndCompareSetsOfObjectsByStringValue(Set<?> set1, Set<?> set2) {
        return orderAndCompareListsOfObjectsByStringValue(new ArrayList<>(set1), new ArrayList<>(set2));
    }

    /**
     * Checks if a list contains an object by comparing their string representations.
     * This method is specifically designed for objects that have overriden equals() methods
     * compare only their models, not the full model
     * @param list List to search in
     * @param obj Object to search for
     * @return true if the list contains the object (string comparison), false otherwise
     */
    public static boolean isInListOfObjectsByString(List<?> list, Object obj) {
        if (list == null || list.isEmpty()) {
            return false;
        }

        return list.stream()
                .anyMatch(itemInList -> Objects.toString(itemInList).equals(obj.toString()));
    }

}
