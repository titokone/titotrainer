package fi.helsinki.cs.titotrainer.app.admin.view;

import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.app.request.TitoRequest;
import fi.helsinki.cs.titotrainer.app.view.TitoPageView;
import fi.helsinki.cs.titotrainer.framework.response.ErrorResponseException;
import fi.helsinki.cs.titotrainer.framework.response.ViewResponse;
import fi.helsinki.cs.titotrainer.framework.view.template.TemplateRenderer;

public class GlobalSettingsView extends TitoPageView<TitoRequest> {
    
    @Override
    protected String getTemplateName() {
        return "admin/globalsettings.vm";
    }
    
    @Override
    protected void handle(TitoRequest req, Session hs, TemplateRenderer tr, ViewResponse resp) throws ErrorResponseException {
        
        tr.put("supportedLocales", req.getContext().getTitoTranslation().getSupportedLocales());
        
        tr.put("criterionTypes", hs.createQuery("FROM CriterionType ORDER BY className").list());
        
    }
    
    @Override
    public Class<TitoRequest> getRequestType() {
        return TitoRequest.class;
    }
    
}
