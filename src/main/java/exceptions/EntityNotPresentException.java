package exceptions;

public class EntityNotPresentException extends SkiAppException {
    public EntityNotPresentException() {
        super();
    }

    public EntityNotPresentException(String message) {
        super(message);
    }
}
