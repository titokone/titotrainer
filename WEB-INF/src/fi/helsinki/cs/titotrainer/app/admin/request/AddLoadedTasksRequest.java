package fi.helsinki.cs.titotrainer.app.admin.request;

import java.util.Map;

import fi.helsinki.cs.titotrainer.app.request.TitoRequest;
import fi.helsinki.cs.titotrainer.framework.request.Optional;

public class AddLoadedTasksRequest extends TitoRequest {
    public long courseId;
    
    @Optional
    public Map<String, String> selected;
    @Optional
    public Map<String, String> category;
}
