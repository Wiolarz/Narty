package wit.io.managers;

import wit.io.data.SkiType;
import wit.io.exceptions.EntityAlreadyPresentException;
import wit.io.exceptions.ReadingException;
import wit.io.exceptions.WritingException;
import wit.io.data.Rent;
import wit.io.data.enums.RentStatus;
import wit.io.utils.Util;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Date;

public class RentManager extends Manager<Rent> {
    public RentManager(String filePath) throws ReadingException {
        super(filePath);
    }

    @Override
    public void readFromFile() throws ReadingException {
        readFromFile(Rent::readData);
    }



    public void addEntity(Rent newEntity) throws EntityAlreadyPresentException, WritingException {
        // TODO walidacja dat
        // (0) validate date if startDate >== now(), i endDate <== reasonable date i startDate <= endDate jeśli nie to błędna data
        // throws dateException

        // (1) data pokrywa się + istnieje status == ACITVE --> throws e
//        ((newRent.startDate >= existingREnt.startDate && newRent.startDate <= existingRent.endDate)
//                ||  (newRent.endDate >= existingREnt.startDate && newRent.endDate <= existingRent.endDate))
//                && existingRent.status == ACTIVE or RESERVED
//        throws Exception - cannot add new Rent, date overlaping / is already reserved

        // DEFAULT - nic nie trzeba sprawdzać
        // (2) data pokrywa się + istnieje status RETURNED lub FAILED => dodajemy (nie trzeba sprawdzać tego warunku -- DEFAULT)
        // (3) data nie pokrywa się -> dodajemy ACTIVE
        // TODO: dokładność do dnia


        // TODO: przy starcie programu (read z pliku)
        // ACTIVE -> FAILED


        // TODO: updtae statusów ACTIVE -> RETURNED, gdy user zwróci w UI
        // to ogarnia SWING za pomocą edit()

        super.addEntity(newEntity);
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
