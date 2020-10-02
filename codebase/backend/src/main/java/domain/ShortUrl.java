package domain;

final public class ShortUrl {
    private final String url;
    private final String path;
    private final LongUrl longUrl;

    public ShortUrl(String path, String host, LongUrl longUrl) {
        this.longUrl = longUrl;
        this.path = path;
        this.url = String.format("%s/%s", host, path);
    }

    public ShortUrl(String path) {
        this.path = path;
        this.url = null;
        this.longUrl = null;
    }

    public LongUrl longURL() {
        return this.longUrl;
    }

    public String path() {
        return path;
    }

    @Override
    public String toString() {
        return url;
    }
}
