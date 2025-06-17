package wit.io.data;

public class User {
    String firstName;
    String lastName;
    int docId;
    String description;

    public User(String firstName, String lastName, int docId, String description) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.docId = docId;
        this.description = description;
    }

    public String getName() {
        return firstName + lastName;
    }

    public int getDocId() {
        return docId;
    }

    //TODO implement tostring and equals
}
