package fi.helsinki.cs.titotrainer.framework.request;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import fi.helsinki.cs.titotrainer.framework.RequestContext;
import fi.helsinki.cs.titotrainer.framework.session.UserSession;


public abstract class Request {

    @Manual
    public static final String INVALIDITY_MSG_MANDATORY_FIELD_EMPTY = "Mandatory parameter not given.";
    @Manual
    public static final String INVALIDITY_MSG_BAD_FORMAT = "Parameter not in a valid format.";
    @Manual
    public static final String INVALIDITY_MSG_NOT_POSITIVE = "Parameter is not positive.";
    
    protected RequestAttribs attribs;
    
    protected Map<String, Object> extraParameters = new HashMap<String, Object>();
    
    /**
     * <p>Returns the request attributes object.</p>
     * 
     * @return The request attributes object. Should never be null if the application entry point does its job.
     */
    public RequestAttribs getAttribs() {
        return this.attribs;
    }
    
    /**
     * <p>Sets the request attributes object.</p>
     * 
     * @param attribs The new request attributes object.
     */
    public void setAttribs(RequestAttribs attribs) {
        this.attribs = attribs;
    }
    
    /**
     * Returns the request context.
     * 
     * This is a shortcut to <code>getAttribs().getContext()</code>.
     * 
     * @return The request context. Not null.
     */
    public RequestContext getContext() {
        return this.getAttribs().getContext();
    }
    
    /**
     * <p>Returns the base path.</p>
     * 
     * This is a shortcut to <code>getAttribs().getBasePath()</code>.
     * 
     * @return The base path. Not null.
     */
    public final String getBasePath() {
        return this.getAttribs().getBasePath();
    }
    
    /**
     * <p>Returns the local path.</p>
     * 
     * This is a shortcut to <code>getAttribs().getLocalPath()</code>.
     * 
     * @return The local path. Not null.
     */
    public final String getLocalPath() {
        return this.getAttribs().getLocalPath();
    }
    
    /**
     * <p>Returns the base path with the local path appended.</p>
     * 
     * This is a shortcut to <code>getAttribs().getFullPath()</code>.
     * 
     * @return <code>getBasePath() + getLocalPath()</code>
     */
    public final String getFullPath() {
        return this.getAttribs().getFullPath();
    }
    
    /**
     * <p>Returns the user session.</p>
     * 
     * This is a shortcut to <code>getAttribs().getUserSession()</code>.
     * 
     * @return The user session. Not null.
     */
    public UserSession getUserSession() {
        return this.getAttribs().getUserSession();
    }
    
    /**
     * <p>Returns the {@link RequestBuilder} isntance used by {@link #copyTo(Request)}.</p>
     * 
     * <p>By default this returns the default instance from {@link RequestBuilder#getDefaultInstance()}.</p>
     * 
     * @return A request builder instance.
     */
    protected RequestBuilder getRequestBuilder() {
        return RequestBuilder.getDefaultInstance();
    }
    
    /**
     * Makes a copy of this request such that:
     * <ul>
     *   <li>All fields are copied. If a field exists in the source object
     *   but not in the destination object, it is added as an extra parameter
     *   by applying {@link #toString()} to the value of the field.</li>
     *   <li>All extra parameters are copied. If there is an extra parameter
     *   for which there exists a field with the same name in the destination
     *   object, the destination object's field is set to that value by
     *   using the conversion semantics in {@link RequestBuilder}.</li>
     *   <li>All fields and extra parameters that were present in
     *   the destination object but not the source object are
     *   left intact.</li>
     *   <li>The request attributes are copied.</li>
     * </ul>
     * 
     * @param dest The request to overwrite.
     * @throws Exception if something goes wrong.
     * @see #copy(Class)
     */
    public void copyTo(Request dest) throws Exception {
        this.getRequestBuilder().buildOn(this.getAllParameters(), dest);
        dest.setAttribs(this.getAttribs());
    }
    
    /**
     * Makes a copy of this request.
     * See {@link #copyTo(Request)} for details.
     * 
     * <p>Note: if you get an inexplicable InstantiationException,
     * make sure the class you give is not a non-static inner class.</p>
     * 
     * @param <T> The desired type of the request.
     * @param cls The class of T.
     * @return An instance of T according to the semantics in {@link #copyTo(Request)}.
     * @throws Exception if something goes wrong.
     * @see #copyTo(Request)
     */
    public <T extends Request> T copy(Class<T> cls) throws Exception {
        T newReq = cls.newInstance();
        this.copyTo(newReq);
        return newReq;
    }
    
