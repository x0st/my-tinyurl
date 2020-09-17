package core;

import java.util.concurrent.TimeUnit;

public interface Cache {
    public String get(String key);
    public void put(String key, String value, long TTL, TimeUnit timeUnit);
}
