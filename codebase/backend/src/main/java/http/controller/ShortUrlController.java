package http.controller;

import business.shorturl.ShortUrl;
import business.shorturl.ShortUrlRepository;
import core.repository.EntryNotFoundException;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;

final public class ShortUrlController {
    private final ShortUrlRepository shortUrlRepository;

    public ShortUrlController(ShortUrlRepository shortUrlRepository) {
        this.shortUrlRepository = shortUrlRepository;
    }

    public void resolve(Context context) {
        String uid;
        ShortUrl shortUrl;

        try {
            uid = context.pathParam("uid");
            shortUrl = this.shortUrlRepository.find(uid);
        } catch (EntryNotFoundException e) {
            throw new NotFoundResponse("Unable to find a record with such a url.");
        }

        context.redirect(shortUrl.url);
    }
}
