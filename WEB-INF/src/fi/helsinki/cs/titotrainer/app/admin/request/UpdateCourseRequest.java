package fi.helsinki.cs.titotrainer.app.admin.request;

import java.util.Map;

import fi.helsinki.cs.titotrainer.app.request.TitoRequest;

public class UpdateCourseRequest extends TitoRequest {
    
    public long id;
    public boolean hidden;
    public Map<String, String> courseName;
    
}
