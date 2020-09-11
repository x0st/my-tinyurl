package http.controller;

import java.util.concurrent.TimeUnit;

import business.shorturl.ShortUrl;
import business.shorturl.ShortUrlRepository;
import core.http.validator.ValidatorFactory;
import uid.UID;
import http.validator.longurl.Alias;
import io.javalin.http.Context;

final public class LongUrlController {
    final private UID uidGenerator;
    final private ValidatorFactory validatorFactory;
    final private ShortUrlRepository shortUrlRepository;

    public LongUrlController(UID generator, ValidatorFactory validatorFactory, ShortUrlRepository shortUrlRepository) {
        this.uidGenerator = generator;
        this.validatorFactory = validatorFactory;
        this.shortUrlRepository = shortUrlRepository;
    }

    public void alias(Context context) {
        this.validatorFactory.make(Alias.class).validate(context);

        ShortUrl shortUrl;

        shortUrl = new ShortUrl();
        shortUrl.uid = this.uidGenerator.uid();
        shortUrl.url = context.formParam("url");

        this.shortUrlRepository.persist(shortUrl);

        context.json(shortUrl);
    }
}
