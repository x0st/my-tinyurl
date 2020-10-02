package domain;

public interface Repository {
    void persist(ShortUrl shortUrl);

    LongUrl find(ShortUrl shortUrl);

    ShortUrl find(LongUrl longUrl);
}
