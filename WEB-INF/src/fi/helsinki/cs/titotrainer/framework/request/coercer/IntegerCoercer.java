package fi.helsinki.cs.titotrainer.framework.request.coercer;

import fi.helsinki.cs.titotrainer.framework.misc.Maybe;
import fi.helsinki.cs.titotrainer.framework.misc.None;
import fi.helsinki.cs.titotrainer.framework.misc.Some;

/**
 * Parses simple string representations of integers.
 */
public class IntegerCoercer extends SimpleFieldCoercer<Integer> {

    @Override
    public Class<Integer> getResultType() {
        return Integer.class;
    }
    
    @Override
	public Maybe<Integer> coerce(String value) {
		try {
			return new Some<Integer>(Integer.parseInt(value));
		} catch (NumberFormatException e) {
		}
		
		return new None<Integer>();
	}

}
