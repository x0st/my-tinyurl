package core;

import java.util.concurrent.TimeUnit;

public interface Cache {
    String get(String key);
    void put(String key, String value, long TTL, TimeUnit timeUnit);
}
