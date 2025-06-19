package wit.io.managers;

import exceptions.ReadingException;
import exceptions.WritingException;
import wit.io.data.SkiType;
import wit.io.utils.Util;

import java.io.*;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class SkiTypeManager extends Manager<SkiType> {
    public SkiTypeManager(String filePath) throws ReadingException {
        super(filePath);
    }

    @Override
    public void writeToFile() throws WritingException {
        // writes skiTypes to file (override)
        try (DataOutputStream output =
                 new DataOutputStream(new FileOutputStream(filePath))) {

            output.writeInt(dataEntity.size());
            for (SkiType type : dataEntity) {
                output.writeUTF(type.getName());
                output.writeUTF(type.getDescription());
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
            int dataLength = input.readInt();
            for (int i = 0; i < dataLength; i++) {
                String name = input.readUTF();
                String description = input.readUTF();
                SkiType skiType = new SkiType(name, description);

                dataEntity.add(skiType);
            }

        }catch(IOException e){
            throw new ReadingException(e);
        }
    }


    public ArrayList<SkiType> search(String nameSuffix, String partialDescription) {
        Stream<SkiType> stream = getEntities().stream();

        if(nameSuffix != null) {
            stream = stream.filter(ski -> Util.startsWithString(ski.getName(), nameSuffix));
        }

        if(partialDescription != null) {
            stream = stream.filter(ski -> Util.containsString(ski.getDescription(), partialDescription));
        }

        return stream.collect(Collectors.toCollection(ArrayList::new));
    }
}