    /**
     * <p>Validates the general format of the request.</p>
     * 
     * <p>The superclass method validates all annotations that define
     * validation semantics. Subclasses are free to override or add to
     * this behaviour.</p>
     * 
     * @return A list of reasons why this request is not valid.
     *         This will be empty (not null) if the request is valid.
     */
    public Collection<RequestInvalidity> validate() {
        Collection<RequestInvalidity> ret = new LinkedList<RequestInvalidity>();
        
        ret.addAll(validateMandatoryFields());
        ret.addAll(validateRegexAnnotatedFields());
        ret.addAll(validatePositiveAnnotatedFields());
        
        return ret;
    }
    
    /**
     * <p>Verifies that mandatory fields are not null nor empty.</p>
     * <p>Called by the default validate() implementation.</p>
     * 
     * @return A list of invalidities.
     */
    protected Collection<RequestInvalidity> validateMandatoryFields() {
        Collection<RequestInvalidity> ret = new LinkedList<RequestInvalidity>();
        
        for (String field : this.getFields()) {
            if (!isFieldOptional(field) && isValueEmpty(reflectField(field))) {
                ret.add(new RequestInvalidity(INVALIDITY_MSG_MANDATORY_FIELD_EMPTY, field));
            }
        }
        
        return ret;
    }
    
    /**
     * <p>Verifies that string fields match their regex annotations.</p>
     * <p>Called by the default validate() implementation.</p>
     * 
     * @return A list of invalidities.
     */
    protected Collection<RequestInvalidity> validateRegexAnnotatedFields() {
        Collection<RequestInvalidity> ret = new LinkedList<RequestInvalidity>();
        
        for (String field : this.getFields()) {
            Field fieldRefl = reflectField(field);
            if (isFieldOptional(field) && isValueEmpty(fieldRefl))
                continue;
            
            Regex matchAnnotation = fieldRefl.getAnnotation(Regex.class);
            if (matchAnnotation != null) {
                assert(matchAnnotation.value() != null); // Should be impossible as far as I know
                
                Object fieldValue = getFieldValue(field);
                if (!(fieldValue instanceof String)) {
                    throw new IllegalStateException("Field " + field + " has Regex annotation but is not a string");
                }
                
                if (!((String)fieldValue).matches(matchAnnotation.value()))
                    ret.add(new RequestInvalidity(INVALIDITY_MSG_BAD_FORMAT, field));
            }
        }
        
        return ret;
    }
    
    /**
     * <p>Verifies that numbers with the positive annotation are indeed positive.</p>
     * <p>Called by the default validate() implementation.</p>
     * 
     * @return A list of invalidities.
     */
    protected Collection<RequestInvalidity> validatePositiveAnnotatedFields() {
        Collection<RequestInvalidity> ret = new LinkedList<RequestInvalidity>();
        
        for (String field : this.getFields()) {
            if (reflectField(field).isAnnotationPresent(Positive.class)) {
                Object value = getFieldValue(field);
                if (value != null) {
                    if (value instanceof Number) {
                        if (((Number)value).longValue() <= 0) {
                            ret.add(new RequestInvalidity(INVALIDITY_MSG_NOT_POSITIVE, field));
                        }
                    }
                }
            }
        }
        
        return ret;
    }
    
