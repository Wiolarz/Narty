package exceptions;

public class UserNotPresent extends SkiAppException {
    public UserNotPresent() {
        super();
    }

    public UserNotPresent(String message) {
        super(message);
    }
}
