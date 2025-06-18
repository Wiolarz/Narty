package wit.io.managers;

import exceptions.ReadingException;
import exceptions.WritingException;
import wit.io.data.Ski;
import wit.io.data.SkiType;
import wit.io.utils.Util;

import java.io.*;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class SkiManager extends Manager<Ski> {
    public SkiManager(String filePath) throws ReadingException {
        super(filePath);
    }

    @Override
    public void writeToFile() throws WritingException {
        try (DataOutputStream output =
                     new DataOutputStream(new FileOutputStream(filePath))) {

            output.writeInt(dataEntity.size());
            for (Ski ski : dataEntity) {
                output.writeUTF(ski.getType().getName());
                output.writeUTF(ski.getType().getDescription());
                output.writeUTF(ski.getBrand());
                output.writeUTF(ski.getModel());
                output.writeUTF(ski.getBonds());
                output.writeFloat(ski.getLength());
            }

        } catch (IOException e) {
            throw new WritingException(e);
        }

    }

    @Override
    public void readFromFile() throws ReadingException{
        try (DataInputStream input =
                     new DataInputStream(new FileInputStream(filePath))) {
            int dataLength = input.readInt();
            for (int i = 0; i < dataLength; i++) {
                String typeName = input.readUTF();
                String typeDescription = input.readUTF();
                String brand = input.readUTF();
                String model = input.readUTF();
                String bonds = input.readUTF();
                Float length = input.readFloat();

                SkiType skiType = new SkiType(typeName, typeDescription);
                Ski ski = new Ski(skiType, brand, model, bonds, length);
                dataEntity.add(ski);
            }

        } catch(IOException e){
            throw new ReadingException(e);
        }
    }

    public ArrayList<Ski> search(SkiType type, String brand, String model, String bonds, Float minLength, Float maxLength) {
        Stream<Ski> stream = getEntities().stream();

        // DRY? what's that.
        if(type != null) {
            stream = stream.filter(ski -> ski.getType().equals(type));
        }

        if(brand != null) {
            stream = stream.filter(ski -> Util.containsString(ski.getBrand(), brand));
        }

        if(model != null) {
            stream = stream.filter(ski -> Util.containsString(ski.getModel(), model));
        }

        if(bonds != null) {
            stream = stream.filter(ski -> Util.containsString(ski.getBonds(), bonds));
        }

        if(minLength != null) {
            stream = stream.filter(ski -> ski.getLength() >= minLength);
        }

        if(maxLength != null) {
            stream = stream.filter(ski -> ski.getLength() <= maxLength);
        }

        return stream.collect(Collectors.toCollection(ArrayList::new));

        // todo: test: stream w pierwszym ifie zwróci 0 elementów
    }
}
