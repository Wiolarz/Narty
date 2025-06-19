package wit.io.exceptions;

public class ReadingException extends SkiAppException {
    public ReadingException() {
        super();
    }

    public ReadingException(String message) {
        super(message);
    }

    public ReadingException(Throwable cause) {
        super(cause);
    }
}