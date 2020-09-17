package domain;

final public class ShortUrl {
    private final String url;
    private final String hash;
    private final LongUrl longUrl;

    public ShortUrl(String hash, String url, LongUrl longUrl) {
        this.longUrl = longUrl;
        this.hash = hash;
        this.url = url;
    }

    public ShortUrl(String hash) {
        this.hash = hash;
        this.url = null;
        this.longUrl = null;
    }

    public LongUrl getLongUrl() {
        return this.longUrl;
    }

    public String getUrl() {
        return url;
    }

    public String getHash() {
        return hash;
    }
}
