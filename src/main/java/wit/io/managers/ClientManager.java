package wit.io.managers;

import wit.io.exceptions.EntityAlreadyPresentException;
import wit.io.exceptions.ReadingException;
import wit.io.exceptions.SkiAppException;
import wit.io.exceptions.WritingException;
import wit.io.data.Client;
import wit.io.utils.Util;

import java.io.*;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO: override addEntity - validate if docId is unique
public class ClientManager extends Manager<Client> {
    public ClientManager(String filePath) throws ReadingException {
        super(filePath);
    }

    @Override
    public void readFromFile() throws ReadingException {
        readFromFile(Client::readData);
    }

    public ArrayList<Client> search(Integer docId, String firstName, String lastName, String description) {
        Stream<Client> stream = getEntities().stream();

        if(docId != null) {
            stream = stream.filter(client -> client.getDocId().equals(docId));
        }

        if(firstName != null) {
            stream = stream.filter(client -> Util.startsWithString(client.getFirstName(), firstName));
        }

        if(lastName != null) {
            stream = stream.filter(client -> Util.startsWithString(client.getLastName(), lastName));
        }

        if(description != null) {
            stream = stream.filter(client -> Util.containsString(client.getDescription(), description));
        }

        return stream.collect(Collectors.toCollection(ArrayList::new));

    }
}
