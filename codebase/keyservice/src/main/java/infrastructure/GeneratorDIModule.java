package infrastructure;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.IOException;

import domain.KeyRepository;

final public class GeneratorDIModule extends AbstractModule {
    @Provides
    @Singleton
    static Config provideConfig() {
        return ConfigFactory.load();
    }

    @Provides
    @Singleton
    static Counter provideCounter(Config config) throws ExceededRangeException {
        int[] range = new int[] {
                config.getInt("generator.counter.from"),
                config.getInt("generator.counter.to"),
        };

        String file = config.getString("generator.counter.log");

        return new Counter(range, file);
    }

    @Provides
    @Singleton
    static KeyRepository provideKeyRepository(Config config) throws IOException {
        return new KeyRepositoryFileImpl(
                config.getString("keys.file")
        );
    }
}
