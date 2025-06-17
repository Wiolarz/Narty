package exceptions;

public class UserAlreadyPresent extends Exception {
    public UserAlreadyPresent() {
        super();
    }

    public UserAlreadyPresent(String message) {
        super(message);
    }
}
