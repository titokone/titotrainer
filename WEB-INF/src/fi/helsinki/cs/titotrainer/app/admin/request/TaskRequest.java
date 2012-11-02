package fi.helsinki.cs.titotrainer.app.admin.request;

import java.util.Collection;

import fi.helsinki.cs.titotrainer.app.request.TitoRequest;
import fi.helsinki.cs.titotrainer.framework.request.Optional;
import fi.helsinki.cs.titotrainer.framework.request.RequestInvalidity;

public class TaskRequest extends TitoRequest {
    @Optional
    public Long id;
    @Optional
    public Long courseId;
    @Optional
    public Long categoryId;
    
    @Optional
    public Long printMode;
    
    @Override
    public Collection<RequestInvalidity> validate() {
        Collection<RequestInvalidity> invalidities = super.validate();
        if (id == null && courseId == null && categoryId == null) {
            invalidities.add(new RequestInvalidity("all of id, courseId and categoryId are missing"));
        }
        return invalidities;
    }
}
