package exceptions;

public class EntityAlreadyPresent extends SkiAppException {
    public EntityAlreadyPresent() {
        super();
    }

    public EntityAlreadyPresent(String message) {
        super(message);
    }

}
