

import com.google.inject.Guice;
import com.google.inject.Injector;

import java.util.ArrayList;
import java.util.List;

import core.Bootstrap;
import bootstrap.BootstrapRedis;
import core.http.validator.exception.ValidationException;
import di.AppModule;
import http.controller.LongUrlController;
import io.javalin.Javalin;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.HttpResponseExceptionMapper;

public class Application {
    final private List<Bootstrap> bootstrapList;
    final private Injector injector;
    final private Javalin javalin;

    public static void main(String[] args) {
        new Application().run();
    }

    private Application() {
        this.javalin = Javalin.create();
        this.injector = Guice.createInjector(new AppModule());

        this.bootstrapList = new ArrayList<>(1);
        this.bootstrapList.add(this.injector.getInstance(BootstrapRedis.class));

        this.bootstrap();
        this.mapExceptions();
        this.defineRoutes();
    }

    private void bootstrap() {
        this.bootstrapList.forEach(Bootstrap::bootstrap);
        this.bootstrapList.clear();
    }

    private void mapExceptions() {
        this.javalin.exception(
                ValidationException.class,
                (e, ctx) -> HttpResponseExceptionMapper.INSTANCE.handle(new BadRequestResponse(e.getMessage()), ctx)
        );
    }

    private void defineRoutes() {
        this.javalin.post("/api/alias", ctx -> this.injector.getInstance(LongUrlController.class).alias(ctx));
    }

    public void run() {
        this.javalin.start(80);
    }
}
