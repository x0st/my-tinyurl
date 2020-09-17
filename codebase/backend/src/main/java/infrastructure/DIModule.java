package infrastructure;

import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.core.RiakCluster;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;

import java.io.IOException;
import java.net.UnknownHostException;

import api.http.UrlController;
import core.Base62Encoder;
import core.Cache;
import domain.Repository;
import domain.Resolver;
import domain.Shortener;

final class DIModule extends AbstractModule {
//    @Provides
//    @Singleton
//    static RiakClient provideRiakClient(Config config) throws UnknownHostException {
//        String[] servers = config.getString("riak.servers").split(",");
//        RiakClient riakClient = RiakClient.newClient(servers);
//
//        return riakClient;
//    }

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
    static Repository provideRepository() {
        return new RepositoryImpl(null);
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
    static Shortener provideShortener(Counter counter, Cache cache, Repository repository, Config config) {
        return new ShortenerImpl(
                counter,
                new Base62Encoder(),
                config.getString("app.url"),
                cache,
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
