package domain;

import core.DomainException;

final public class PathAlreadyTaken extends DomainException {
    public PathAlreadyTaken(String message) {
        super(message);
    }
}
