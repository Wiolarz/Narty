package wit.io;

import exceptions.UserAlreadyPresentException;
import exceptions.UserNotPresentException;
import wit.io.data.Client;

import java.util.ArrayList;
import java.util.List;

public class ClientManager {
    private List<Client> clients = new ArrayList<>();

    public List<Client> getUsers() {
        return clients;
    }

    public void setUsers(List<Client> clients) {
        this.clients = clients;
    }


    public ClientManager() {
    }

    public ClientManager(List<Client> clients) {
        this();
        this.clients = clients;
    }

    public void addUser(String firstName, String lastName, int docId, String description) throws UserAlreadyPresentException {
        if (UserExists(docId)) {
            throw new UserAlreadyPresentException("Exception occurred adding new User.");
        }

        Client newClient = new Client(firstName, lastName, docId, description);
        clients.add(newClient);
        // todo: save/override user files here?
    }

    public void removeUser(int docId) throws UserNotPresentException {
        if (!UserExists(docId)) {
            throw new UserNotPresentException();
        }
    }

    public void editUser(int docId) throws UserAlreadyPresentException {
        if (UserExists(docId)) {
            throw new UserAlreadyPresentException();
        }

    }


    private boolean UserExists(int docId) {
        for (Client type : clients) {
            if (type.getDocId() == docId)
                return true;
        }
        return false;
    }
}
