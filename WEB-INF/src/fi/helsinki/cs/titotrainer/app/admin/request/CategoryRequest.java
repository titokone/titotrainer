package fi.helsinki.cs.titotrainer.app.admin.request;

import java.util.Collection;

import fi.helsinki.cs.titotrainer.app.request.TitoRequest;
import fi.helsinki.cs.titotrainer.framework.request.Optional;
import fi.helsinki.cs.titotrainer.framework.request.RequestInvalidity;

public class CategoryRequest extends TitoRequest {
    @Optional
    public Long id;
    
    @Optional
    public Long courseId;
    
    @Override
    public Collection<RequestInvalidity> validate() {
        Collection<RequestInvalidity> invs = super.validate();
        
        if (id == null && courseId == null)
            invs.add(new RequestInvalidity("either id or courseId must be specified"));
        
        return invs;
    }
}
