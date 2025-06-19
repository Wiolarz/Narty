package wit.io.managers;

import wit.io.data.enums.RentStatus;
import wit.io.exceptions.*;
import wit.io.data.Rent;
import wit.io.utils.Util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RentManager extends Manager<Rent> {
    public RentManager(String filePath) throws ReadingException, SkiAppException {
        super(filePath);
        // TODO: przy starcie programu (read z pliku)
        // ACTIVE -> FAILED
        Date now = new Date();

        // (1) set OVERDUE
        for (Rent rent : dataEntities) {
            // istnieje ACTIVE, który ma endDat < now i jesteśmy active
            if (rent.getStatus() == RentStatus.ACTIVE && rent.getEndDate().before(now)) {
                // TODO: add new field: updatedEndDate and change it here
                editEntity(rent, rent.setStatus(RentStatus.OVERDUE));
            }
        }


        // (2) set FAILED
        for (Rent rent : dataEntities) {
            // istnieje ACTIVE, który ma endDat < now  który ma startDate >=now
            if (rent.getStatus() == RentStatus.ACTIVE && rent.getStartDate().compareTo(now) <= 0) {
                for (Rent otherRent : dataEntities) {
                    if (otherRent.getStatus() == RentStatus.OVERDUE && otherRent.getSkiID().equals(rent.getSkiID())) {
                        editEntity(rent, rent.setStatus(RentStatus.FAILED));
                    }
                }
            }

        }
    }

    @Override
    public void readFromFile() throws ReadingException {
        readFromFile(Rent::readData);
    }


    static boolean rentDatesOverlap(Rent rent, Rent otherRent) {
        return !(rent.getEndDate().before(otherRent.getStartDate()) || rent.getStartDate().after(otherRent.getEndDate()));
    }

    void validateRent(Rent rent) throws InvalidRentDateException, OverlappingRentDateException {
        if(!Util.isDateRangeValid(rent.getStartDate(), rent.getEndDate())) {
            throw new InvalidRentDateException();
        }

        for (Rent otherRent : getEntities()) {
            if (rentDatesOverlap(rent, otherRent)
                && (otherRent.getStatus() == RentStatus.ACTIVE || otherRent.getStatus() == RentStatus.OVERDUE)
                && otherRent.getSkiID().equals(rent.getSkiID())) {
                throw new OverlappingRentDateException("Cannot create a reservation. " +
                        "Chosen skis are already reserved for given startDate and endDate");
            }
        }
    }

    // TODO: checks for updatedEndDate instead of endDate!
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

    // TODO: checks for updatedEndDate instead of endDate!
    public void editEntity(Rent oldRent, Rent newRent)
            throws EntityNotPresentException, EntityAlreadyPresentException, IllegalArgumentException, WritingException, SkiAppException {
        if (Util.isAnyArgumentNull(oldRent, newRent)) {
            throw new IllegalArgumentException("One or more of given arguments were null.");
        }
        if (oldRent.getStartDate() != newRent.getStartDate() || oldRent.getEndDate() != newRent.getEndDate()) {
            validateRent(newRent);
        }

        removeEntity(oldRent);
        // don't use this.addEntity, because it will always set status to ACTIVE
        // and perform same validations again
        super.addEntity(newRent);
    }


    public ArrayList<Rent> search(String nameSuffix, String partialDescription) {
        Stream<Rent> stream = getEntities().stream();

        if(nameSuffix != null) {
            //stream = stream.filter(ski -> ski.getName().toLowerCase().startsWith(nameSuffix.toLowerCase()));
        }

        if(partialDescription != null) {
            //stream = stream.filter(ski -> ski.getDescription().toLowerCase().contains(partialDescription.toLowerCase()));
        }

        return stream.collect(Collectors.toCollection(ArrayList::new));

        // todo: test: stream w pierwszym ifie zwróci 0 elementów
    }
}
