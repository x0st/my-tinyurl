package infrastructure;

import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.api.commands.kv.FetchValue;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;

import core.Cache;
import domain.LongUrl;
import domain.Repository;
import domain.ShortUrl;

final class RepositoryImpl implements Repository {
    private final RiakClient riakClient;
    private final Cache cache;

    RepositoryImpl(RiakClient riakClient, Cache cache) {
        this.cache = cache;
        this.riakClient = riakClient;
    }

    @Override
    public void persist(ShortUrl shortUrl) {

    }

    @Override
    public LongUrl findLongUrl(ShortUrl shortUrl) {
        String cachedLongUrl = this.cache.get(shortUrl.getHash());

        if (null != cachedLongUrl) return new LongUrl(cachedLongUrl);

        return null;
//        new Location(new Namespace(""))
//        new FetchValue.Builder()
    }

    @Override
    public ShortUrl findShortUrl(LongUrl longUrl) {
        return new ShortUrl("hash");
    }
}
