package di;

import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.core.RiakCluster;
import com.basho.riak.client.core.RiakNode;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import bootstrap.BootstrapRedis;
import business.shorturl.ShortUrlRepository;
import cache.RedisCache;
import core.cache.Cache;
import core.http.validator.ValidatorFactory;
import core.Base62;
import uid.Counter;
import uid.UID;
import http.controller.LongUrlController;
import http.validator.longurl.Alias;

public class AppModule extends AbstractModule {
    @Singleton
    @Provides
    static Base62 provideBase62() {
        return new Base62();
    }

    @Singleton
    @Provides
    static Counter provideCounter(RiakClient riak) {
        String[] range = System.getenv("RANGE").split("-");

        return new Counter(Long.valueOf(range[0]), Long.valueOf(range[1]), riak);
    }

    @Singleton
    @Provides
    static ShortUrlRepository provideShortUrlRepository(RiakClient riak, Cache cache) {
        return new ShortUrlRepository(riak, cache);
    }

    @Singleton
    @Provides
    static UID provideUID(Base62 base62, Counter counter) {
        return new UID(base62, counter);
    }

    @Singleton
    @Provides
    static RiakClient provideRiakClient() throws UnknownHostException {
        RiakNode.Builder builder;
        List<String> addresses;
        List<RiakNode> nodes;
        RiakCluster cluster;

        builder = new RiakNode.Builder();
        builder.withMinConnections(10);
        builder.withMaxConnections(50);

        addresses = new LinkedList<String>(Arrays.asList(System.getenv("RIAK_HOSTS").split(",")));

        nodes = RiakNode.Builder.buildNodes(builder, addresses);
        cluster = new RiakCluster.Builder(nodes).build();
        cluster.start();

        return new RiakClient(cluster);
    }

    @Singleton
    @Provides
    static Cache provideCache(RedissonClient redis) {
        return new RedisCache(redis);
    }

    @Singleton
    @Provides
    static RedissonClient provideRedissonClient() {
        Config config;

        config = new Config();
        config.useSingleServer().setAddress("redis://" + System.getenv("REDIS_HOST"));

        return Redisson.create(config);
    }

    @Provides
    @Singleton
    static LongUrlController provideLongUrlController(UID generator, ValidatorFactory validatorFactory, ShortUrlRepository shortUrlRepository) {
        return new LongUrlController(generator, validatorFactory, shortUrlRepository);
    }

    @Provides
    @Singleton
    static BootstrapRedis provideBootstrapRedis(RedissonClient redissonClient) {
        return new BootstrapRedis(redissonClient);
    }

    @Provides
    @Singleton
    static ValidatorFactory provideValidatorFactory() {
        ValidatorFactory instance;

        instance = new ValidatorFactory();
        instance.push(new Alias());

        return instance;
    }
}
