package infrastructure;

import domain.LongUrl;
import domain.NoUrlFound;
import domain.Repository;
import domain.Resolver;
import domain.ShortUrl;

final class ResolverImpl implements Resolver {
    private final Repository repository;

    ResolverImpl(Repository repository) {
        this.repository = repository;
    }

    @Override
    public LongUrl resolve(ShortUrl shortUrl) throws NoUrlFound {
        LongUrl longUrl = this.repository.findLongUrl(shortUrl);

        if (null == longUrl) throw new NoUrlFound();

        return longUrl;
    }
}
