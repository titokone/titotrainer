package fi.helsinki.cs.titotrainer.framework.request.coercer;

import java.lang.reflect.Array;

import org.apache.commons.lang.StringUtils;

import fi.helsinki.cs.titotrainer.framework.misc.Maybe;
import fi.helsinki.cs.titotrainer.framework.misc.None;
import fi.helsinki.cs.titotrainer.framework.misc.Some;

/**
 * Parses a comma-separated list into an array.
 */
public class ArrayCoercer<T> extends SimpleFieldCoercer<T[]> {

    private SimpleFieldCoercer<T> elementCoercer;
    
    public ArrayCoercer(SimpleFieldCoercer<T> elementCoercer) {
        this.elementCoercer = elementCoercer;
    }

    @SuppressWarnings("unchecked")
    protected T[] createArray(int length) {
        return (T[])Array.newInstance(this.elementCoercer.getResultType(), length);
    }
    
    @Override
    public Maybe<T[]> coerce(String value) {
        String[] parts = StringUtils.split(value, ',');
        T[] result = this.createArray(parts.length);
        for (int i = 0; i < parts.length; ++i) {
            Maybe<T> element = elementCoercer.coerce(parts[i].trim());
            if (element.hasValue())
                result[i] = element.getValue();
            else
                return new None<T[]>();
        }
        
        return new Some<T[]>(result);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Class<T[]> getResultType() {
        T[] mock = this.createArray(0);
        return (Class<T[]>)mock.getClass();
    }
    
}
