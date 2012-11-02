package fi.helsinki.cs.titotrainer.app.admin.controller;

import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.app.admin.request.SaveGlobalSettingsRequest;
import fi.helsinki.cs.titotrainer.app.controller.TitoActionController;
import fi.helsinki.cs.titotrainer.app.i18n.TitoTranslation;
import fi.helsinki.cs.titotrainer.app.model.CriterionType;
import fi.helsinki.cs.titotrainer.framework.response.RedirectResponse;
import fi.helsinki.cs.titotrainer.framework.response.Response;

public class SaveGlobalSettingsController extends TitoActionController<SaveGlobalSettingsRequest> {
    
    @Override
    public Class<SaveGlobalSettingsRequest> getRequestType() {
        return SaveGlobalSettingsRequest.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Response handleValid(SaveGlobalSettingsRequest req, Session hs) throws Exception {
        
        TitoTranslation tt = req.getContext().getTitoTranslation();
        
        List<CriterionType> criterionTypes = hs.createQuery("FROM CriterionType").list();
        
        for (CriterionType ct : criterionTypes) {
            Map<String, String> acceptMsg = req.acceptMsg.get(ct.getCriterionClass().getSimpleName());
            Map<String, String> rejectMsg = req.rejectMsg.get(ct.getCriterionClass().getSimpleName());
            
            if (acceptMsg != null)
                ct.setDefaultAcceptMessage(mapToTString(acceptMsg, tt, false));
            if (rejectMsg != null)
                ct.setDefaultRejectMessage(mapToTString(rejectMsg, tt, false));
            
            hs.update(ct);
        }
        
        return new RedirectResponse("/admin/globalsettings");
    }
    
    
    
}
