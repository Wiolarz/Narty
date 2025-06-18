package exceptions;

public class WritingException extends SkiAppException {
    public WritingException() {
        super();
    }

    public WritingException(String message) {
        super(message);
    }

    public WritingException(Throwable cause) {
        super(cause);
    }
}