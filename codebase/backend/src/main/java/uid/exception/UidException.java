package uid.exception;

final public class UidException extends RuntimeException {
    public UidException(Throwable cause) {
        super(cause);
    }

    public UidException(String message) {
        super(message);
    }
}
