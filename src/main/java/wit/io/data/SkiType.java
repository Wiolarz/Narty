package wit.io.data;

import wit.io.utils.Util;
import wit.io.utils.Writeable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;

public class SkiType implements Writeable {
    private final String name;
    private final String description;

    public SkiType(String name, String description) {
        if (Util.isAnyArgumentNull(name)) {
            throw new IllegalArgumentException("name cannot be null.");
        }

        this.name = name;
        this.description = (description == null) ? "" : description;
    }

    @Override
    public String toString() {
        return "SkiType{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!obj.getClass().equals(getClass())){
            return false;
        }

        return ((SkiType) obj).name.equals(name);
    }

    public void writeData(DataOutputStream output) throws IOException {
        output.writeUTF(name);
        output.writeUTF(description);
    }

    public static SkiType readData(DataInputStream input) throws IOException {
        String name = input.readUTF();
        String description = input.readUTF();
        return new SkiType(name, description);
    }

    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
}



// TODO
// equals
// ?hashCode
// toString
// validation in constructor