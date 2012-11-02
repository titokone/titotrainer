package fi.helsinki.cs.titotrainer.framework.controller;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.framework.RequestHandler;
import fi.helsinki.cs.titotrainer.framework.request.DefaultRequest;
import fi.helsinki.cs.titotrainer.framework.response.ErrorResponse;
import fi.helsinki.cs.titotrainer.framework.response.Response;

/**
 * <p>A controller that contains a map of path prefixes to
 * other request handlers.</p>
 * 
 * <p>The request handler is selected by a longest prefix match.</p>
 * 
 * TODO: code example
 */
public class PrefixRouter extends Router {
    
    private Map<String, RequestHandler<?>> prefixMap;
    
    public PrefixRouter() {
        this.prefixMap = new HashMap<String, RequestHandler<?>>();
    }
    
    public void addRule(String prefix, RequestHandler<?> handler) {
        this.prefixMap.put(prefix, handler);
    }
    
    /**
     * Returns the request handler of the rule whose key was the longest
     * prefix of the given path.
     * 
     * @param path The path to match.
     * @return The longest prefix or null if none matched.
     */
    protected String findLongestPrefix(String path) {
        int longestMatchLength = -1;
        String longestMatchingPrefix = null;
        for (String prefix : prefixMap.keySet()) {
            if (prefix.length() > longestMatchLength && path.startsWith(prefix)) {
                longestMatchLength = prefix.length();
                longestMatchingPrefix = prefix;
            }
        }
        
        return longestMatchingPrefix;
    }
    
    /**
     * Returns the request handler associated with a path.
     * 
     * @param localPath A local path (or beginning of a local path) that may have a registered prefix.
     * @return The request handler that matches the prefix, or null if none found.
     */
    public RequestHandler<?> getHandlerForPrefix(String localPath) {
        if (localPath == null) {
            throw new NullPointerException("PrefixRouter did not expect request to have null path.");
        }
        
        String prefix = findLongestPrefix(localPath);
        if (prefix != null) {
            return this.prefixMap.get(prefix);
        } else {
            return null;
        }
    }
    
    @Override
    public Class<DefaultRequest> getRequestType() {
        return DefaultRequest.class;
    }
    
    /**
     * Prefix routers use no transaction by default.
     */
    @Override
    protected boolean useTransaction() {
        return false;
    }
    
    /**
     * <p>Forwards a request to a subcontroller according to its path.</p>
     * 
     * <p>If the request path doesn't match any rule then returns what
     * {@link #fallback(DefaultRequest)} returns.</p>
     */
    @Override
    public Response handleValid(DefaultRequest req, Session hs) throws Exception {
        RequestHandler<?> handler = getHandlerForPrefix(req.getLocalPath());
        if (handler != null) {
            return callHandler(handler, req);
        } else {
            return fallback(req);
        }
    }
    
    /**
     * By default, returns an {@link ErrorResponse} with an error code
     * of 404 and a message of "Not found".
     * 
     * @param req The request.
     * @return The default response.
     * @throws Exception
     */
    protected Response fallback(DefaultRequest req) throws Exception {
        return new ErrorResponse(404, "Not found");
    }
    
}
