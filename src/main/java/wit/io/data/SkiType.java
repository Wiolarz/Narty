package wit.io.data;

import wit.io.utils.Util;

public class SkiType {
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
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!obj.getClass().equals(getClass())){
            return false;
        }

        return ((SkiType) obj).name.equals(name);
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