package fi.helsinki.cs.titotrainer.framework.response;


/**
 * A response that can renders a view.
 */
public interface ViewRenderingResponse extends ViewResponse {
    
    /**
     * <p>Returns a value for the <code>Content-Type</code> header.</p>
     * 
     * @return A MIME type.
     */
    public String getContentType();
    
    /**
     * <p>Sets a value for the <code>Content-Type</code> header.</p>
     * 
     * @param contentType A MIME type.
     */
    public void setContentType(String contentType);
    
    /**
     * <p>Returns a value for the <code>Content-Disposition</code> header.</p>
     * 
     * @return A content disposition. Defaults to "inline". Not null.
     */
    public String getContentDisposition();
    
    /**
     * <p>Sets a value for the <code>Content-Type</code> header.</p>
     */
    public void setContentDisposition(String contentDisposition);
    
    /**
     * <p>Returns the response writer that will produce the output.</p>
     * 
     * @return A response writer.
     */
    public ResponseBodyWriter getResponseWriter();
    
}
