package domain;

import core.DomainException;

final public class NoUrlFound extends DomainException {
    public NoUrlFound(String message) {
        super(message);
    }
}
