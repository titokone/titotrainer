package fi.helsinki.cs.titotrainer.app.admin.request;

import fi.helsinki.cs.titotrainer.app.request.TitoRequest;
import fi.helsinki.cs.titotrainer.framework.request.Optional;

public class DeleteTaskRequest extends TitoRequest {
    public long id;
    
    /**
     * Either {@code "course"} or {@code "category"}.
     * Defaults to course. Will also use course if the task had no category.
     */
    @Optional
    public String returnTo;
}
