package infrastructure;

import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.core.RiakCluster;
import com.basho.riak.client.core.RiakNode;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import api.http.UrlController;
import core.Base62Encoder;
import core.Cache;
import domain.Repository;
import domain.Resolver;
import domain.Shortener;

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
    static RedissonClient provideRedissonClient(Config config) {
        org.redisson.config.Config redissonConfig;
        redissonConfig = new org.redisson.config.Config();
        redissonConfig
                .useSingleServer()
                .setDatabase(0)
                .setAddress(config.getString("redis.url"));

        return Redisson.create(redissonConfig);
    }

    @Provides
    @Singleton
    static Cache provideCache(RedissonClient redissonClient) {
        return new RedisCache(redissonClient);
    }

    @Provides
    @Singleton
    static Repository provideRepository(RiakClient riakClient, Cache cache) {
        return new RepositoryImpl(riakClient, cache);
    }

    @Provides
    @Singleton
    static CounterLog provideCounterLog(Config config) throws IOException {
        return new CounterLog(
                config.getString("counter.log.dir"),
                3
        );
    }

    @Provides
    @Singleton
    static Counter provideCounter(CounterLog counterLog, Config config) throws CounterException {
        int[] range = new int[] {
                config.getInt("range.beginning"),
                config.getInt("range.end")
        };

        return new Counter(range, counterLog);
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
    static UrlController provideUrlController(Shortener shortener, Resolver resolver) {
        return new UrlController(shortener, resolver);
    }
}
