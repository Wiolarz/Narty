package wit.io.managers;

import exceptions.EntityAlreadyPresentException;
import exceptions.ReadingException;
import exceptions.WritingException;
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



    public void addEntity(Rent newEntity) throws EntityAlreadyPresentException, WritingException {
        // TODO walidacja dat
        // (0) validate date if startDate >== now(), i endDate <== reasonable date i startDate <= endDate jeśli nie to błędna data
        // throws dateException

        // (1) data pokrywa się + istnieje status == ACITVE --> throws e
        ((newRent.startDate >= existingREnt.startDate && newRent.startDate <= existingRent.endDate)
                ||  (newRent.endDate >= existingREnt.startDate && newRent.endDate <= existingRent.endDate))
                && existingRent.status == ACTIVE or RESERVED
        throws Exception - cannot add new Rent, date overlaping / is already reserved

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

    @Override
    public void writeToFile() throws WritingException {
        // writes skiTypes to file (override)
        try (DataOutputStream output =
                     new DataOutputStream(new FileOutputStream(filePath))) {

            output.writeInt(dataEntity.size());
            for (Rent rent : dataEntity) {
                output.writeUTF(String.valueOf(rent.getSkiID()));
                output.writeUTF(String.valueOf(rent.getClientID()));
                output.writeUTF(Util.dateToString(rent.getStartDate()));
                output.writeUTF(Util.dateToString(rent.getEndDate()));
                output.writeUTF(rent.getComment());
                output.writeUTF(rent.getStatus().name());
            }

        } catch (IOException e) {
            throw new WritingException(e);
        }

    }

    @Override
    public void readFromFile() throws ReadingException{
        try (DataInputStream input =
                     new DataInputStream(new FileInputStream(filePath))) {
            int length = input.readInt();
            for (int i = 0; i < length; i++) {
                Integer skiID = input.readInt();
                Integer clientID = input.readInt();
                Date startDate = Util.stringToDate(input.readUTF());
                Date endDate = Util.stringToDate(input.readUTF());
                String comment = input.readUTF();
                RentStatus rentStatus = RentStatus.valueOf(input.readUTF());
                Rent rent = new Rent(startDate, endDate, skiID, clientID, comment, rentStatus);
                System.out.println(rent);
                dataEntity.add(rent);
            }

        }catch(IOException | ParseException e){
            throw new ReadingException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
