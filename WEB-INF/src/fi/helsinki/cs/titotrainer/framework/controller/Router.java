package fi.helsinki.cs.titotrainer.framework.controller;

import java.util.concurrent.atomic.AtomicReference;

import fi.helsinki.cs.titotrainer.framework.RequestHandler;
import fi.helsinki.cs.titotrainer.framework.access.AccessController;
import fi.helsinki.cs.titotrainer.framework.request.DefaultRequest;
import fi.helsinki.cs.titotrainer.framework.request.Request;
import fi.helsinki.cs.titotrainer.framework.response.ErrorResponse;
import fi.helsinki.cs.titotrainer.framework.response.Response;

/**
 * <p>Convenience superclass for routers.</p>
 * 
 * <p>A router is a controller whose main task is to
 * call another controller.</p>
 */
public abstract class Router extends AbstractController<DefaultRequest> {
    
    private AtomicReference<AccessController> accessController;
    
    public Router() {
        this.accessController = new AtomicReference<AccessController>();
    }
    
    public AccessController getAccessController() {
        return accessController.get();
    }
    
    public void setAccessController(AccessController accessController) {
        this.accessController.set(accessController);
    }
    
    /**
     * <p>Calls the matched request handler.</p>
     * 
     * <p>If an access controller is set, it is called with the
     * handler as the resource. If the access check fails,
     * {@link #denyAccess(Request)} is called.</p>
     * 
     * @param <DesiredRequestType> The request type expected by the handler.
     * @param handler The handler to call.
     * @param req The request to copy for the handler.
     * @return The response from the handler (or a response provided by an overriding subclass).
     * @throws Exception An exception.
     */
    protected <DesiredRequestType extends Request> Response callHandler(RequestHandler<DesiredRequestType> handler, Request req) throws Exception {
        
        AccessController ac = this.getAccessController();
        if (ac != null) {
            if (req.getAttribs().getUserSession() == null)
                return this.denyAccess(req);
            if (req.getAttribs().getUserSession().getRole() == null)
                return this.denyAccess(req);
            
            if (!ac.hasAccess(req.getAttribs().getUserSession().getRole(), handler))
                return this.denyAccess(req);
        }
        
        DesiredRequestType newReq = req.copy(handler.getRequestType());
        
        Response resp = handler.handle(newReq);
        
        return resp;
    }
    
    /**
     * <p>Called when access to a resource is denied.</p>
     * 
     * <p>The default implementation returns an
     * {@link ErrorResponse} with a code of <code>403</code>.</p>
     * 
     * @param req The request that was made.
     * @return The response to return.
     */
    protected Response denyAccess(Request req) {
        return new ErrorResponse(403, req.getLocalPath());
    }
    
}
