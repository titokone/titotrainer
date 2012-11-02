package fi.helsinki.cs.titotrainer.framework.response;

/**
 * <p>A response that indicates a redirect.</p>
 */
public class RedirectResponse extends AbstractResponse {
    
    public static final int DEFAULT_STATUS_CODE = 302;
    
    protected String path;
    
    /**
     * Constructor. Uses the default status code.
     * 
     * @param path The path to redirect to.
     */
    public RedirectResponse(String path) {
        super(DEFAULT_STATUS_CODE);
        this.path = path;
    }
    
    /**
     * Constructor.
     * 
     * @param path The path to redirect to.
     * @param statusCode A 3xx HTTP status code.
     */
    public RedirectResponse(String path, int statusCode) {
        super(statusCode);
        if (statusCode / 100 != 3)
            throw new IllegalArgumentException("Cannot construct RedirectResponse with a status code of " + statusCode + " - must be 3xx");
        this.path = path;
    }
    
    public String getPath() {
        return path;
    }
    
    @Override
    public String toString() {
        return "RedirectResponse: " + this.getPath();
    }

}
