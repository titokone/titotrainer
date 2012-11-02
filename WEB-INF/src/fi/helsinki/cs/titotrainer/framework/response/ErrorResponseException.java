package fi.helsinki.cs.titotrainer.framework.response;

/**
 * <p>An {@link ErrorResponse} wrapper that is a checked exception.</p>
 */
public class ErrorResponseException extends Exception {
    
    private ErrorResponse errorResponse;
    
    /**
     * Constructor.
     * 
     * @param errorResponse The (non-null) error response to wrap.
     */
    public ErrorResponseException(ErrorResponse errorResponse) {
        super("ErrorResponse (" + errorResponse.getStatusCode() + "): " + errorResponse.getMessage(), errorResponse.getCause());
        this.errorResponse = errorResponse;
    }

    /**
     * Convenience constructor.
     */
    public ErrorResponseException(int code) {
        this(new ErrorResponse(code));
    }
    
    /**
     * Convenience constructor.
     */
    public ErrorResponseException(int code, String msg) {
        this(new ErrorResponse(code, msg));
    }

    /**
     * Convenience constructor.
     */
    public ErrorResponseException(int code, Throwable cause) {
        this(new ErrorResponse(code, cause));
    }
    
    /**
     * Convenience constructor.
     */
    public ErrorResponseException(int code, String msg, Throwable cause) {
        this(new ErrorResponse(code, msg, cause));
    }

    
    public ErrorResponse getErrorResponse() {
        return errorResponse;
    }
}
