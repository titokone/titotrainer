package fi.helsinki.cs.titotrainer.framework.session;

import java.io.Serializable;

import fi.helsinki.cs.titotrainer.framework.access.Role;

/**
 * Interface for the user sesssion data.
 */
public interface UserSession extends Serializable {
    
    /**
     * Returns the access control role of the user.
     * 
     * @return The role of the user (or null if none).
     */
    public Role getRole();
    
    /**
     * <p>Called when a session is reinstantiated due to a new
     * request. This way the session can make parts of its
     * data expire.</p>
     * 
     * <p>This method is not called for the hop on which the
     * session is created.</p>
     */
    public void hop();
    
}
