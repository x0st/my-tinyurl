package business.shorturl;

import com.basho.riak.client.api.RiakClient;

import java.util.concurrent.TimeUnit;

import core.cache.Cache;
import core.repository.EntryNotFoundException;

final public class ShortUrlRepository {
    final private RiakClient riak;
    final private Cache cache;

    public ShortUrlRepository(RiakClient riak, Cache cache) {
        this.cache = cache;
        this.riak = riak;
    }

    public void persist(ShortUrl shortUrl) {
        this.cache.put(shortUrl.uid, shortUrl.url, 2, TimeUnit.HOURS);
    }

    public ShortUrl find(String uid) {
        String cached;
        ShortUrl shortUrl;

        cached = (String) this.cache.get(uid);

        if (null == cached) throw new EntryNotFoundException();

        shortUrl = new ShortUrl();
        shortUrl.url = cached;
        shortUrl.uid = uid;

        return shortUrl;
    }
}
