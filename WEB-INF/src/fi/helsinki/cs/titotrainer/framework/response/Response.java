package fi.helsinki.cs.titotrainer.framework.response;


/**
 * Encapsulates an HTTP response.
 */
public interface Response {
    
    /**
     * Returns the desired HTTP response code.
     * 
     * @return A valid HTTP response code.
     */
    public int getStatusCode();
}
