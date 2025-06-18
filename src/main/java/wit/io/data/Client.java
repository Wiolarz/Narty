package wit.io.data;

import wit.io.utils.Util;

public class Client {
    private final int docId;
    private final String firstName;
    private final String lastName;
    private final String description;

    public Client(String firstName, String lastName, int docId, String description) {
        if (Util.isAnyArgumentNull(firstName, lastName, docId)) {
            throw new IllegalArgumentException("One or more of given arguments were null.");
        }
        this.firstName = firstName;
        this.lastName = lastName;
        this.docId = docId;
        this.description = (description == null) ? "" : description;
    }

    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", docId=" + docId +
                ", description='" + description + '\'' +
                '}';
    }

    public String getName() {
        return firstName + lastName;
    }

    public int getDocId() {
        return docId;
    }

    //TODO implement tostring and equals
}
