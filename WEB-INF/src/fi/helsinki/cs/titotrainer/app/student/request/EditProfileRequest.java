package fi.helsinki.cs.titotrainer.app.student.request;

import java.util.Collection;

import fi.helsinki.cs.titotrainer.app.request.TitoRequest;
import fi.helsinki.cs.titotrainer.framework.request.Optional;
import fi.helsinki.cs.titotrainer.framework.request.RequestInvalidity;

public class EditProfileRequest extends TitoRequest {
    @Optional
    public String lastName;
    @Optional
    public String firstName;
    @Optional
    public String prefLocale;
    @Optional
    public String email;
    
    @Optional
    public Long courseId; // Only admins can change this for themselves.
    
    @Optional
    public String passwd;
    @Optional
    public String passwd2;
    
    
    @Override
    public Collection<RequestInvalidity> validate() {
        Collection<RequestInvalidity> invalidities = super.validate();
        
        if (passwd != null) {
            if (!passwd.equals(passwd2)) {
                invalidities.add(new RequestInvalidity("passwd_do_not_match", "passwd"));
            }
        }
        
        return invalidities;
    }
    
}
