package fi.helsinki.cs.titotrainer.framework.request;

/**
 * Contains information about why a Request is invalid.
 */
public class RequestInvalidity {
    protected String field; // May be null
    protected String msg; // Never null
    
    /**
     * Constructor for no specific field.
     * 
     * @param msg The key for the error message.
     */
    public RequestInvalidity(String msg) {
        this(msg, null);
    }
    
    /**
     * Constructor for a specific field.
     * 
     * @param msg The key for the error message.
     * @param field The field that caused the problem.
     */
    public RequestInvalidity(String msg, String field) {
        this.field = field;
        if (msg != null)
            this.msg = msg;
        else
            this.msg = "";
    }
    
    /**
     * Returns the name of the field.
     * 
     * @return The name of the field. Null if none specified.
     */
    public String getField() {
        return this.field;
    }
    
    /**
     * Returns the key for the error message.
     * 
     * @return The key for the error message.
     *         Never null, but may be empty.
     */
    public String getMsgKey() {
        return this.msg;
    }
    
    @Override
    public String toString() {
        return this.getField() + ": " + this.getMsgKey();
    }
}
