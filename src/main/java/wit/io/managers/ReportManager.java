package wit.io.managers;

import wit.io.data.Ski;
import wit.io.data.SkiType;
import wit.io.data.Rent;
import wit.io.data.enums.RentStatus;
import wit.io.utils.Util;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Manages reporting functionality for ski rental operations, providing methods to track
 * available skis, rentals, and overdue equipment.
 */
public class ReportManager {
    private RentManager rentManager;
    private SkiManager skiManager;

    /**
     * Constructor for ReportManager
     * @param rentManager Manages rental operations and data
     * @param skiManager Manages ski inventory management
     */
    public ReportManager(RentManager rentManager, SkiManager skiManager) {
        if(Util.isAnyArgumentNull(rentManager, skiManager)) {
            throw new IllegalArgumentException("One or more of given arguments were null.");
        }
        this.rentManager = rentManager;
        this.skiManager = skiManager;
    }

    /**
     * Retrieves a collection of skis currently available for rental within the specified date range.
     * A ski is considered available if it is not currently rented out.
     * @param startDate Date from which to check availability, required
     * @return Set of available Ski objects
     */
    public LinkedHashSet<Ski> availableSkis(LocalDate startDate) {
        // wszystkie - na wypożyczeniu
        return getSelectedSkisDifference(rentedSkis(startDate));
    }

    /**
     * Returns a collection of overdue skis that have exceeded their rental period.
     * Skis are considered overdue when their status is OVERDUE.
     * @return Set of overdue Ski objects
     */
    public LinkedHashSet<Ski> overdueSkis() {
        var selectedRents = rentManager.getEntities()
                .stream()
                .filter(e -> e.getStatus() == RentStatus.OVERDUE);

        var selectedSkis = toSkiSet(selectedRents);
        return getSelectedSkis(selectedSkis);

    }

    /**
     * Identifies skis that are currently rented at given start date.
     * Includes both ACTIVE and OVERDUE rentals.
     * @param startDate Filter date for rentals (null defaults to current date)
     * @return Set of currently rented Ski objects
     */
    public LinkedHashSet<Ski> rentedSkis(LocalDate startDate) {
        // startDate now or before now
        LocalDate startDateFilter = (startDate == null) ? LocalDate.now() : startDate;
        var selectedRents = rentManager.getEntities()
                .stream()
                .filter(e -> e.getStatus() == RentStatus.ACTIVE || e.getStatus() == RentStatus.OVERDUE)
                .filter(e -> !e.getStartDate().isAfter(startDateFilter));

        var selectedSkis = toSkiSet(selectedRents);
        return getSelectedSkis(selectedSkis);
        // TODO: test if stream is empty at any stage
    }

    /**
     * Creates a filtered set containing only the skis present in both the full inventory
     * and the selected skis collection.
     * @param selectedSkis Collection of skis to compare against
     * @return Filtered set of matching Ski objects
     */
    private LinkedHashSet<Ski> getSelectedSkis(LinkedHashSet<Ski> selectedSkis) {
        var result = new LinkedHashSet<>(skiManager.getEntities());
        result.retainAll(selectedSkis);

        return result;
    }

    /**
     * Creates a set containing all skis except those in the selected collection.
     * Used to determine available skis by removing currently rented ones.
     * @param selectedSkis Collection of skis to exclude
     * @return Set of remaining Ski objects
     */
    private LinkedHashSet<Ski> getSelectedSkisDifference(LinkedHashSet<Ski> selectedSkis) {
        var result = new LinkedHashSet<>(skiManager.getEntities());
        result.removeAll(selectedSkis);

        return result;
    }

    /**
     * Converts a stream of Rent objects to a set of corresponding Ski objects.
     * Creates new Ski instances mapped from the rental records
     * @param s Stream of Rent objects to convert
     * @return Set of converted Ski objects
     */
    private LinkedHashSet<Ski> toSkiSet(Stream<Rent> s) {
        return  s.map(Rent::getSkiModel)
                .map(e -> new Ski(new SkiType("", ""), "", e, "", 0.0f))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
