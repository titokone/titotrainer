package fi.helsinki.cs.titotrainer.framework.request.coercer;

import fi.helsinki.cs.titotrainer.framework.misc.Maybe;
import fi.helsinki.cs.titotrainer.framework.misc.None;
import fi.helsinki.cs.titotrainer.framework.misc.Some;

/**
 * Parses simple string representations of integers.
 */
public class LongCoercer extends SimpleFieldCoercer<Long> {

    @Override
    public Class<Long> getResultType() {
        return Long.class;
    }
    
    @Override
	public Maybe<Long> coerce(String value) {
		try {
			return new Some<Long>(Long.parseLong(value));
		} catch (NumberFormatException e) {
		}
		
		return new None<Long>();
	}

}
