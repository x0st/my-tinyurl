package http.controller;

import business.ShortUrl;
import core.http.validator.ValidatorFactory;
import uid.Generator;
import http.validator.longurl.Alias;
import io.javalin.http.Context;

final public class LongUrlController {
    final private Generator uidGenerator;
    final private ValidatorFactory validatorFactory;

    public LongUrlController(Generator generator, ValidatorFactory validatorFactory) {
        this.uidGenerator = generator;
        this.validatorFactory = validatorFactory;
    }

    public void alias(Context context) {
        this.validatorFactory.make(Alias.class).validate(context);

        ShortUrl shortUrl;
        String longUrl;
        String uid;

        longUrl = context.formParam("url");
        uid = this.uidGenerator.uid();

        shortUrl = new ShortUrl();
        shortUrl.uid = uid;
        shortUrl.url = longUrl;

        context.json(shortUrl);
    }
}
