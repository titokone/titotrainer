package fi.helsinki.cs.titotrainer.app.admin.request;

import java.util.Collection;
import java.util.Map;

import fi.helsinki.cs.titotrainer.app.request.TitoRequest;
import fi.helsinki.cs.titotrainer.framework.request.Optional;
import fi.helsinki.cs.titotrainer.framework.request.Regex;
import fi.helsinki.cs.titotrainer.framework.request.RequestInvalidity;
import fi.helsinki.cs.titotrainer.framework.request.coercer.BooleanCoercer;
import fi.helsinki.cs.titotrainer.framework.request.coercer.LongCoercer;
import fi.helsinki.cs.titotrainer.framework.request.coercer.MapTypes;

public class TaskBulkOpRequest extends TitoRequest {
    
    @Regex("delete|copyToCourse|moveToCategory")
    public String action;
    
    public String returnTo;
    
    @MapTypes(keyCoercer = LongCoercer.class,
              valueCoercer = BooleanCoercer.class)
    public Map<Long, Boolean> selected;
    
    @Optional
    public Long targetCourse;
    
    @Optional
    public Long targetCategory;
    
    @Override
    public Collection<RequestInvalidity> validate() {
        Collection<RequestInvalidity> invs = super.validate();
        
        if (action.equals("copyToCourse") && targetCourse == null)
            invs.add(new RequestInvalidity("target course not set"));
        
        // (it's always OK for targetCategory to be null)
        
        return invs;
    }
}
