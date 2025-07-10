package exception;

public class IncorrectTaskException extends RuntimeException {
    public IncorrectTaskException(String message) {
        super(message);
    }
}
