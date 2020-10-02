package infrastructure;

import java.util.concurrent.TimeUnit;

import core.Cache;
import domain.LongUrl;
import domain.Repository;
import domain.ShortUrl;

final class RepositoryCacheDecorator implements Repository {
    private final Repository repository;
    private final Cache cache;

    RepositoryCacheDecorator(Repository repository, Cache cache) {
        this.repository = repository;
        this.cache = cache;
    }

    @Override
    public void persist(ShortUrl shortUrl) {
        this.repository.persist(shortUrl);
        this.cache.put(shortUrl.path(), shortUrl.longURL().toString(), 2, TimeUnit.DAYS);
    }

    @Override
    public LongUrl find(ShortUrl shortUrl) {
        String longUrl = this.cache.get(shortUrl.path());
        if (null != longUrl) return new LongUrl(longUrl);
        LongUrl longUrlObj = this.repository.find(shortUrl);
        this.cache.put(shortUrl.path(), longUrlObj.toString(), 2, TimeUnit.HOURS);
        return longUrlObj;
    }

    @Override
    public ShortUrl find(LongUrl longUrl) {
        String hash = this.cache.get(longUrl.toString());
        if (null != hash) return new ShortUrl(hash);
        ShortUrl shortUrl = this.repository.find(longUrl);
        this.cache.put(longUrl.toString(), shortUrl.path(),2, TimeUnit.HOURS);
        return shortUrl;
    }
}
