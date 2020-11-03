package core;

public interface Cache {
    String get(String key);
    void put(String key, String value, int ttl);
}
