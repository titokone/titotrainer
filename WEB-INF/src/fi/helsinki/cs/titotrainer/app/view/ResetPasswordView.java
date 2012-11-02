package fi.helsinki.cs.titotrainer.app.view;

import fi.helsinki.cs.titotrainer.app.request.TitoRequest;

public class ResetPasswordView extends TitoPageView<TitoRequest> {
    
    @Override
    protected String getTemplateName() {
        return "reset-password.vm";
    }
    
    @Override
    public Class<TitoRequest> getRequestType() {
        return TitoRequest.class;
    }
    
}
