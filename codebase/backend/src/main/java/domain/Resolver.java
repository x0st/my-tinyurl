package domain;

public interface Resolver {
    public LongUrl resolve(ShortUrl shortUrl) throws NoUrlFound;
}
