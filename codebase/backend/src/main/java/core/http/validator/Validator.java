package core.http.validator;

import io.javalin.http.Context;

public interface Validator {
    public void validate(Context context);
}