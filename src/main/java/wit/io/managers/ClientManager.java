package wit.io.managers;

import exceptions.ReadingException;
import exceptions.WritingException;
import wit.io.data.Client;
import wit.io.data.Ski;
import wit.io.data.SkiType;
import wit.io.utils.Util;

import java.io.*;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ClientManager extends Manager<Client> {
    public ClientManager(String filePath) throws ReadingException {
        super(filePath);
    }

    @Override
    public void writeToFile() throws WritingException {
        try (DataOutputStream output =
                     new DataOutputStream(new FileOutputStream(filePath))) {

            output.writeInt(dataEntity.size());
            for (Client client : dataEntity) {
                output.writeInt(client.getDocId());
                output.writeUTF(client.getFirstName());
                output.writeUTF(client.getLastName());
                output.writeUTF(client.getDescription());
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
                Integer docId = input.readInt();
                String firstName = input.readUTF();
                String lastName = input.readUTF();
                String description = input.readUTF();

                Client client = new Client(docId, firstName, lastName, description);
                dataEntity.add(client);
            }

        } catch(IOException e){
            throw new ReadingException(e);
        }
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

        // todo: test: stream w pierwszym ifie zwróci 0 elementów
    }
}
