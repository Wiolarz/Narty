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

        Date now = new Date();

        // (1) set OVERDUE
        for (Rent rent : dataEntities) {
            // istnieje ACTIVE, który ma endDat < now i jesteśmy active
            if (rent.getStatus() == RentStatus.ACTIVE && rent.getEndDate().before(now)) {
                setRentOverdue(rent, now);
            }
        }


        // (2) set FAILED
        for (Rent rent : dataEntities) {
            // istnieje ACTIVE, który ma endDat < now  który ma startDate >=now
            boolean shouldStartNow = rent.getStatus() == RentStatus.ACTIVE && rent.getStartDate().compareTo(now) <= 0;
            if (shouldStartNow) {
                for (Rent otherRent : dataEntities) {
                    boolean otherIsOverdue = otherRent.getStatus() == RentStatus.OVERDUE && otherRent.getSkiID().equals(rent.getSkiID());
                    if (otherIsOverdue) {
                        super.editEntity(rent, rent.setStatus(RentStatus.FAILED));
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
        return !(rent.getUpdatedEndDate().before(otherRent.getStartDate()) || rent.getStartDate().after(otherRent.getUpdatedEndDate()));
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

    public void editEntity(Rent oldRent, Rent newRent)
            throws EntityNotPresentException, EntityAlreadyPresentException, IllegalArgumentException, WritingException, SkiAppException {
        if (Util.isAnyArgumentNull(oldRent, newRent)) {
            throw new IllegalArgumentException("One or more of given arguments were null.");
        }
        if (oldRent.getStartDate() != newRent.getStartDate() || oldRent.getEndDate() != newRent.getEndDate()  || oldRent.getUpdatedEndDate() != newRent.getUpdatedEndDate()) {
            validateRent(newRent);
        }

        removeEntity(oldRent);
        // don't use this.addEntity, because it will always set status to ACTIVE
        // and perform same validations again
        super.addEntity(newRent);
    }

    public void setRentOverdue(Rent rent, Date now)
            throws EntityNotPresentException, EntityAlreadyPresentException, IllegalArgumentException, WritingException, SkiAppException {
        if (Util.isAnyArgumentNull(rent)) {
            throw new IllegalArgumentException("One or more of given arguments were null.");
        }

        Rent updatedRent = rent.setStatus(RentStatus.OVERDUE).setUpdatedEndDate(now);
        removeEntity(rent);
        // don't use this.addEntity, because it will always set status to ACTIVE
        // and perform same validations again
        super.addEntity(updatedRent);
    }


    public ArrayList<Rent> search(Integer skiId, Integer docId, Date startDate, Date endDate, Date updatedEndDate, String comment, RentStatus status) {
        Stream<Rent> stream = getEntities().stream();

        if(skiId != null) {
            stream = stream.filter(rent -> rent.getSkiID().equals(skiId));
        }

        if(docId != null) {
            stream = stream.filter(rent -> rent.getClientID().equals(docId));
        }

        if(startDate != null) {
            stream = stream.filter(rent -> !rent.getStartDate().before(startDate));
        }

        if(endDate != null) {
            stream = stream.filter(rent -> !rent.getStartDate().after(endDate));
        }

        if(updatedEndDate != null) {
            stream = stream.filter(rent -> !rent.getStartDate().after(updatedEndDate));
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
