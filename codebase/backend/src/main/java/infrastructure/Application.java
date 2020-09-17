package infrastructure;

import com.google.inject.Guice;
import com.google.inject.Injector;

import api.http.UrlController;
import io.javalin.Javalin;

public class Application {
    final private Injector injector;
    final private Javalin javalin;

    public static void main(String[] args) throws Exception {
        new Application().run();
    }

    private Application() {
        this.javalin = Javalin.create();
        this.injector = Guice.createInjector(new DIModule());

        this.bootstrap();
        this.mapExceptions();
        this.defineRoutes();
    }

    private void bootstrap() {

    }

    private void mapExceptions() {

    }

    private void defineRoutes() {
        this.javalin.post("/api/v1/shortenUrl", this.injector.getInstance(UrlController.class)::shorten);
        this.javalin.get("/:hash", this.injector.getInstance(UrlController.class)::resolve);
    }

    public void run() {
        this.javalin.start(80);
    }
}
