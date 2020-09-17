package api.http;

import com.alibaba.fastjson.JSONObject;

import domain.LongUrl;
import domain.NoUrlFound;
import domain.Resolver;
import domain.ShortUrl;
import domain.Shortener;
import io.javalin.http.Context;

final public class UrlController {
    private final Shortener shortener;
    private final Resolver resolver;

    public UrlController(Shortener shortener, Resolver resolver) {
        this.shortener = shortener;
        this.resolver = resolver;
    }

    public void shorten(Context context) {
        LongUrl longUrl = new LongUrl(context.formParam("url"));
        ShortUrl shortUrl = this.shortener.shorten(longUrl);

        JSONObject jsonResponse = new JSONObject()
                .fluentPut("data", new JSONObject()
                        .fluentPut("url", shortUrl.getUrl()));

        context.result(jsonResponse.toJSONString());
        context.header("Content-Type", "application/json");
        context.status(200);
    }

    public void resolve(Context context) throws NoUrlFound {
        ShortUrl shortUrl = new ShortUrl(context.pathParam("hash"));
        LongUrl longUrl = this.resolver.resolve(shortUrl);

        context.header("Location", longUrl.getUrl());
        context.status(301);
    }
}
