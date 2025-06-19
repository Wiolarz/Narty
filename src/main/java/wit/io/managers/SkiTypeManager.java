package wit.io.managers;

import wit.io.data.Client;
import wit.io.exceptions.ReadingException;
import wit.io.exceptions.WritingException;
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
    public void readFromFile() throws ReadingException {
        readFromFile(SkiType::readData);
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
