package fi.helsinki.cs.titotrainer.framework.request.coercer;

import java.lang.annotation.Annotation;
import java.util.Map;

import fi.helsinki.cs.titotrainer.framework.misc.Maybe;
import fi.helsinki.cs.titotrainer.framework.misc.None;

/**
 * An abstract class to help implement field coercers that don't need most of the parameter map.
 */
public abstract class SimpleFieldCoercer<FieldType> implements FieldCoercer<FieldType> {
    
    @Override
    public final Maybe<FieldType> coerce(String field, Map<String, ?> params, Annotation[] annotations) {
        Object obj = params.get(field);
        if (obj != null)
            return coerce(obj.toString());
        else
            return new None<FieldType>();
    }
    
    /**
     * Coerces a string value.
     * 
     * @param value The string value to coerce. Never null.
     * @return The coerced value, or {@link None}.
     */
    public abstract Maybe<FieldType> coerce(String value);

}
