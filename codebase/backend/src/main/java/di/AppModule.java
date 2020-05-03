package di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import bootstrap.BootstrapRedis;
import core.http.validator.ValidatorFactory;
import uid.Base62;
import uid.Generator;
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
    static Generator provideGenerator(RedissonClient redissonClient, Base62 base62) {
        String[] range = System.getenv("RANGE").split("-");

        return new Generator(Long.valueOf(range[0]), Long.valueOf(range[1]), redissonClient, base62);
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
    static LongUrlController provideLongUrlController(Generator generator, ValidatorFactory validatorFactory) {
        return new LongUrlController(generator, validatorFactory);
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
