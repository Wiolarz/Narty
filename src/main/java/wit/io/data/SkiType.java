package wit.io.data;

public class SkiType {
    String name;
    String description;

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
}