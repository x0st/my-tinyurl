package core.cache;

import java.util.concurrent.TimeUnit;

public interface Cache {
    public void put(String key, Object object, long ttl, TimeUnit timeUnit);
    public Object get(String key);
}
