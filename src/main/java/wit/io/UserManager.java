package wit.io;

import exceptions.UserAlreadyPresent;
import exceptions.UserNotPresent;
import wit.io.data.Client;

import java.util.ArrayList;
import java.util.List;

public class UserManager {
    private List<Client> clients = new ArrayList<>();

    public List<Client> getUsers() {
        return clients;
    }

    public void setUsers(List<Client> clients) {
        this.clients = clients;
    }


    public UserManager() {
    }

    public UserManager(List<Client> clients) {
        this();
        this.clients = clients;
    }

    public void addUser(String firstName, String lastName, int docId, String description) throws UserAlreadyPresent {
        if (UserExists(docId)) {
            throw new UserAlreadyPresent("Exception occurred adding new User.");
        }

        Client newClient = new Client(firstName, lastName, docId, description);
        clients.add(newClient);
        // todo: save/override user files here?
    }

    public void removeUser(int docId) throws UserNotPresent {
        if (!UserExists(docId)) {
            throw new UserNotPresent();
        }
    }

    public void editUser(int docId) throws UserAlreadyPresent {
        if (UserExists(docId)) {
            throw new UserAlreadyPresent();
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
