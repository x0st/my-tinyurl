package domain;

public interface Shortener {
    ShortUrl shorten(LongUrl url);

    ShortUrl shorten(LongUrl url, String path) throws PathAlreadyTaken;
}
