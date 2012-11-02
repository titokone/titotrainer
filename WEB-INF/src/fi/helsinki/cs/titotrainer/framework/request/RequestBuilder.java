package fi.helsinki.cs.titotrainer.framework.request;

import java.lang.annotation.Annotation;
import java.util.Map;

import fi.helsinki.cs.titotrainer.framework.misc.Maybe;
import fi.helsinki.cs.titotrainer.framework.request.coercer.CoercerSet;
import fi.helsinki.cs.titotrainer.framework.request.coercer.DefaultCoercerSet;
import fi.helsinki.cs.titotrainer.framework.request.coercer.FieldCoercer;

/**
 * <p>Builds a Request object given a map of parameters.</p>
 * 
 * <p>If you only want to make a copy of a request, you should
 * probably use {@link Request#copy(Class)} or {@link Request#copyTo(Request)}
 * instead.</p>
 * 
 * <p>This class is thread-safe.</p>
 * 
 * @see Request#copy(Class)
 * @see Request#copyTo(Request)
 */
public class RequestBuilder {
    
    private static RequestBuilder defaultInstance = new RequestBuilder();
    
	protected CoercerSet coercers;
	
	/**
	 * Returns the default request builder instance so this immutable object
	 * needn't be reinstantiated everywhere it's needed.
	 * @return The default request builder instance.
	 */
	public static RequestBuilder getDefaultInstance() {
	    return defaultInstance;
	}
	
    protected RequestBuilder() {
        this.coercers = DefaultCoercerSet.getInstance();
    }
    
    /**
     * <p>Builds a request object given a map of request parameters.</p>
     * 
     * @param params The parameter map.
     * @param dest The request to write parameters to.
     * @throws IllegalAccessException If the builder class had no access to the destination object's class.
     * @throws IllegalArgumentException If a field had a type for which there was no coercer or some other type mismatch occured.
     */
    public void buildOn(Map<String, ?> params, Request dest) throws InstantiationException, IllegalAccessException {
        for (String field : dest.getFields()) {
            Class<?> expectedType = dest.getFieldType(field);
            
            Annotation[] annotations = dest.reflectField(field).getAnnotations();
            
            FieldCoercer<?> fc = coercers.findCoercer(expectedType);
            if (fc == null) {
                throw new IllegalArgumentException("No type coercer for " + expectedType.getName() + " field '" + field + "'");
            }
            
            Maybe<?> coercedValue = fc.coerce(field, params, annotations);
            
            if (coercedValue.hasValue())
                dest.setFieldValue(field, coercedValue.getValue());
        }
        
        for (String param : params.keySet()) {
            if (!dest.hasField(param)) {
                Object value = params.get(param);
                dest.setExtraParameter(param, value);
            }
        }
    }
    
    /**
     * Creates a new Request object of the given type and calls {@link #buildOn(Map, Request)}.
     * 
     * <p>Note: if you get an inexplicable InstantiationException, make sure the required class
     * you give is not a non-static inner class.</p>
     * 
     * @see #buildOn(Map, Request)
     * @param params The parameter map.
     * @return The request object.
     * @throws IllegalAccessException If the builder class had no access to the Request class.
     * @throws InstantiationException If the request class could not be constructed using the default constructor.
     * @throws IllegalArgumentException If a field had a type for which there was no coercer or some other type mismatch occured.
     */
    public <T extends Request> T build(Map<String, ?> params, Class<T> cls) throws InstantiationException, IllegalAccessException {
        T req = cls.newInstance();
        buildOn(params, req);
        return req;
    }
}
