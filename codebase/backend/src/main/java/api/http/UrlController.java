package api.http;

import com.alibaba.fastjson.JSONObject;
import com.typesafe.config.Config;

import domain.LongUrl;
import domain.NoUrlFound;
import domain.Resolver;
import domain.ShortUrl;
import domain.Shortener;
import io.javalin.http.Context;

final public class UrlController {
    private final Shortener shortener;
    private final Resolver resolver;
    private final Config config;

    public UrlController(Shortener shortener, Resolver resolver, Config config) {
        this.shortener = shortener;
        this.resolver = resolver;
        this.config = config;
    }

    public void shorten(Context context) {
        LongUrl longUrl = new LongUrl(context.formParam("url"));
        ShortUrl shortUrl = this.shortener.shorten(longUrl);

        JSONObject jsonResponse = new JSONObject()
                .fluentPut("data", new JSONObject()
                        .fluentPut("url", shortUrl.getUrl()));

        context.result(jsonResponse.toJSONString());
        context.header("Content-Type", "application/json");
        context.status(201);
    }

    public void resolve(Context context) {
        ShortUrl shortUrl = new ShortUrl(context.pathParam("hash"));

        try {
            LongUrl longUrl = this.resolver.resolve(shortUrl);

            context.redirect(longUrl.getUrl());
        } catch (NoUrlFound ex) {
            context.redirect(this.config.getString("app.url"));
        }
    }
}
