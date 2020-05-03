package core.http.validator;

import java.util.HashMap;

final public class ValidatorFactory {
    HashMap<Class<? extends Validator>, Validator> store = new HashMap<>(5);

    public void push(Validator validator) {
        this.store.put(validator.getClass(), validator);
    }

    public Validator make(Class<? extends Validator> validatorClass) {
        return this.store.get(validatorClass);
    }
}
