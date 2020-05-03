package bootstrap;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;

public class BootstrapRedis implements Bootstrap {
    private final RedissonClient redis;

    public BootstrapRedis(RedissonClient redis) {
        this.redis = redis;
    }

    @Override
    public void bootstrap() {
        RAtomicLong counterRef;
        String[] range;
        long rangeStart;
        long rangeEnd;
        String counterKey;

        range = System.getenv("RANGE").split("-");
        rangeStart = Long.parseLong(range[0]);
        rangeEnd = Long.parseLong(range[1]);
        counterKey = String.format("range:%s-%s", rangeStart, rangeEnd);

        counterRef = this.redis.getAtomicLong(counterKey);

        if (!counterRef.isExists()) counterRef.set(rangeStart);
        else if (counterRef.get() >= rangeEnd)
            throw new RuntimeException(String.format("Exceeded the allowed range %s-%s", rangeStart, rangeEnd));
    }
}
