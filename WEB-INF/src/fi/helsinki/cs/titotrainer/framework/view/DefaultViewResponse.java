package fi.helsinki.cs.titotrainer.framework.view;

import fi.helsinki.cs.titotrainer.framework.response.AbstractResponse;
import fi.helsinki.cs.titotrainer.framework.response.ResponseBodyWriter;
import fi.helsinki.cs.titotrainer.framework.response.ViewRenderingResponse;

/**
 * The response returned by a {@link View}.
 */
public class DefaultViewResponse extends AbstractResponse implements ViewRenderingResponse {
    
    public final static String DEFAULT_CONTENT_TYPE = "text/html; charset=utf-8";
    public final static int DEFAULT_STATUS_CODE = 200;
    
    private ResponseBodyWriter rw;
    private String contentType;
    private String contentDisposition;
    
    /**
     * <p>Default constructor for text/html responses.</p>
     * 
     * <p>Immediately adds the Content-Type header to the
     * <code>headers</code> field.</p>
     */
    public DefaultViewResponse(ResponseBodyWriter rw) {
        this(rw, DEFAULT_CONTENT_TYPE);
    }
    
    /**
     * Constructor for specifying a custom content type.
     */
    public DefaultViewResponse(ResponseBodyWriter rw, String contentType) {
        this(rw, contentType, DEFAULT_STATUS_CODE);
    }
    
    /**
     * Constructor for specifying a custom content type and status code.
     */
    public DefaultViewResponse(ResponseBodyWriter rw, String contentType, int statusCode) {
        super(statusCode);
        if (rw == null)
            throw new NullPointerException();
        if (contentType == null)
            throw new NullPointerException();
        this.rw = rw;
        this.contentType = contentType;
        this.contentDisposition = "inline";
    }
    
    /**
     * Returns the content type as set by {@link #setContentType(String)}.
     * @return The Content-Type header's value.
     */
    public String getContentType() {
        return contentType;
    }
    
    /**
     * Sets the content type.
     * @param contentType The Content-Type header's value.
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    @Override
    public String getContentDisposition() {
        return contentDisposition;
    }
    
    @Override
    public void setContentDisposition(String contentDisposition) {
        this.contentDisposition = contentDisposition;
    }
    
    /**
     * Sets the HTTP status code (which defaults to 200 OK).
     * 
     * @param statusCode The new status code.
     */
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public ResponseBodyWriter getResponseWriter() {
        return this.rw;
    }
    
    @Override
    public String toString() {
        return "ViewResponse";
    }
    
}
