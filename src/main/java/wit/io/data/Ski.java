package wit.io.data;

import wit.io.utils.Util;
import wit.io.utils.Writeable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;

// (typ, marka, model, wiązania, długość)
public class Ski implements Writeable {
    /*
    Ewidencja typów nart (lista) i wprowadzanie nowego typu nart (nazwa, opis), edycja, usuwanie.

     */

    private final int id;
    private final SkiType type;
    private final String brand;
    private final String model;
    private final String bonds;
    private final Float length;

    public Ski(SkiType type, String brand, String model, String bonds, Float length) {
        if (Util.isAnyArgumentNull(type, brand, model, bonds, length)) {
            throw new IllegalArgumentException("One or more of given arguments were null.");
        }

        this.id = hashCode();
        this.type = type;
        this.brand = brand;
        this.model = model;
        this.bonds = bonds;
        this.length = length;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, brand, model, bonds, length);
    }

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

    @Override
    public String toString() {
        return "Ski{" +
                "id=" + id +
                ", type=" + type +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", bonds='" + bonds + '\'' +
                ", length=" + length +
                '}';
    }

    public void writeData(DataOutputStream output) throws IOException {
        output.writeUTF(type.getName());
        output.writeUTF(type.getDescription());
        output.writeUTF(brand);
        output.writeUTF(model);
        output.writeUTF(bonds);
        output.writeFloat(length);
    }

    public static Ski readData(DataInputStream input) throws IOException {
        String typeName = input.readUTF();
        String typeDescription = input.readUTF();
        String brand = input.readUTF();
        String model = input.readUTF();
        String bonds = input.readUTF();
        Float length = input.readFloat();

        SkiType skiType = new SkiType(typeName, typeDescription);
        return new Ski(skiType, brand, model, bonds, length);
    }

    public int getId() {
        return id;
    }

    public SkiType getType() {
        return type;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public String getBonds() {
        return bonds;
    }

    public Float getLength() {
        return length;
    }
}