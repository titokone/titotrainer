package fi.helsinki.cs.titotrainer.app.admin.request;

import java.util.Map;

import fi.helsinki.cs.titotrainer.app.request.TitoRequest;

public class CreateCourseRequest extends TitoRequest {
    
    public Map<String, String> courseName;
    public boolean hidden;
    
}
