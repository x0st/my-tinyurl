package infrastructure;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.util.ArrayList;

import core.Base62Encoder;
import domain.LongUrl;
import domain.PathAlreadyTaken;
import domain.Repository;
import domain.ShortUrl;
import domain.Shortener;

final class ShortenerImpl implements Shortener {
    private final String appUrl;
    private final Counter counter;
    private final Repository repository;
    private final ZooKeeper zooKeeper;
    private final Base62Encoder base62Encoder;

    ShortenerImpl(Counter counter, Base62Encoder base62, String appUrl, Repository repository, ZooKeeper zooKeeper) {
        this.base62Encoder = base62;
        this.repository = repository;
        this.zooKeeper = zooKeeper;
        this.counter = counter;
        this.appUrl = appUrl;
    }

    @Override
    public ShortUrl shorten(LongUrl longUrl) {
        ShortUrl shortUrlObj = this.repository.find(longUrl);

        if (null != shortUrlObj) {
            return new ShortUrl(shortUrlObj.path(), this.appUrl, longUrl);
        }

        try {
            long counter = this.counter.increment();
            String hash = this.base62Encoder.encodeBase10(counter);
            shortUrlObj = new ShortUrl(hash, this.appUrl, longUrl);

            this.repository.persist(shortUrlObj);

            return shortUrlObj;
        } catch (CounterException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public ShortUrl shorten(LongUrl url, String path) throws PathAlreadyTaken {
        // 1. Check whether the given path has already been taken
        try {
            zooKeeper.create(String.format("/custom_short_urls/%s", path), new byte[]{}, new ArrayList<>(), CreateMode.PERSISTENT);
        } catch (KeeperException e) {
            if (e.code() == KeeperException.Code.NODEEXISTS) {
                throw new PathAlreadyTaken(String.format("Path '%s' is already taken", path));
            }

            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (null != this.repository.find(new ShortUrl(path))) {
            throw new PathAlreadyTaken(String.format("Path '%s' is already taken", path));
        }

        // 2. Persist
        ShortUrl shortUrl = new ShortUrl(path, this.appUrl, url);
        this.repository.persist(shortUrl);

        return shortUrl;
    }
}
