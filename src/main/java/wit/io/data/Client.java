package wit.io.data;

import wit.io.utils.Util;

public class Client {
    private final Integer docId;
    private final String firstName;
    private final String lastName;
    private final String description;


    public Client(String firstName, String lastName, Integer docId, String description) {
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!obj.getClass().equals(getClass())){
            return false;
        }

        return ((Client) obj).docId.equals(docId);
    }


    public int getDocId() {
        return docId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDescription() {
        return description;
    }
}
