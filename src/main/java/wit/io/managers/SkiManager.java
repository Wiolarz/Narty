package wit.io.managers;

import wit.io.data.Rent;
import wit.io.exceptions.ReadingException;
import wit.io.exceptions.WritingException;
import wit.io.data.Ski;
import wit.io.data.SkiType;
import wit.io.utils.Util;

import java.io.*;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Manages operations related to {@link Ski} objects, extending the generic Manager.
 * This class handles Ski class interactions between the user and data.
 */
public class SkiManager extends Manager<Ski> {
    /**
     * Constructor for SkiManager.
     * @param filePath path to the file where Ski data is/will be stored, required argument.
     * @throws ReadingException if the path is null.
     */
    public SkiManager(String filePath) throws ReadingException {
        super(filePath);
    }

    /**
     * Reads ski data from the file specified during construction.
     * This method utilizes the readData method from the Ski class
     * to load the ski objects.
     * @throws ReadingException If an error occurs while reading the ski data from the file.
     */
    @Override
    public void readFromFile() throws ReadingException {
        readFromFile(Ski::readData);
    }

    /**
     * Searches for skis based on the provided criteria.
     * Multiple criteria can be combined to narrow down the search results.
     * If a parameter is null, it is not used as a filter.
     *
     * @param type The SkiType of the ski to search for (exact match).
     * @param brand The brand of the ski to search for (contains match).
     * @param model The model name of the ski to search for (contains match).
     * @param bonds The type or model of the ski bindings to search for (contains match).
     * @param minLength The minimum length of the ski (inclusive).
     * @param maxLength The maximum length of the ski (inclusive).
     * @return An ArrayList of Ski instances that match all specified non-null criteria.
     */
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
