package infrastructure;

import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

import core.Cache;

final class CacheRedisImpl implements Cache {
    private final RedissonClient redissonClient;

    CacheRedisImpl(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public String get(String key) {
        Object response = this.redissonClient.getBucket(key).get();

        if (null == response) return null;
        return String.valueOf(response);
    }

    @Override
    public void put(String key, String value, long TTL, TimeUnit timeUnit) {
        this.redissonClient.getBucket(key).set(value, TTL, timeUnit);
    }
}
