package infrastructure;

import domain.LongUrl;
import domain.Repository;
import domain.ShortUrl;

final class RepositoryImpl implements Repository {
    @Override
    public void persist(ShortUrl shortUrl) {

    }

    @Override
    public LongUrl findLongUrl(ShortUrl shortUrl) {
        return new LongUrl("http://test.test");
    }

    @Override
    public ShortUrl findShortUrl(LongUrl longUrl) {
        return new ShortUrl("hash");
    }
}
