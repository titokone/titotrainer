package fi.helsinki.cs.titotrainer.app.view;

import fi.helsinki.cs.titotrainer.app.request.TitoRequest;

/**
 * Displays the administrators' login page.
 */
public class LoginView extends TitoPageView<TitoRequest> {
    
    @Override
    public Class<TitoRequest> getRequestType() {
        return TitoRequest.class;
    }
    
    @Override
    protected String getTemplateName() {
        return "login.vm";
    }
    
}
