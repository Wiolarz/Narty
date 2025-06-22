package wit.io.managers;

import wit.io.data.Client;
import wit.io.data.Ski;
import wit.io.exceptions.ReadingException;
import wit.io.exceptions.WritingException;
import wit.io.data.SkiType;
import wit.io.utils.Util;

import java.io.*;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Manages operations related to {@link SkiType} objects, extending the generic Manager.
 * This class handles SkiType class interactions between the user and data.
 */
public class SkiTypeManager extends Manager<SkiType> {
    /**
     * Constructor for SkiTypeManager.
     * @param filePath path to the file where SkiType data is/will be stored, required argument.
     * @throws ReadingException if the path is null.
     */
    public SkiTypeManager(String filePath) throws ReadingException {
        super(filePath);
    }

    /**
     * Reads ski type data from the file specified during construction.
     * This method utilizes the readData method from the SkiType class
     * to load the ski type objects.
     *
     * @throws ReadingException If an error occurs while reading the ski type data from the file.
     */
    @Override
    public void readFromFile() throws ReadingException {
        readFromFile(SkiType::readData);
    }


    /**
     * Searches for ski types based on the provided criteria.
     * Multiple criteria can be combined to narrow down the search results.
     * If a parameter is null, it is not used as a filter.
     *
     * @param nameSuffix The suffix of the ski type name to search for (starts with match)..
     * @param partialDescription A partial string to match within the ski type's description (contains match).
     * @return An ArrayList of SkiType instances that match all specified non-null criteria.
     */
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
