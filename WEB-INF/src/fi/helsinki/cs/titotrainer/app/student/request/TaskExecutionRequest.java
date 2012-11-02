package fi.helsinki.cs.titotrainer.app.student.request;

import fi.helsinki.cs.titotrainer.app.request.TitoRequest;
import fi.helsinki.cs.titotrainer.framework.request.Optional;

public class TaskExecutionRequest extends TitoRequest {
    public long taskId;
    
    @Optional
    public Integer[] userInput;
    @Optional
    public String code;
}
