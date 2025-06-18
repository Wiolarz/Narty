package exceptions;

public class EntityAlreadyPresentException extends SkiAppException {
    public EntityAlreadyPresentException() {
        super();
    }

    public EntityAlreadyPresentException(String message) {
        super(message);
    }

}
