package uid;

import core.Base62;

final public class UID {
    private final Base62 base62;
    private final Counter counter;

    public UID(Base62 base62, Counter counter) {
        this.base62 = base62;
        this.counter = counter;
    }

    public String uid() {
        return this.base62.encodeBase10(
                this.counter.next()
        );
    }
}
