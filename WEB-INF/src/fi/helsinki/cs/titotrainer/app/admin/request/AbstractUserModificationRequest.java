package fi.helsinki.cs.titotrainer.app.admin.request;

import java.util.Collection;

import fi.helsinki.cs.titotrainer.app.access.TitoBaseRole;
import fi.helsinki.cs.titotrainer.app.request.TitoRequest;
import fi.helsinki.cs.titotrainer.framework.request.Optional;
import fi.helsinki.cs.titotrainer.framework.request.RequestInvalidity;

public abstract class AbstractUserModificationRequest extends TitoRequest {
    public String username;
    
    public String role;
    
    @Optional
    public String firstName;
    @Optional
    public String lastName;
    @Optional
    public String email;
    @Optional
    public String studentNumber;
    
    @Optional
    public String prefLocale;
    
    @Optional
    public Long courseId;
    
    @Optional
    public String passwd;
    
    @Override
    public Collection<RequestInvalidity> validate() {
        Collection<RequestInvalidity> invs = super.validate();
        
        if (username == null || username.trim().isEmpty())
            invs.add(new RequestInvalidity("empty_username", "username"));
        
        TitoBaseRole role = TitoBaseRole.getRoleByName(this.role);
        if (role == null || !role.isConcrete() || role == TitoBaseRole.GUEST) {
            invs.add(new RequestInvalidity("invalid_role", "role"));
        }
        
        return invs;
    }
}
