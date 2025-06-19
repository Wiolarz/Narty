package wit.io.exceptions;

public class UserNotPresentException extends SkiAppException {
    public UserNotPresentException() {
        super();
    }

    public UserNotPresentException(String message) {
        super(message);
    }
}
