package wit.io;

import exceptions.ReadingException;
import exceptions.SkiAlreadyPresent;
import exceptions.SkiNotPresent;
import exceptions.WritingException;
import wit.io.data.Rent;
import wit.io.data.Ski;
import wit.io.utils.Util;
import wit.io.Manager;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


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
            for (Rent type : dataEntity) {
                //output.writeUTF(type.getName());//TODO rent saving
                //output.writeUTF(type.getDescription());
            }

        } catch (IOException e) {
            throw new WritingException(e);
        }

    }

    @Override
    public void readFromFile() throws ReadingException{
        // ran only once per manager, at the start of the program
        try (DataInputStream input =
                     new DataInputStream(new FileInputStream(filePath))) {
            int length = input.readInt();
            for (int i = 0; i < length; i++) {
                /*String name = input.readUTF(); //TODO add reading
                String description = input.readUTF();
                RentManager rent = new Rent();
                System.out.println(rent.getName());
                System.out.println(rent.getDescription());
                dataEntity.add(rent);*/
            }

        }catch(IOException e){
            throw new ReadingException(e);
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
