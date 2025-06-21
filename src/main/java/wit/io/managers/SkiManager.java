package wit.io.managers;

import wit.io.exceptions.ReadingException;
import wit.io.exceptions.WritingException;
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
    public void readFromFile() throws ReadingException {
        readFromFile(Ski::readData);
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

    }
}
