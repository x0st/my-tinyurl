package infrastructure;

final class CounterException extends Exception {
    CounterException(String message) {
        super(message);
    }

    CounterException(Throwable throwable) {
        super(throwable);
    }
}
