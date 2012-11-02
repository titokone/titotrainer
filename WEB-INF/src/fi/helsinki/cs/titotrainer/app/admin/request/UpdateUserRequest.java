package fi.helsinki.cs.titotrainer.app.admin.request;

import fi.helsinki.cs.titotrainer.framework.request.Optional;

public class UpdateUserRequest extends AbstractUserModificationRequest {
    
    public long id;
    
    @Optional
    public boolean setPassword;
}
