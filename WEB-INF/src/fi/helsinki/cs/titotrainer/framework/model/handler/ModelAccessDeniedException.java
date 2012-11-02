package fi.helsinki.cs.titotrainer.framework.model.handler;

import org.hibernate.HibernateException;

import fi.helsinki.cs.titotrainer.framework.access.Role;

/**
 * Thrown by AccessInterceptor when access to an entity is denied.
 * 
 * @see ModelAccessChecker
 */
public class ModelAccessDeniedException extends HibernateException {

    public ModelAccessDeniedException(Role role, Object res, Object perm) {
        super(perm + " access denied for " + role + " to " + res);
    }
    
}
