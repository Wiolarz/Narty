package exceptions;

public class UserNotPresent extends Exception {
    public UserNotPresent() {
        super();
    }

    public UserNotPresent(String message) {
        super(message);
    }
}
