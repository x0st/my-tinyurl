package infrastructure;

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
        this.cache.put(shortUrl.getHash(), shortUrl.getLongUrl().getUrl(), 2 * 24 * 60 * 60);
    }

    @Override
    public LongUrl findLongUrl(ShortUrl shortUrl) {
        // 1. Look it up in cache
        String longUrl = this.cache.get(shortUrl.getHash());
        if (null != longUrl) {
            return new LongUrl(longUrl);
        }

        // 2. Try to find it in the db
        LongUrl longUrlObj = this.repository.findLongUrl(shortUrl);
        if (null == longUrlObj) {
            return null;
        }

        this.cache.put(shortUrl.getHash(), longUrlObj.getUrl(), 2 * 2 * 60 * 60);
        return longUrlObj;
    }

    @Override
    public ShortUrl findShortUrl(LongUrl longUrl) {
        // 1. Look it up in cache
        String hash = this.cache.get(longUrl.getUrl());
        if (null != hash) {
            return new ShortUrl(hash);
        }

        // 2. Try to find it in the db
        ShortUrl shortUrl = this.repository.findShortUrl(longUrl);
        if (null == shortUrl) {
            return null;
        }

        this.cache.put(longUrl.getUrl(), shortUrl.getHash(), 2 * 2 * 60 * 60);
        return shortUrl;
    }
}
