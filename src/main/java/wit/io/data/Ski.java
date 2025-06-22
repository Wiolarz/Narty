package wit.io.data;

import wit.io.utils.Util;
import wit.io.utils.Writeable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * Class representing a ski,
 * implements Writeable
 */
public class Ski implements Writeable {

    private final String model; // PK
    private final SkiType type;
    private final String brand;
    private final String bonds;
    private final Float length;

    /**
     * Constructor for Ski.
     * @param type The type of the ski, required argument.
     * @param brand The brand of the ski, required argument.
     * @param model The model name of the ski, required argument. This acts as the primary key.
     * @param bonds The type or model of the ski bindings, required argument.
     * @param length The length of the ski in centimeters, required argument.
     * @throws IllegalArgumentException if any required parameter is null.
     */
    public Ski(SkiType type, String brand, String model, String bonds, Float length) {
        if (Util.isAnyArgumentNull(type, brand, model, bonds, length)) {
            throw new IllegalArgumentException("One or more of given arguments were null.");
        }

        this.type = type;
        this.brand = brand;
        this.model = model;
        this.bonds = bonds;
        this.length = length;
    }

    /**
     * Generates a hash code for the Ski object based on its unique model.
     * @return The hash code of the model string.
     */
    @Override
    public int hashCode() {
        return model.hashCode();
    }

    /**
     * Compares two Ski objects for equality.
     * Two Skis are considered equal if they have the same model.
     * @param obj The object to compare with this Ski.
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

        return ((Ski) obj).hashCode() == (this.hashCode());
    }

    /**
     * Returns a string representation of the Ski object.
     * @return A string containing all the Ski's details.
     */
    @Override
    public String toString() {
        return "Ski{" +
                "type=" + type +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", bonds='" + bonds + '\'' +
                ", length=" + length +
                '}';
    }

    /**
     * Writes the ski's data to a binary stream.
     * The data is written in the order: type name, type description, brand, model, bonds, length.
     * @param output The DataOutputStream to write the data to.
     * @throws IOException if an I/O error occurs.
     */
    public void writeData(DataOutputStream output) throws IOException {
        type.writeData(output);
        output.writeUTF(brand);
        output.writeUTF(model);
        output.writeUTF(bonds);
        output.writeFloat(length);
    }

    /**
     * Reads ski data from a binary stream and creates a new Ski object.
     * The data is read in the order: type name, type description, brand, model, bonds, length.
     * @param input The DataInputStream to read the data from.
     * @return A new Ski object populated with the data from the stream.
     * @throws IOException if an I/O error occurs or if the end of the stream is reached before the object is populated.
     */
    public static Ski readData(DataInputStream input) throws IOException {
        SkiType skiType = SkiType.readData(input);

        String brand = input.readUTF();
        String model = input.readUTF();
        String bonds = input.readUTF();
        Float length = input.readFloat();

        return new Ski(skiType, brand, model, bonds, length);
    }

    /**
     * Gets the type of the ski.
     * @return The SkiType instance representing the ski's type.
     */
    public SkiType getType() {
        return type;
    }

    /**
     * Gets the brand of the ski.
     * @return A string representing the ski's brand.
     */
    public String getBrand() {
        return brand;
    }

    /**
     * Gets the model name of the ski.
     * @return A string representing the ski's model.
     */
    public String getModel() {
        return model;
    }

    /**
     * Gets the type or model of the ski bindings.
     * @return A string representing the ski's bonds.
     */
    public String getBonds() {
        return bonds;
    }

    /**
     * Gets the length of the ski.
     * @return A Float representing the ski's length in CENTIMETERS.
     */
    public Float getLength() {
        return length;
    }
}