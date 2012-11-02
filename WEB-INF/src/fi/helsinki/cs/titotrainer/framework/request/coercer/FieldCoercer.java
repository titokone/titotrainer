package fi.helsinki.cs.titotrainer.framework.request.coercer;

import java.lang.annotation.Annotation;
import java.util.Map;

import fi.helsinki.cs.titotrainer.framework.misc.Maybe;
import fi.helsinki.cs.titotrainer.framework.misc.None;
import fi.helsinki.cs.titotrainer.framework.misc.Some;

/**
 * <p>Interface to a class that converts a string to another type.</p>
 * 
 * <p>Must be thread-safe.</p>
 */
public interface FieldCoercer<FieldType> {
    
    /**
     * Returns the type of values this coercer can produce.
     * 
     * @return The type of field that this coercer matches.
     */
    public Class<FieldType> getResultType();
    
    /**
	 * <p>Attempts to create a value of type T from a string.</p>
	 * 
	 * <p>Because null is a valid result, the return value is put
	 * inside a {@link Maybe} wrapper.</p>
	 * 
	 * @param field The string value to coerce. Not null.
	 * @param params The parameter map from which fields are coerced.
	 *               Some coercers might look at several parameters, but most
	 *               only look at the parameter named {@code field}.
     * @param annotations The field's annotations.
	 * @return {@link Some} if the value could be coerced, {@link None} otherwise.
	 * @throws NullPointerException if {@code field} or {@code params} is null (might also not throw).
	 */
	public Maybe<FieldType> coerce(String field, Map<String, ?> params, Annotation[] annotations);
}
