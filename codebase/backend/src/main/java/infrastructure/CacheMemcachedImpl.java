package infrastructure;

import net.spy.memcached.MemcachedClientIF;
import net.spy.memcached.OperationTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import core.Cache;

final class CacheMemcachedImpl implements Cache {
    private final MemcachedClientIF memcached;
    private final Logger logger;

    CacheMemcachedImpl(MemcachedClientIF client) {
        this.memcached = client;
        this.logger = LoggerFactory.getLogger(CacheMemcachedImpl.class);
    }

    @Override
    public String get(String key) {
        try {
            Object response = this.memcached.get(key);

            if (null == response) return null;
            return String.valueOf(response);
        } catch (OperationTimeoutException | IllegalStateException e) {
            this.logger.error("Unable to fetch a value from memcached", e);
        }

        return null;
    }

    @Override
    public void put(String key, String value, int ttl) {
        try {
            this.memcached.set(key, ttl, value);
        } catch (IllegalStateException e) {
            this.logger.error("Unable to set a value in memcached", e);
        }
    }
}
