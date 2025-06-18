package wit.io;

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

// TODO: SWING
public class RentManager extends Manager<Rent> {
    public RentManager(String filePath) throws ReadingException {
        super(filePath);
    }

    @Override
    public void writeToFile() throws WritingException {
        // writes skiTypes to file (override)
        try (DataOutputStream output =
                     new DataOutputStream(new FileOutputStream(filePath))) {

            output.writeInt(dataEntity.size());
            for (Rent rent : dataEntity) {
                output.writeInt(rent.getRentID());
                output.writeUTF(Util.dateToString(rent.getStartDate()));
                output.writeUTF(Util.dateToString(rent.getEndDate()));
                output.writeUTF(String.valueOf(rent.getSkiID()));
                output.writeUTF(String.valueOf(rent.getClientID()));
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
                Integer rentID = input.readInt();
                Date startDate = Util.stringToDate(input.readUTF());
                Date endDate = Util.stringToDate(input.readUTF());
                Integer skiID = input.readInt();
                Integer clientID = input.readInt();
                String comment = input.readUTF();
                RentStatus rentStatus = RentStatus.valueOf(input.readUTF());
                Rent rent = new Rent(rentID, startDate, endDate, skiID, clientID, comment, rentStatus);
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

        if(!Util.isAnyArgumentNull(nameSuffix)) {
            //stream = stream.filter(ski -> ski.getName().toLowerCase().startsWith(nameSuffix.toLowerCase()));
        }

        if(!Util.isAnyArgumentNull(nameSuffix)) {
            //stream = stream.filter(ski -> ski.getDescription().toLowerCase().contains(partialDescription.toLowerCase()));
        }

        return stream.collect(Collectors.toCollection(ArrayList::new));

        // todo: test: stream w pierwszym ifie zwróci 0 elementów
    }
}
