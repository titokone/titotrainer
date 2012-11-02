package fi.helsinki.cs.titotrainer.framework;

import fi.helsinki.cs.titotrainer.framework.controller.Controller;
import fi.helsinki.cs.titotrainer.framework.request.Request;
import fi.helsinki.cs.titotrainer.framework.response.Response;
import fi.helsinki.cs.titotrainer.framework.view.View;

/**
 * Superclass for all request handlers, which are almost always
 * categorized further as either controllers or views.
 * 
 * @param <RequestType> The type of Request subclass expected.
 * @see Controller
 * @see View
 */
public interface RequestHandler<RequestType extends Request> {
    
    /**
     * <p>Handles a request and produces a response.</p>
     * 
     * @param req The request object. Never null. May not be valid (as per {@link Request#validate()}).
     * @return A response. Should not be null.
     * @throws Exception If something goes wrong.
     */
    public Response handle(RequestType req) throws Exception;
    
    /**
     * Returns the class of requests this handler expects.
     * @return The {@link Class} of a subclass of Request.
     */
    public Class<RequestType> getRequestType();
}
