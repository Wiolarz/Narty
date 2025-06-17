package wit.io.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SkiType {
    private final String name;
    private final String description;

    public SkiType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object obj) {
        if (!obj.getClass().equals(getClass())){
            return false;
        }

        return ((SkiType) obj).name.equals(name);
    }
}