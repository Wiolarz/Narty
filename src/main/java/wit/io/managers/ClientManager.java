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

/**
 * Manages operations related to Client objects, extending the generic {@link Manager}.
 * This class handles Client class interactions between the user and data.
 */
public class ClientManager extends Manager<Client> {

    /**
     * Constructor for ClientManager.
     * @param filePath path to the file where client data is or will be stored, required argument
     * @throws ReadingException if the path is null.
     */
    public ClientManager(String filePath) throws ReadingException {
        super(filePath);
    }

    /**
     * Reads client data from the file specified during construction.
     * This method uses the readData method from the {@link Client} class
     * to load the client objects.
     * @throws ReadingException If an error occurs while reading the client data from the file.
     */
    @Override
    public void readFromFile() throws ReadingException {
        readFromFile(Client::readData);
    }

    /**
     * Searches for clients based on the provided criteria.
     * Multiple criteria can be combined to narrow down the search results.
     * If a parameter is {@code null}, it is skipped as a filter.
     *
     * @param docId The Client's ID number to search for (exact match). Optional.
     * @param firstName The first name of the client to search for (starts with match). Optional.
     * @param lastName The last name of the client to search for (starts with match). Optional.
     * @param description A description associated with the client to search for (contains match). Optional.
     * @return An ArrayList of Client instances that match all specified non-null filters.
     */
    public ArrayList<Client> search(String docId, String firstName, String lastName, String description) {
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
