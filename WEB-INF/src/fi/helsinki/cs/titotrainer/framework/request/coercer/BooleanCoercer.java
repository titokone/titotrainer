package fi.helsinki.cs.titotrainer.framework.request.coercer;

import fi.helsinki.cs.titotrainer.framework.misc.Maybe;
import fi.helsinki.cs.titotrainer.framework.misc.Some;

/**
 * Coerces a string that is not empty nor 0 (after trimming) to true,
 * otherwise to false.
 */
public class BooleanCoercer extends SimpleFieldCoercer<Boolean> {
    
    @Override
    public Class<Boolean> getResultType() {
        return Boolean.class;
    }
    
    @Override
    public Maybe<Boolean> coerce(String value) {
        value = value.trim();
        return new Some<Boolean>(!(value.isEmpty() || value.equals("0")));
    }
    
}
