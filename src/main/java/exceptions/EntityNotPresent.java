package exceptions;

public class EntityNotPresent extends Exception {
    public EntityNotPresent() {
        super();
    }

    public EntityNotPresent(String message) {
        super(message);
    }
}
