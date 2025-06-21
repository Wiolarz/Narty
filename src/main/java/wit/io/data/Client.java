package wit.io.data;

import wit.io.utils.Util;
import wit.io.utils.Writeable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;

public class Client implements Writeable {
    private final String docId; // numer dowodu zawiera litery i cyfry, PK

    private final String firstName;
    private final String lastName;
    private final String description;

    public Client(String docId, String firstName, String lastName, String description) {
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
    public int hashCode() {
        return docId.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!obj.getClass().equals(getClass())){
            return false;
        }

        return ((Client) obj).hashCode() == (this.hashCode());
    }

    public void writeData(DataOutputStream output) throws IOException {
        output.writeUTF(getDocId());
        output.writeUTF(getFirstName());
        output.writeUTF(getLastName());
        output.writeUTF(getDescription());
    }

    public static Client readData(DataInputStream input) throws IOException {
        String docId = input.readUTF();
        String firstName = input.readUTF();
        String lastName = input.readUTF();
        String description = input.readUTF();

        return new Client(docId, firstName, lastName, description);
    }


    public String getDocId() {
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
