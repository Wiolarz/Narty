package exceptions;

public class EntityNotPresent extends SkiAppException {
    public EntityNotPresent() {
        super();
    }

    public EntityNotPresent(String message) {
        super(message);
    }
}
