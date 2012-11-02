package fi.helsinki.cs.titotrainer.framework.request.coercer;

import fi.helsinki.cs.titotrainer.framework.misc.Maybe;
import fi.helsinki.cs.titotrainer.framework.misc.Some;

/**
 * Returns the string value given.
 */
public class StringCoercer extends SimpleFieldCoercer<String> {
    @Override
    public Class<String> getResultType() {
        return String.class;
    }

    @Override
    public Maybe<String> coerce(String value) {
        return new Some<String>(value);
    }
    
}
