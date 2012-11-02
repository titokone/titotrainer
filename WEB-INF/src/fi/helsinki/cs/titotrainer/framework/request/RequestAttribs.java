package fi.helsinki.cs.titotrainer.framework.request;

import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.framework.RequestContext;
import fi.helsinki.cs.titotrainer.framework.session.UserSession;

/**
 * <p>Contains the attributes of a request that are not
 * request parameters. This includes things like the
 * request path, the request context, etc.</p>
 * 
 * <p>Every {@link Request} has one instance of
 * {@link RequestAttribs} or its subclass.
 * If copies are made of a {@link Request},
 * e.g. when forwarding from one controller to another,
 * the new request will share the same {@link RequestAttribs}.</p>
 */
public class RequestAttribs {
    protected RequestContext context = null;
    protected Session hibernateSession = null;
    protected UserSession userSession = null;
    protected String basePath = null;
    protected String localPath = null;
    
    /**
     * <p>The constructor.</p>
     * 
     * <p>None of the parameters may be null.</p>
     * 
     * @param context The request context.
     * @param hibernateSession A hibernate session.
     * @param userSession The current user's session.
     * @param basePath The base path of the request.
     * @param localPath The local path of the request.
     */
    public RequestAttribs(RequestContext context,
                          Session hibernateSession,
                          UserSession userSession,
                          String basePath,
                          String localPath) {
        if (context == null)
            throw new NullPointerException("context may not be null");
        if (hibernateSession == null)
            throw new NullPointerException("hibernateSession may not be null");
        if (userSession == null)
            throw new NullPointerException("userSession may not be null");
        if (basePath == null)
            throw new NullPointerException("basePath may not be null");
        if (localPath == null)
            throw new NullPointerException("localPath may not be null");
        
        this.context = context;
        this.hibernateSession = hibernateSession;
        this.userSession = userSession;
        this.basePath = basePath;
        this.localPath = localPath;
    }
    
    /**
     * Returns the request context.
     * 
     * @return The request context. Not null.
     */
    public RequestContext getContext() {
        return context;
    }
    
    /**
     * <p>Returns the hibernate session object associated with this
     * request (and any copies of the request).</p>
     * 
     * @return The hibernate session. Not null.
     */
    public Session getHibernateSession() {
        return hibernateSession;
    }
    
    /**
     * <p>Returns the session of the user that made this request
     * request (and any copies of the request).</p>
     * 
     * @return The user session. Not null.
     */
    public UserSession getUserSession() {
        return userSession;
    }
    
    /**
     * Returns the base path.
     * 
     * @return The base path. Not null.
     */
    public String getBasePath() {
        return basePath;
    }
    
    /**
     * Returns the local path.
     * 
     * @return The local path. Not null.
     */
    public String getLocalPath() {
        return this.localPath;
    }
    
    /**
     * Returns the base path with the local path appended.
     * @return <code>getBasePath() + getLocalPath()</code>
     */
    public String getFullPath() {
        return this.getBasePath() + this.getLocalPath();
    }
    
}
