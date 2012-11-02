package fi.helsinki.cs.titotrainer.framework.response;

import fi.helsinki.cs.titotrainer.framework.controller.Controller;
import fi.helsinki.cs.titotrainer.framework.view.View;


/**
 * <p>A response that encodes an error that is representable by
 * an HTTP error code.</p>
 * 
 * <p>An {@link ErrorResponse} may be returned by both {@link Controller}s and
 * {@link View}s.
 */
public class ErrorResponse extends AbstractResponse implements Response, ViewResponse {

    public static int DEFAULT_STATUS_CODE = 500;
    
    protected final String message;
    protected final Throwable cause;
    
    public ErrorResponse() {
        this(DEFAULT_STATUS_CODE);
    }
    
    public ErrorResponse(int statusCode) {
        this(statusCode, null, null);
    }
    
    public ErrorResponse(String message) {
        this(DEFAULT_STATUS_CODE, message, null);
    }
    
    public ErrorResponse(String message, Throwable cause) {
        this(DEFAULT_STATUS_CODE, message, cause);
    }
    
    public ErrorResponse(int statusCode, String message) {
        this(statusCode, message, null);
    }
    
    public ErrorResponse(int statusCode, Throwable cause) {
        this(statusCode, null, cause);
    }
    
    public ErrorResponse(int statusCode, String message, Throwable cause) {
        super(statusCode);
        this.message = message;
        this.cause = cause;
    }
    
    /**
     * Returns the message of the error response.
     * 
     * @return The message (may be null).
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * Returns the cause of the error response.
     * 
     * @return The cause (may be null).
     */
    public Throwable getCause() {
        return cause;
    }
    
    @Override
    public String toString() {
        return "ErrorResponse (" + this.getStatusCode() + "): " + this.getMessage();
    }
    
}
