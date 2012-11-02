package fi.helsinki.cs.titotrainer.framework.misc;

/**
 * A wrapper object to use when the Java language requires a variable
 * to be final but you wish to be able to set it anyway.
 * 
 * @param <T>
 */
public class MutableRef<T> {
    /**
     * The value being wrapped.
     */
    public T value;
    
    /**
     * <p>The default constructor.</p>
     * 
     * <p>Sets the value to null.</p>
     */
    public MutableRef() {
        this.value = null;
    }
    
    /**
     * <p>The initializing constructor.</p>
     * @param value The initial value.
     */
    public MutableRef(T value) {
        this.value = value;
    }
}
