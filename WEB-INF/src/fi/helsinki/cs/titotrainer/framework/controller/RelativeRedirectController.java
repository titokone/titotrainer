package fi.helsinki.cs.titotrainer.framework.controller;

import fi.helsinki.cs.titotrainer.framework.request.DefaultRequest;
import fi.helsinki.cs.titotrainer.framework.response.RedirectResponse;
import fi.helsinki.cs.titotrainer.framework.response.Response;

/**
 * A controller that always returns a relative path redirect response.
 */
public class RelativeRedirectController implements Controller<DefaultRequest> {
    
    protected RedirectResponse response;
    
    /**
     * Constructor. Uses the default status code of RedirectResponse.
     * @param path A redirect path - either absolute or relative.
     */
    public RelativeRedirectController(String path) {
        this.response = new RedirectResponse(path);
    }
    
    /**
     * Constructor.
     * @param path A redirect path - either absolute or relative.
     * @param statusCode The 3xx status code to use.
     */
    public RelativeRedirectController(String path, int statusCode) {
        this.response = new RedirectResponse(path, statusCode);
    }
    
    public Class<DefaultRequest> getRequestType() {
        return DefaultRequest.class;
    }
    
    @Override
    public Response handle(DefaultRequest req) throws Exception {
        return this.response;
    }
}
