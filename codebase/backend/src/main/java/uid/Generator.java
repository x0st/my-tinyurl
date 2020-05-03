package uid;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

final public class Generator {
    private final Long rangeStart;
    private final Long rangeEnd;

    private final Base62 base62;
    private final RedissonClient redis;

    private final String counterKey;

    private final AtomicLong counter = new AtomicLong(0);

    public Generator(Long rangeStart, Long rangeEnd, RedissonClient redis, Base62 base62) {
        if (rangeEnd <= rangeStart) throw new RuntimeException("The end must be greater than the start.");

        this.counterKey = String.format("range:%s-%s", rangeStart, rangeEnd);

        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;

        this.redis = redis;
        this.base62 = base62;
    }

    public String uid() {
        if (this.counter.get() >= this.rangeEnd) throw new RuntimeException("Reached the end.");

        RAtomicLong counterRef;
        long counterValue;

        counterRef = this.redis.getAtomicLong(this.counterKey);
        counterValue = counterRef.incrementAndGet();

        this.counter.set(counterValue);

        return this.base62.encodeBase10(counterValue);
    }
}
