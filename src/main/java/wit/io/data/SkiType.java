package wit.io.data;

import wit.io.utils.Util;
import wit.io.utils.Writeable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * Class representing one SkiType,
 * implements Writeable
 */
public class SkiType implements Writeable {
    private final String name; // PK
    private final String description;

    /**
     * Constructor for SkiType.
     * @param name The name of the ski type (e.g., "Alpine", "Freeride"), required argument. This acts as the primary key.
     * @param description A brief description of the ski type, optional argument. If null, it defaults to an empty string.
     * @throws IllegalArgumentException if the name is null.
     */
    public SkiType(String name, String description) {
        if (Util.isAnyArgumentNull(name)) {
            throw new IllegalArgumentException("name cannot be null.");
        }

        this.name = name;
        this.description = (description == null) ? "" : description;
    }

    /**
     * Returns a string representation of the SkiType instance.
     * @return A string containing the name and description of the ski type.
     */
    @Override
    public String toString() {
        return "SkiType{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    /**
     * Generates a hash code for the SkiType object based on its unique name.
     * @return The hash code of the name string.
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * Compares two SkiType objects for equality.
     * Two SkiTypes are considered equal if they have the same name.
     * @param obj The object to compare with this SkiType.
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

        return ((SkiType) obj).hashCode() == (this.hashCode());
    }

    /**
     * Writes the ski type's data to a binary stream.
     * The data is written in the order: name, description.
     * @param output The DataOutputStream to write the data to.
     * @throws IOException if an I/O error occurs.
     */
    public void writeData(DataOutputStream output) throws IOException {
        output.writeUTF(name);
        output.writeUTF(description);
    }

    /**
     * Reads ski type data from a binary stream and creates a new SkiType object.
     * The data is read in the order: name, description.
     * @param input The DataInputStream to read the data from.
     * @return A new SkiType object populated with the data from the stream.
     * @throws IOException if an I/O error occurs or if the end of the stream is reached before the object is populated.
     */
    public static SkiType readData(DataInputStream input) throws IOException {
        String name = input.readUTF();
        String description = input.readUTF();
        return new SkiType(name, description);
    }

    /**
     * Gets the name of the ski type.
     * @return A string representing the ski type's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the description of the ski type.
     * @return A string representing the ski type's description, or an empty string if no description was provided.
     */
    public String getDescription() {
        return description;
    }
}