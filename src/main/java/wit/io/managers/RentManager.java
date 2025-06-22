package wit.io.managers;

import wit.io.data.Client;
import wit.io.data.enums.RentStatus;
import wit.io.exceptions.*;
import wit.io.data.Rent;
import wit.io.utils.Util;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Manages operations related to {@link Rent} objects, extending the generic Manager.
 * This class handles Rent class interactions between the user and data.
 */
public class RentManager extends Manager<Rent> {
    /**
     * Local reference to "current" time, can be passed by a constructor, allows for testing of this class
     * without mocking Date.
     */
    private final LocalDate now;

    /**
     * Constructor for ClientManager.
     * @param filePath path to the file where rent data is/will be stored, required argument.
     * @throws ReadingException if the path is null.
     */
    public RentManager(String filePath) throws ReadingException, SkiAppException {
        this(filePath, LocalDate.now());
    }

    /**
     * Constructor for ClientManager.
     * @param filePath path to the file where client data is/will be stored, required argument.
     * @param now instance of LocalDate keeping the current time to perform validations later.
     * @throws ReadingException if the path is null.
     * @throws SkiAppException If a general application error occurs during initialization
     */
    public RentManager(String filePath, LocalDate now) throws ReadingException, SkiAppException {
        super(filePath);

        this.now = now;
        setOverdue(now);
        setFailed(now);
    }

    /**
     * Updates the status of RentStatus rentals to OVERDUE
     * if their planned end date is before the 'now' date.
     * The `updatedEndDate` for these rentals is also set to 'now'.
     * @param now The current date used to determine if a rental is overdue.
     */
    private void setOverdue(LocalDate now) {
        LinkedHashSet<Rent> updatedRents = new LinkedHashSet<>();
        var it = dataEntities.iterator();
        while(it.hasNext()) {
            Rent rent = it.next();
            if (rent.getStatus() == RentStatus.ACTIVE && rent.getEndDate().isBefore(now)) {
                Rent updatedRent = rent.setStatus(RentStatus.OVERDUE).setUpdatedEndDate(now);
                updatedRents.add(updatedRent);
                it.remove();
            }
        }
        // don't use this.addEntity, because it will always set status to ACTIVE
        // and perform same validations again
        dataEntities.addAll(updatedRents);
    }

    /**
     * Identifies and updates the status of rentals to FAILED.
     * A rental is marked as FAILED if it is currently ACTIVE and was supposed to start
     * by 'now', but another OVERDUE rental exists for the same ski model.
     * This prevents new rentals from starting if the ski is not returned on time from a previous rental.
     * @param now The current date used to determine if a rental should start.
     */
    private void setFailed(LocalDate now) {
        LinkedHashSet<Rent> removedRents = new LinkedHashSet<>();
        LinkedHashSet<Rent> updatedRents = new LinkedHashSet<>();
        for (Rent rent : dataEntities) {
            // istnieje ACTIVE, który się zaczął, ale istnieje dla niego jakiś OVERDUE
            boolean shouldStartNow = rent.getStatus() == RentStatus.ACTIVE && rent.getStartDate().compareTo(now) <= 0;
            if (shouldStartNow) {
                for (Rent otherRent : dataEntities) {
                    boolean otherIsOverdue = otherRent.getStatus() == RentStatus.OVERDUE && otherRent.getSkiModel().equals(rent.getSkiModel());
                    if (otherIsOverdue) {
                        removedRents.add(rent);
                        updatedRents.add(rent.setStatus(RentStatus.FAILED));
                    }
                }
            }
        }
        dataEntities.removeAll(removedRents);
        dataEntities.addAll(updatedRents);
    }

    /**
     * Reads rental data from the file specified during construction.
     * This method utilizes the readData method from the Rent class
     * to load the rental objects.
     * @throws ReadingException If an error occurs while reading the rental data from the file.
     */
    @Override
    public void readFromFile() throws ReadingException {
        readFromFile(Rent::readData);
    }

    /**
     * Checks if two rental periods overlap.
     * An overlap occurs if the end date of one rental is not before the start date of the other,
     * AND the start date of one rental is not after the end date of the other.
     *
     * @param rent The first instance of Rent class.
     * @param otherRent The second instance of Rent class.
     * @return true if the rental periods overlap, false otherwise.
     */
    static boolean rentDatesOverlap(Rent rent, Rent otherRent) {
        return !(rent.getUpdatedEndDate().isBefore(otherRent.getStartDate()) || rent.getStartDate().isAfter(otherRent.getUpdatedEndDate()));
    }

    /**
     * Validates a new rental to ensure its dates are valid and do not overlap with existing rentals
     * for the same ski model that are currently ACTIVE or OVERDUE.
     * @param rent The Rent instance to validate.
     * @throws InvalidRentDateException If the rental's start or end date is invalid (e.g., start date is after end date, or in the past for new rentals).
     * @throws OverlappingRentDateException If the rental period overlaps with an existing active or overdue rental for the same ski.
     */
    void validateRent(Rent rent) throws InvalidRentDateException, OverlappingRentDateException {
        if(!Util.isDateRangeValid(rent.getStartDate(), rent.getEndDate(), now)) {
            throw new InvalidRentDateException();
        }

        for (Rent otherRent : getEntities()) {
            if(otherRent.equals(rent)) continue;
            if (rentDatesOverlap(rent, otherRent)
                && (otherRent.getStatus() == RentStatus.ACTIVE || otherRent.getStatus() == RentStatus.OVERDUE)
                && otherRent.getSkiModel().equals(rent.getSkiModel())) {
                throw new OverlappingRentDateException("Cannot create a reservation. " +
                        "Chosen skis are already reserved for given startDate and endDate");
            }
        }
    }

