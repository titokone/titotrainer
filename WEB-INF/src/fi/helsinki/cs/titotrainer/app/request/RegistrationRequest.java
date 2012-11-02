package fi.helsinki.cs.titotrainer.app.request;

import java.util.Collection;

import fi.helsinki.cs.titotrainer.framework.request.RequestInvalidity;

public class RegistrationRequest extends TitoRequest {
    
    public String username;
    public String firstName;
    public String lastName;
    public String email;
    public String studentNumber;
    
    public String prefLocale;
    
    public Long courseId;
    
    public String passwd;
    
    public String passwd2;
    
    @Override
    public Collection<RequestInvalidity> validate() {
        Collection<RequestInvalidity> invs = super.validate();
        
        if (!passwd.equals(passwd2)) {
            invs.add(new RequestInvalidity("password_mismatch"));
        }
        
        return invs;
    }
}
