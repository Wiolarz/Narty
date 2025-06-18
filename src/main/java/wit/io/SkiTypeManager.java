package wit.io;

import exceptions.ReadingException;
import exceptions.WritingException;
import wit.io.data.SkiType;
import wit.io.utils.Util;

import java.io.*;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;


// TODO: SWING
public class SkiTypeManager extends Manager<SkiType>{
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
            int length = input.readInt();
            for (int i = 0; i < length; i++) {
                String name = input.readUTF();
                String description = input.readUTF();
                SkiType skiType = new SkiType(name, description);
                System.out.println(skiType.getName());
                System.out.println(skiType.getDescription());
                dataEntity.add(skiType);
            }

        }catch(IOException e){
            throw new ReadingException(e);
        }
    }


    public ArrayList<SkiType> search(String nameSuffix, String partialDescription) {
        Stream<SkiType> stream = getEntities().stream();

        if(!Util.isAnyArgumentNull(nameSuffix)) {
            stream = stream.filter(ski -> ski.getName().toLowerCase().startsWith(nameSuffix.toLowerCase()));
        }

        if(!Util.isAnyArgumentNull(nameSuffix)) {
            stream = stream.filter(ski -> ski.getDescription().toLowerCase().contains(partialDescription.toLowerCase()));
        }

        return stream.collect(Collectors.toCollection(ArrayList::new));

        // todo: test: stream w pierwszym ifie zwróci 0 elementów
    }
}
