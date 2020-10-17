package infrastructure;

import com.google.inject.Guice;
import com.google.inject.Injector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import core.Base62Encoder;
import domain.Key;
import domain.KeyRepository;

final public class GeneratorApplication {
    private final Injector injector;
    private final Counter counter;
    private final Base62Encoder base62Encoder;
    private final KeyRepository keyRepository;
    private final Logger logger;

    public static void main(String[] args) {
        new GeneratorApplication().run();
    }

    private GeneratorApplication() {
        this.logger = LoggerFactory.getLogger(GeneratorApplication.class);
        this.injector = Guice.createInjector(new GeneratorDIModule());
        this.counter = this.injector.getInstance(Counter.class);
        this.keyRepository = this.injector.getInstance(KeyRepository.class);
        this.base62Encoder = new Base62Encoder();
    }

    private void run() {
        while (true) {
 
        }
    }

    private void generatePortion() {
        for (int i = 0; i < 10000; i++) {
            try {
                long counter = this.counter.increment();
                String base62 = this.base62Encoder.encode(counter);
                Key key = Key.create(base62);

                this.keyRepository.persist(key);

                this.logger.info(String.format("Generated key %s", base62));
                this.logger.info(String.format("Counter at %s", counter));
            } catch (ExceededRangeException e) {
                this.logger.error("Reached the upper limit");

                System.exit(1);
            }
        }
    }
}
