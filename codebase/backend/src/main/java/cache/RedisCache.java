package cache;

import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;
import core.cache.Cache;

final public class RedisCache implements Cache {
    private final RedissonClient redis;

    public RedisCache(RedissonClient redis) {
        this.redis = redis;
    }

    @Override
    public void put(String key, Object object, long ttl, TimeUnit timeUnit) {
        this.redis.getBucket(key).set(object, ttl, timeUnit);
    }

    @Override
    public Object get(String key) {
        return this.redis.getBucket(key).get();
    }
}
