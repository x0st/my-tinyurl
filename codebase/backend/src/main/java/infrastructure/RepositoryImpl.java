package infrastructure;

import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.api.commands.kv.FetchValue;
import com.basho.riak.client.api.commands.kv.StoreValue;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.basho.riak.client.core.query.RiakObject;
import com.basho.riak.client.core.util.BinaryValue;

import java.util.concurrent.TimeUnit;

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
        Namespace riakNamespace;
        Location riakLocation;
        RiakObject riakObject;
        StoreValue riakCommand;

        String hash = shortUrl.getHash();
        String longUrl = shortUrl.getLongUrl().getUrl();

        riakNamespace = new Namespace("short_urls");
        riakLocation = new Location(riakNamespace, hash);
        riakObject = new RiakObject().setContentType("text/plain").setValue(BinaryValue.create(longUrl));
        riakCommand = new StoreValue.Builder(riakObject)
                .withLocation(riakLocation)
                .build();

        try {
            this.riakClient.execute(riakCommand);
            this.cache.put(hash, longUrl, 2, TimeUnit.DAYS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        riakNamespace = new Namespace("long_urls");
        riakLocation = new Location(riakNamespace, longUrl);
        riakObject = new RiakObject().setContentType("text/plain").setValue(BinaryValue.create(hash));
        riakCommand = new StoreValue.Builder(riakObject)
                .withLocation(riakLocation)
                .build();

        this.riakClient.executeAsync(riakCommand);
    }

    @Override
    public LongUrl findLongUrl(ShortUrl shortUrl) {
        String hash = shortUrl.getHash();
        String cachedLongUrl = this.cache.get(hash);

        if (null != cachedLongUrl) return new LongUrl(cachedLongUrl);

        Namespace riakNamespace = new Namespace("short_urls");
        Location riakLocation = new Location(riakNamespace, hash);
        FetchValue riakCommand = new FetchValue.Builder(riakLocation).build();

        try {
            FetchValue.Response riakResponse = this.riakClient.execute(riakCommand);
            if (riakResponse.isNotFound()) return null;
            String longUrl = riakResponse.getValue(RiakObject.class).getValue().toString();

            this.cache.put(hash, longUrl, 2, TimeUnit.DAYS);

            return new LongUrl(longUrl);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ShortUrl findShortUrl(LongUrl longUrlObj) {
        String longUrl = longUrlObj.getUrl();
        String cachedShortUrlHash = this.cache.get(longUrl);

        if (null != cachedShortUrlHash) return new ShortUrl(cachedShortUrlHash);

        Namespace riakNamespace = new Namespace("long_urls");
        Location riakLocation = new Location(riakNamespace, longUrl);
        FetchValue riakCommand = new FetchValue.Builder(riakLocation).build();

        try {
            FetchValue.Response riakResponse = this.riakClient.execute(riakCommand);
            if (riakResponse.isNotFound()) return null;
            String hash = riakResponse.getValue(RiakObject.class).getValue().toString();

            this.cache.put(longUrl, hash, 2, TimeUnit.DAYS);

            return new ShortUrl(hash);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