    /**
     * <p>Determines whether a field's value is to be considered empty.</p>
     * 
     * <p>Called by <code>validateMandatoryFields()</code></p>
     * 
     * <p>
     * The superclass considers a value empty iff one of the following holds:
     * <ol>
     *   <li>The value is null.</li>
     *   <li>The type has a method with the signature "boolean isEmpty()",
     *       which returns true for this value.</li>
     * </ol>
     * Subclasses are free to change this.
     * </p>
     * 
     * 
     * @param field The name of the field.
     * @return Whether the value is empty.
     */
    protected boolean isValueEmpty(Field field)  {
        try {
            if (field.get(this) == null)
                return true;
            Method isEmpty = field.getType().getMethod("isEmpty");
            if (isEmpty.getReturnType() == Boolean.TYPE)
                if ((Boolean)isEmpty.invoke(field.get(this)))
                    return true;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (NoSuchMethodException e) {
            // No isEmpty method - ignored
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        return false;
    }
    
    /**
     * Returns the names of all public data fields in the request.
     * 
     * @return An unsorted collection of the names of all data fields.
     */
    public Collection<String> getFields() {
        Collection<String> result = new LinkedList<String>();
        Field[] fields = this.getClass().getFields();
        for (Field field : fields) {
            if (Modifier.isPublic(field.getModifiers()) && !field.isAnnotationPresent(Manual.class))
                result.add(field.getName());
        }
        return result;
    }
    
    /**
     * Returns the value of a field.
     * 
     * @param field The name of the field.
     * @return The value of the field (may be null).
     * @throws IllegalArgumentException If the specified field does not exist, is not public, is annotated Manual or is not accessible.
     */
    public Object getFieldValue(String field) throws IllegalArgumentException {
        try {
            Field fieldObj = this.reflectField(field);
            return fieldObj.get(this);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    /**
     * Sets the value of a field.
     * 
     * @param field The name of the field.
     * @param value The new value of the field (may be null).
     * @throws IllegalArgumentException If the specified field does not exist, is not public, is annotated Manual, is not accessible or if the specified value is of an incorrect type.
     */
    public void setFieldValue(String field, Object value) throws IllegalArgumentException {
    	try {
    		Field fieldObj = this.reflectField(field);
			fieldObj.set(this, value);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		}
    }
    
    /**
     * Returns the type of a field.
     * 
     * @param field The name of the field.
     * @return The reflection object of the type (class) of the field.
     * @throws IllegalArgumentException If the specified field does not exist, is not public or is annotated Manual.
     */
    public Class<?> getFieldType(String field) throws IllegalArgumentException {
        return this.reflectField(field).getType();
    }
    
    /**
     * Returns whether there is a field with the given name.
     * 
     * @param field The name of the field.
     * @return Whether the named field exists, is public and is not marked as <code>@Manual</code>.
     */
    public boolean hasField(String field) {
        try {
            this.reflectField(field);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }
    
    /**
     * Checks whether the specified field is optional.
     * 
     * @param field The name of the field.
     * @return True if the field is marked optional.
     * @throws IllegalArgumentException If the specified field does not exist, is not public or is annotated Manual.
     */
    public boolean isFieldOptional(String field) throws IllegalArgumentException {
        return this.reflectField(field).isAnnotationPresent(Optional.class);
    }

    /**
     * Convenience method for getting a public field, non-manual field
     * and throwing IllegalArgumentException if something goes wrong.
     * 
     * @param field The name of the field.
     * @return The field reflection object.
     * @throws IllegalArgumentException If the specified field does not exist, is not public or is annotated Manual.
     */
    protected Field reflectField(String field) throws IllegalArgumentException {
        try {
            Field fieldObj = this.getClass().getField(field);
            fieldObj.setAccessible(true);
            assert(Modifier.isPublic(fieldObj.getModifiers())); // getField should only return public ones.
            if (fieldObj.isAnnotationPresent(Manual.class))
                throw new IllegalArgumentException("Field " + field + " is marked manual");
            return fieldObj;
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    /**
     * Records a parameter for which there was no field.
     * 
     * @param name The name of the extra parameter. Must not be a field.
     * @param value The value of the extra parameter. May be null.
     * @throws NullPointerException if name is null.
     * @throws IllegalArgumentException if name is a field.
     * @see #getExtraParameter(String)
     */
    public void setExtraParameter(String name, Object value) {
        if (name == null)
            throw new NullPointerException();
        if (this.hasField(name))
            throw new IllegalArgumentException(name + " is a field, not an extra parameter");
        this.extraParameters.put(name, value);
    }
    
    /**
     * Returns a parameter for which there is no field.
     * 
     * @param name The name of the extra parameter. Must not be a field.
     * @return value The value of the extra parameter. null if not found.
     * @throws NullPointerException if name is null.
     * @throws IllegalArgumentException if name is a field.
     * @see #setExtraParameter(String, Object)
     */
    public Object getExtraParameter(String name) {
        if (name == null)
            throw new NullPointerException();
        if (this.hasField(name))
            throw new IllegalArgumentException(name + " is a field, not an extra parameter");
        return this.extraParameters.get(name);
    }
    
    /**
     * Returns an unmodifiable map of all extra parameters.
     * @return An unmodifiable map of all extra parameters.
     */
    public Map<String, Object> getExtraParameters() {
        return Collections.unmodifiableMap(this.extraParameters);
    }
    
    /**
     * Returns a map that contains copies of all fields and their
     * values as well as all extra parameters.
     * 
     * @return A modifiable map of all parameters.
     */
    public Map<String, Object> getAllParameters() {
        Map<String, Object> ret = new HashMap<String, Object>();
        for (String field : this.getFields()) {
            ret.put(field, this.getFieldValue(field));
        }
        ret.putAll(this.extraParameters);
        return ret;
    }
    
}
