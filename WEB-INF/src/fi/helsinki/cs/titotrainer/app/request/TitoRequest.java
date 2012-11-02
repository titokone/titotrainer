package fi.helsinki.cs.titotrainer.app.request;

import fi.helsinki.cs.titotrainer.app.TitoRequestContext;
import fi.helsinki.cs.titotrainer.app.session.TitoUserSession;
import fi.helsinki.cs.titotrainer.framework.RequestContext;
import fi.helsinki.cs.titotrainer.framework.request.Request;
import fi.helsinki.cs.titotrainer.framework.request.RequestAttribs;

/**
 * <p>The base class for all requests in TitoTrainer.</p>
 * 
 * <p>This class is also non-abstract so it can be used directly
 * by handlers that don't expect request parameters.</p>
 * 
 * <p>The {@link RequestAttribs} of a {@link TitoRequest}
 * is guaranteed to be an instance of {@link TitoRequestAttribs}.</p>
 * 
 * <p>The {@link RequestContext} of a {@link TitoRequest}
 * is guaranteed to be an instance of {@link TitoRequestContext}.</p>
 */
public class TitoRequest extends Request {
    
    @Override
    public TitoRequestAttribs getAttribs() {
        return (TitoRequestAttribs)super.getAttribs();
    }
    
    /**
     * Sets a {@link TitoRequestAttribs} as the request attributes object.
     *
     * @param attribs The attributes. Must be a {@link TitoRequestAttribs} instance.
     * @throws IllegalArgumentException if <code>attribs</code> is not a {@link TitoRequestAttribs} instance.
     * @see Request#setAttribs(RequestAttribs)
     */
    @Override
    public void setAttribs(RequestAttribs attribs) {
        if (!(attribs instanceof TitoRequestAttribs))
            throw new IllegalArgumentException("TitoRequest.setAttribs() expects a TitoRequestAttribs object");
        
        super.setAttribs(attribs);
    }
    
    @Override
    public TitoRequestContext getContext() {
        return (TitoRequestContext)super.getContext();
    }
    
    @Override
    public TitoUserSession getUserSession() {
        return this.getAttribs().getUserSession();
    }
    
    /**
     * An alias of {@link TitoRequestAttribs#getQueryString()}.
     */
    public String getQueryString() {
        return this.getAttribs().getQueryString();
    }
    
}
