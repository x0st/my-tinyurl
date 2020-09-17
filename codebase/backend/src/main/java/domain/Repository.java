package domain;

public interface Repository {
    void persist(ShortUrl shortUrl);

    LongUrl findLongUrl(ShortUrl shortUrl);

    ShortUrl findShortUrl(LongUrl longUrl);
}
