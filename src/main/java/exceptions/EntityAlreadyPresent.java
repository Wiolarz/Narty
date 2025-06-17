package exceptions;

public class EntityAlreadyPresent extends Exception {
    public EntityAlreadyPresent() {
        super();
    }

    public EntityAlreadyPresent(String message) {
        super(message);
    }

}
