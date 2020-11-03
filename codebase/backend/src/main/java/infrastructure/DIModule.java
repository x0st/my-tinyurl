package infrastructure;

import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.core.RiakCluster;
import com.basho.riak.client.core.RiakNode;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import net.spy.memcached.KetamaConnectionFactory;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.MemcachedClientIF;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import api.http.UrlController;
import core.Base62Encoder;
import core.Cache;
import domain.Repository;
import domain.Resolver;
import domain.Shortener;
import infrastructure.counter.Counter;
import infrastructure.counter.CounterException;
import infrastructure.counter.CounterLog;
import infrastructure.counter.CounterSnapshotThread;

final class DIModule extends AbstractModule {
    @Provides
    @Singleton
    static RiakClient provideRiakClient(Config config) {
        List<RiakNode> riakNodes = new ArrayList<>(3);

        for (String server : config.getString("riak.servers").split(",")) {
            riakNodes.add(new RiakNode.Builder().withRemoteAddress(server).build());
        }

        RiakCluster riakCluster = new RiakCluster.Builder(riakNodes).build();
        riakCluster.start();

        RiakClient riakClient = new RiakClient(riakCluster);

        return riakClient;
    }

    @Provides
    @Singleton
    static Config provideConfig() {
        return ConfigFactory.load();
    }

    @Provides
    @Singleton
    static MemcachedClientIF provideMemcachedClient(Config config) throws IOException {
        List<InetSocketAddress> servers = new ArrayList<>();

        for (String server : config.getString("memcached.servers").split(",")) {
            servers.add(new InetSocketAddress(server, 11211));
        }

        return new MemcachedClient(new KetamaConnectionFactory(), servers);
    }

    @Provides
    @Singleton
    static Cache provideCache(MemcachedClientIF memcachedClient) {
        return new CacheMemcachedImpl(memcachedClient);
    }

    @Provides
    @Singleton
    static Repository provideRepository(RiakClient riakClient, Cache cache) {
        return new RepositoryCacheDecorator(
                new RepositoryRiakImpl(riakClient),
                cache
        );
    }

    @Provides
    @Singleton
    static CounterLog provideCounterLog(Config config) throws IOException {
        return new CounterLog(config.getString("counter.log.dir"));
    }

    @Provides
    @Singleton
    static Counter provideCounter(CounterLog counterLog, Config config) throws CounterException {
        long[] range = new long[] {
                config.getLong("range.beginning"),
                config.getLong("range.end")
        };

        return new Counter(range, counterLog.restore());
    }

    @Provides
    @Singleton
    static CounterSnapshotThread provideCounterSnapshotThread(Counter counter, CounterLog counterLog) {
        return new CounterSnapshotThread(
                counter,
                counterLog
        );
    }

    @Provides
    @Singleton
    static Shortener provideShortener(Counter counter, Repository repository, Config config) {
        return new ShortenerImpl(
                counter,
                new Base62Encoder(),
                config.getString("app.url"),
                repository
        );
    }

    @Provides
    @Singleton
    static Resolver provideResolver(Repository repository) {
        return new ResolverImpl(repository);
    }

    @Provides
    @Singleton
    static UrlController provideUrlController(Shortener shortener, Resolver resolver, Config config) {
        return new UrlController(shortener, resolver, config);
    }
}
