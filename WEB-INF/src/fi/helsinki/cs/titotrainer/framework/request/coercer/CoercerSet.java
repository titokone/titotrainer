package fi.helsinki.cs.titotrainer.framework.request.coercer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.ClassUtils;

/**
 * A collection of coercers.
 */
public class CoercerSet implements Iterable<FieldCoercer<?>> {
    
    protected ArrayList<FieldCoercer<?>> coercers;
    
    public CoercerSet() {
        this.coercers = new ArrayList<FieldCoercer<?>>();
    }
    
    public List<FieldCoercer<?>> getCoercers() {
        return coercers;
    }
    
    @Override
    public Iterator<FieldCoercer<?>> iterator() {
        return coercers.iterator();
    }
    
    /**
     * Finds a coercer for coercing to the target type.
     * 
     * @param destClass The type to coerce to.
     * @return A coercer that returns an object assignable to {@code destClass},
     *         or null if not found.
     */
    public FieldCoercer<?> findCoercer(Class<?> destClass) {
        // Look for an exact match
        for (FieldCoercer<?> c : this.coercers) {
            if (c.getResultType() == destClass) {
                return c;
            }
        }
        
        // Look for a primitive/wrapper match
        for (FieldCoercer<?> c : this.coercers) {
            if (destClass.isPrimitive()) {
                if (c.getResultType() == ClassUtils.primitiveToWrapper(destClass)) {
                    return c;
                }
            }
        }
        
        // Look for a close match
        for (FieldCoercer<?> c : this.coercers) {
            if (ClassUtils.isAssignable(c.getResultType(), destClass)) {
                return c;
            }
        }
        
        return null;
    }
    
}