    /**
     * Adds a new rental entity to the manager. Before adding, it validates the rental's dates
     * and checks for overlaps with existing rentals. The new rental's status is automatically
     * set to {@link RentStatus#ACTIVE}.
     * @param newRent The Rent instance to be added.
     * @throws IllegalArgumentException If newRent is null.
     * @throws EntityAlreadyPresentException If the rental to be added already exists.
     * @throws WritingException If an error occurs while writing the updated data to the file.
     * @throws InvalidRentDateException If the rental's dates are invalid.
     * @throws OverlappingRentDateException If the rental overlaps with an existing rental for the same ski.
     * @throws SkiAppException If a general application error occurs.
     */
    @Override
    public void addEntity(Rent newRent)
            throws EntityAlreadyPresentException, WritingException, InvalidRentDateException, OverlappingRentDateException, SkiAppException {
        if (Util.isAnyArgumentNull(newRent)) {
            throw new IllegalArgumentException("newRent cannot be null.");
        }
        validateRent(newRent);


        // TODO: updtae statusów ACTIVE -> RETURNED, gdy user zwróci w UI
        // to ogarnia SWING za pomocą edit()


        newRent = newRent.setStatus(RentStatus.ACTIVE);
        super.addEntity(newRent);
    }

    /**
     * Edits an existing rental entity by replacing it with a new one.
     * If the start date, end date, or updated end date of the rental has changed,
     * it re-validates the new rental to ensure date validity and prevent overlaps.
     *
     * @param oldRent The existing Rent instance to be replaced.
     * @param newRent The new Rent instance that will replace the old one.
     * @throws IllegalArgumentException If either oldRent or newRent is null.
     * @throws EntityNotPresentException If the oldRent is not found in the collection.
     * @throws EntityAlreadyPresentException If the newRent (after removal of old)
     * still exists (veri rare).
     * @throws WritingException If an error occurs while writing the updated data to the file.
     * @throws SkiAppException If a general application error occurs.
     */
    public void editEntity(Rent oldRent, Rent newRent)
            throws EntityNotPresentException, EntityAlreadyPresentException, IllegalArgumentException, WritingException, SkiAppException {
        if (Util.isAnyArgumentNull(oldRent, newRent)) {
            throw new IllegalArgumentException("One or more of given arguments were null.");
        }
        if (!oldRent.getStartDate().isEqual(newRent.getStartDate()) || !oldRent.getEndDate().isEqual(newRent.getEndDate()) || !oldRent.getUpdatedEndDate().isEqual(newRent.getUpdatedEndDate())) {
            validateRent(newRent);
        }

        removeEntity(oldRent);
        // don't use this.addEntity, because it will always set status to ACTIVE
        // and perform same validations again
        super.addEntity(newRent);
    }

    /**
     * Searches for rental records based on the provided criteria.
     * Multiple criteria can be combined to narrow down the search results.
     * If a parameter is null, it is not used as a filter.
     * @param SkiModel The model of the ski equipment.
     * @param docId The document ID of the client renting.
     * @param startDate The start date of the rental period (inclusive search).
     * @param endDate The planned end date of the rental period (inclusive search).
     * @param updatedEndDate The actual (updated) end date of the rental period (inclusive search).
     * @param comment Optional notes about the rental (contains match).
     * @param status The current status of the rental (e.g., ACTIVE, OVERDUE, RETURNED, FAILED).
     * @return An ArrayList of Rent instances that match all specified non-null criteria.
     */
    public ArrayList<Rent> search(String SkiModel, String docId, LocalDate startDate, LocalDate endDate, LocalDate updatedEndDate, String comment, RentStatus status) {
        Stream<Rent> stream = getEntities().stream();

        if(SkiModel != null) {
            stream = stream.filter(rent -> rent.getSkiModel().equals(SkiModel));
        }

        if(docId != null) {
            stream = stream.filter(rent -> rent.getClientID().equals(docId));
        }

        if(startDate != null) {
            stream = stream.filter(rent -> !rent.getStartDate().isBefore(startDate));
        }

        if(endDate != null) {
            stream = stream.filter(rent -> !rent.getStartDate().isAfter(endDate));
        }

        if(updatedEndDate != null) {
            stream = stream.filter(rent -> !rent.getStartDate().isAfter(updatedEndDate));
        }

        if(comment != null) {
            stream = stream.filter(rent -> Util.containsString(rent.getComment(), comment));
        }

        if(status != null) {
            stream = stream.filter(rent -> rent.getStatus().equals(status));
        }

        return stream.collect(Collectors.toCollection(ArrayList::new));

    }
}
