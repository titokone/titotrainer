package fi.helsinki.cs.titotrainer.app.controller;

import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.app.request.TitoRequest;
import fi.helsinki.cs.titotrainer.app.session.TitoUserSession;
import fi.helsinki.cs.titotrainer.framework.response.RedirectResponse;
import fi.helsinki.cs.titotrainer.framework.response.Response;

/**
 * Handles the logout requests of administrative users.
 */
public class LogoutController extends TitoActionController<TitoRequest> {
    
    @Override
    public Class<TitoRequest> getRequestType() {
        return TitoRequest.class;
    }
    
    @Override
    protected Response handleValid(TitoRequest req, Session hs) throws Exception {
        TitoUserSession userSession = req.getUserSession();
        userSession.setAuthenticatedUser(null);
        userSession.clearAttributes();
        return new RedirectResponse("/login");
    }
    
}
