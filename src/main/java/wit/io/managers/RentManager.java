package wit.io.managers;

import wit.io.data.enums.RentStatus;
import wit.io.exceptions.*;
import wit.io.data.Rent;
import wit.io.utils.Util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RentManager extends Manager<Rent> {
    public RentManager(String filePath) throws ReadingException, SkiAppException {
        this(filePath, new Date());
    }

    public RentManager(String filePath, Date now) throws ReadingException, SkiAppException {
        super(filePath);

        setOverdue(now);
        setFailed(now);
    }
    
    private void setOverdue(Date now) {
        LinkedHashSet<Rent> updatedRents = new LinkedHashSet<>();
        var it = dataEntities.iterator();
        while(it.hasNext()) {
            Rent rent = it.next();
            if (rent.getStatus() == RentStatus.ACTIVE && rent.getEndDate().before(now)) {
                Rent updatedRent = rent.setStatus(RentStatus.OVERDUE).setUpdatedEndDate(now);
                updatedRents.add(updatedRent);
                it.remove();
            }
        }
        // don't use this.addEntity, because it will always set status to ACTIVE
        // and perform same validations again
        dataEntities.addAll(updatedRents);
    }

    private void setFailed(Date now) {
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
            if(otherRent == rent) continue;
            if (rentDatesOverlap(rent, otherRent)
                && (otherRent.getStatus() == RentStatus.ACTIVE || otherRent.getStatus() == RentStatus.OVERDUE)
                && otherRent.getSkiModel().equals(rent.getSkiModel())) {
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
        if (oldRent.getStartDate() != newRent.getStartDate() || oldRent.getEndDate() != newRent.getEndDate() || oldRent.getUpdatedEndDate() != newRent.getUpdatedEndDate()) {
            validateRent(newRent);
        }

        removeEntity(oldRent);
        // don't use this.addEntity, because it will always set status to ACTIVE
        // and perform same validations again
        super.addEntity(newRent);
    }


    public ArrayList<Rent> search(String SkiModel, String docId, Date startDate, Date endDate, Date updatedEndDate, String comment, RentStatus status) {
        Stream<Rent> stream = getEntities().stream();

        if(SkiModel != null) {
            stream = stream.filter(rent -> rent.getSkiModel().equals(SkiModel));
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
