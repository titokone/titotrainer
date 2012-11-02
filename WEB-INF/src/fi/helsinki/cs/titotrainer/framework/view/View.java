package fi.helsinki.cs.titotrainer.framework.view;

import fi.helsinki.cs.titotrainer.framework.RequestHandler;
import fi.helsinki.cs.titotrainer.framework.request.Request;
import fi.helsinki.cs.titotrainer.framework.response.ViewResponse;

/**
 * <p>Superclass for view objects i.e. objects that retrieve data
 * from the model as per a request and print it out in the request's
 * output stream.</p>
 * 
 * <p>Views must be thread-safe!</p>
 * 
 * @param <RequestType>
 */
public interface View<RequestType extends Request> extends RequestHandler<RequestType> {
    
    /**
     * Handles a request by printing out the result into the request's
     * output stream or by throwing an error.
     * 
     * @param req A validated request object expected by this view.
     * @return A view response.
     */
    @Override
    public ViewResponse handle(RequestType req) throws Exception;
}
