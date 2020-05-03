package http.validator.longurl;

import core.http.validator.Validator;
import core.http.validator.exception.ValidationException;
import io.javalin.http.Context;

final public class Alias implements Validator {
    @Override
    public void validate(Context context) {
        if (null == context.formParam("url"))
            throw new ValidationException("Invalid url.");
    }
}
