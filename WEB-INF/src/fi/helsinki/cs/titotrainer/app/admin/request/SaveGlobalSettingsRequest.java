package fi.helsinki.cs.titotrainer.app.admin.request;

import java.util.Map;

import fi.helsinki.cs.titotrainer.app.request.TitoRequest;

public class SaveGlobalSettingsRequest extends TitoRequest {
    
    // These guys map CriterionType.class.simpleName -> (locale -> message).
    public Map<String, Map<String, String>> acceptMsg;
    public Map<String, Map<String, String>> rejectMsg;
    
}
