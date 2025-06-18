package exceptions;

public class UserAlreadyPresentException extends SkiAppException {
    public UserAlreadyPresentException() {
        super();
    }

    public UserAlreadyPresentException(String message) {
        super(message);
    }
}
