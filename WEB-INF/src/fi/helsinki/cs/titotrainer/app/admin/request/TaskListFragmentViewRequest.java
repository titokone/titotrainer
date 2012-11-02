package fi.helsinki.cs.titotrainer.app.admin.request;

import fi.helsinki.cs.titotrainer.app.request.TitoRequest;
import fi.helsinki.cs.titotrainer.framework.request.Optional;

public class TaskListFragmentViewRequest extends TitoRequest {
    @Optional
    public Long courseId;
    @Optional
    public Long categoryId;
}
