package infrastructure;

import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.api.commands.kv.FetchValue;
import com.basho.riak.client.api.commands.kv.StoreValue;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.basho.riak.client.core.query.RiakObject;
import com.basho.riak.client.core.util.BinaryValue;

import domain.LongUrl;
import domain.Repository;
import domain.ShortUrl;

final class RepositoryRiakImpl implements Repository {
    private final RiakClient riakClient;

    RepositoryRiakImpl(RiakClient riakClient) {
        this.riakClient = riakClient;
    }

    @Override
    public void persist(ShortUrl shortUrl) {
        String hash = shortUrl.getHash();
        String longUrl = shortUrl.getLongUrl().getUrl();

        try {
            this.riakStore(String.format("urls/short_urls/%s", hash), longUrl);
            this.riakStoreAsync(String.format("urls/long_urls/%s", longUrl), hash);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public LongUrl findLongUrl(ShortUrl shortUrl) {
        String longUrl;
        LongUrl longUrlObj;

        try {
            longUrl = this.riakFetch(String.format("urls/short_urls/%s", shortUrl.getHash()));
            if (null == longUrl) return null;
            longUrlObj = new LongUrl(longUrl);

            return longUrlObj;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ShortUrl findShortUrl(LongUrl longUrlObj) {
        String hash;
        ShortUrl shortUrlObj;

        try {
            hash = this.riakFetch(String.format("urls/long_urls/%s", longUrlObj.getUrl()));
            if (null == hash) return null;
            shortUrlObj = new ShortUrl(hash);

            return shortUrlObj;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String riakFetch(String path) throws Exception {
        String[] split = path.split("/");

        String bucketType = split[0];
        String bucketName = split[1];
        String key = split[2];

        Namespace namespace = new Namespace(bucketType, bucketName);
        Location location = new Location(namespace, key);
        FetchValue command = new FetchValue.Builder(location).build();

        FetchValue.Response response = this.riakClient.execute(command);
        if (response.isNotFound()) return null;

        return response.getValue(RiakObject.class).getValue().toString();
    }

    private void riakStore(String path, String value) throws Exception {
        String[] split = path.split("/");

        String bucketType = split[0];
        String bucketName = split[1];
        String key = split[2];

        Namespace riakNamespace = new Namespace(bucketType, bucketName);
        Location riakLocation = new Location(riakNamespace, key);
        RiakObject riakObject = new RiakObject().setContentType("text/plain").setValue(BinaryValue.create(value));
        StoreValue riakCommand = new StoreValue.Builder(riakObject)
                .withLocation(riakLocation)
                .build();

        this.riakClient.execute(riakCommand);
    }

    private void riakStoreAsync(String path, String value) {
        String[] split = path.split("/");

        String bucketType = split[0];
        String bucketName = split[1];
        String key = split[2];

        Namespace riakNamespace = new Namespace(bucketType, bucketName);
        Location riakLocation = new Location(riakNamespace, key);
        RiakObject riakObject = new RiakObject().setContentType("text/plain").setValue(BinaryValue.create(value));
        StoreValue riakCommand = new StoreValue.Builder(riakObject)
                .withLocation(riakLocation)
                .build();

        this.riakClient.executeAsync(riakCommand);
    }
}
