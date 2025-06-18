package exceptions;

public class UserAlreadyPresent extends SkiAppException {
    public UserAlreadyPresent() {
        super();
    }

    public UserAlreadyPresent(String message) {
        super(message);
    }
}
