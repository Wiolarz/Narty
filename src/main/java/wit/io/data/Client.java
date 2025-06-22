package wit.io.data;

import wit.io.utils.Util;
import wit.io.utils.Writeable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * Class representing a Client, person that can rent skis.
 * Implements Writeable
 */
public class Client implements Writeable {
    /**
     * The client's unique identification document number.
     * This field is set only once, during object construction.
     */
    private final String docId;

    /**
     * The client's first name.
     * This field is set only once, during object construction.
     */
    private final String firstName;
    /**
     * The client's last name.
     * This field is set only once, during object construction.
     */
    private final String lastName;
    /**
     * Additional notes or description about the client, added by staff.
     * This field is set only once, during object construction.
     * May be empty string if no description is provided.
     */
    private final String description;

    /**
     * constructor for Client
     * @param docId Client's ID card number, required argument, has to be unique
     * @param firstName Client's first name, required argument
     * @param lastName Client's last name, required argument
     * @param description Additional notes about client, put by staff, optional argument
     */
    public Client(String docId, String firstName, String lastName, String description) {
        if (Util.isAnyArgumentNull(firstName, lastName, docId)) {
            throw new IllegalArgumentException("One or more of given arguments were null.");
        }
        this.firstName = firstName;
        this.lastName = lastName;
        this.docId = docId;
        this.description = (description == null) ? "" : description;
    }

    /**
     * Returns a string representation of the Client object.
     * @return A string containing all the client's details.
     */
    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", docId=" + docId +
                ", description='" + description + '\'' +
                '}';
    }

    /**
     * Generates a hash code for the Client object based on unique docId.
     * @return The hash code of the docId string.
     */
    @Override
    public int hashCode() {
        return docId.hashCode();
    }


    /**
     * Compares two Clients.
     * Two clients are considered equal if they have the same docId.
     * @param obj The object to compare with this Client.
     * @return true if the objects are equal, false otherwise.
     */
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

    /**
     * Writes the client's data to a binary stream.
     * The data is written in the order: docId, firstName, lastName, description.
     * @param output The DataOutputStream to write the data to.
     * @throws IOException if an I/O error occurs.
     */
    public void writeData(DataOutputStream output) throws IOException {
        output.writeUTF(getDocId());
        output.writeUTF(getFirstName());
        output.writeUTF(getLastName());
        output.writeUTF(getDescription());
    }

    /**
     * Reads client data from a binary stream and creates a new Client object.
     * The data is read in the order: docId, firstName, lastName, description.
     * @param input The DataInputStream to read the data from.
     * @return A new Client object populated with the data from the stream.
     * @throws IOException if an I/O error occurs or if the end of the stream is reached before the object is populated.
     */
    public static Client readData(DataInputStream input) throws IOException {
        String docId = input.readUTF();
        String firstName = input.readUTF();
        String lastName = input.readUTF();
        String description = input.readUTF();

        return new Client(docId, firstName, lastName, description);
    }


    /**
     * Gets the client's document ID.
     * @return The docId string.
     */
    public String getDocId() {
        return docId;
    }


    /**
     * Gets the client's firstName.
     * @return The firstName string.
     */
    public String getFirstName() {
        return firstName;
    }


    /**
     * Gets the client's lastName.
     * @return The lastName string.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Gets the client's description.
     * @return The description string.
     */
    public String getDescription() {
        return description;
    }
}
