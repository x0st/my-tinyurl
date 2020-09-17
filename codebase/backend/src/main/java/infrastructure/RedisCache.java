package infrastructure;

import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

import core.Cache;

final class RedisCache implements Cache {
    private final RedissonClient redissonClient;

    RedisCache(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public String get(String key) {
        return String.valueOf(this.redissonClient.getBucket(key).get());
    }

    @Override
    public void put(String key, String value, long TTL, TimeUnit timeUnit) {
        this.redissonClient.getBucket(key).set(value, TTL, timeUnit);
    }
}