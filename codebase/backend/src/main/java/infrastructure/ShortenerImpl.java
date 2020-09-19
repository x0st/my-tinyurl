package infrastructure;

import java.util.concurrent.TimeUnit;

import core.Base62Encoder;
import core.Cache;
import domain.LongUrl;
import domain.Repository;
import domain.ShortUrl;
import domain.Shortener;

final class ShortenerImpl implements Shortener {
    private final String appUrl;
    private final Counter counter;
    private final Repository repository;
    private final Base62Encoder base62Encoder;

    ShortenerImpl(Counter counter, Base62Encoder base62, String appUrl, Repository repository) {
        this.base62Encoder = base62;
        this.repository = repository;
        this.counter = counter;
        this.appUrl = appUrl;
    }

    @Override
    public ShortUrl shorten(LongUrl longUrl) {
        ShortUrl shortUrlObj;
        shortUrlObj = this.repository.findShortUrl(longUrl);

        if (null != shortUrlObj) {
            return new ShortUrl(
                    shortUrlObj.getHash(),
                    String.format("%s/%s", this.appUrl, shortUrlObj.getHash()),
                    longUrl
            );
        }

        try {
            long counter = this.counter.increment();
            String hash = this.base62Encoder.encodeBase10(counter);
            shortUrlObj = new ShortUrl(hash, String.format("%s/%s", this.appUrl, hash), longUrl);

            this.repository.persist(shortUrlObj);

            return shortUrlObj;
        } catch (CounterException exception) {
            throw new RuntimeException(exception);
        }
    }
}
