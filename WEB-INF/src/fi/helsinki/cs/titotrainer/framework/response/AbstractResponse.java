package fi.helsinki.cs.titotrainer.framework.response;


/**
 * <p>A convenience superclass to help implement {@link Response}.</p>
 * 
 * <p>This stubs the path transformation methods and stores a status code
 * given in the constructor.</p>
 */
public abstract class AbstractResponse implements Response {
    
    /**
     * The status code that will be returned.
     */
    protected int statusCode;
    
    public AbstractResponse(int statusCode) {
        this.statusCode = statusCode;
    }
    
    @Override
    public int getStatusCode() {
        return statusCode;
    }
}
