package wit.io;

import exceptions.UserAlreadyPresent;
import exceptions.UserNotPresent;
import wit.io.data.User;

import java.util.ArrayList;
import java.util.List;

public class UserManager {
    private List<User> users = new ArrayList<>();

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> Users) {
        this.users = Users;
    }


    public UserManager() {
    }

    public UserManager(List<User> Users) {
        this();
        this.users = Users;
    }

    public void addUser(String firstName, String lastName, int docId, String description) throws UserAlreadyPresent {
        if (UserExists(docId)) {
            throw new UserAlreadyPresent("Exception occurred adding new User.");
        }

        User newUser = new User(firstName, lastName, docId, description);
        users.add(newUser);
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
        for (User type : users) {
            if (type.getDocId() == docId)
                return true;
        }
        return false;
    }
}
