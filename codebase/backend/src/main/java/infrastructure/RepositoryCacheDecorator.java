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
        this.cache.put(shortUrl.getHash(), shortUrl.getLongUrl().getUrl(), 2, TimeUnit.DAYS);
    }

    @Override
    public LongUrl findLongUrl(ShortUrl shortUrl) {
        String longUrl = this.cache.get(shortUrl.getHash());
        if (null != longUrl) return new LongUrl(longUrl);
        LongUrl longUrlObj = this.repository.findLongUrl(shortUrl);
        this.cache.put(shortUrl.getHash(), longUrlObj.getUrl(), 2, TimeUnit.HOURS);
        return longUrlObj;
    }

    @Override
    public ShortUrl findShortUrl(LongUrl longUrl) {
        String hash = this.cache.get(longUrl.getUrl());
        if (null != hash) return new ShortUrl(hash);
        ShortUrl shortUrl = this.repository.findShortUrl(longUrl);
        this.cache.put(longUrl.getUrl(), shortUrl.getHash(),2, TimeUnit.HOURS);
        return shortUrl;
    }
}
