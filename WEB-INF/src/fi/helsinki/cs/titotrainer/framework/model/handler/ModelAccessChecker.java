package fi.helsinki.cs.titotrainer.framework.model.handler;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.event.PostLoadEvent;
import org.hibernate.event.PostLoadEventListener;
import org.hibernate.event.PreDeleteEvent;
import org.hibernate.event.PreDeleteEventListener;
import org.hibernate.event.PreInsertEvent;
import org.hibernate.event.PreInsertEventListener;
import org.hibernate.event.PreUpdateEvent;
import org.hibernate.event.PreUpdateEventListener;

import fi.helsinki.cs.titotrainer.framework.access.AccessController;
import fi.helsinki.cs.titotrainer.framework.access.Role;

/**
 * <p>Provides model-layer access control.</p>
 * 
 * <p>The access control policy is specified using an {@link AccessController}
 * with {@link ModelPermission} as the permissions.</p>
 */
public class ModelAccessChecker implements PostLoadEventListener,
                                           PreInsertEventListener,
                                           PreUpdateEventListener,
                                           PreDeleteEventListener {
    private static class SessionPolicy {
        private AccessController ac;
        private Role role;
        
        public SessionPolicy(AccessController ac, Role role) {
            if (ac == null)
                throw new NullPointerException();
            if (role == null)
                throw new NullPointerException();
            this.ac = ac;
            this.role = role;
        }
    }
    
    private static Map<Session, SessionPolicy> sessionPolicies; // Synchronized WeakHashMap.
    
    static {
        sessionPolicies = Collections.synchronizedMap(new WeakHashMap<Session, SessionPolicy>());
    }
    
    /**
     * <p>Enables access checks for a hibernate session.</p>
     * 
     * <p>A weak reference to the session is stored along with references to
     * the access controller and the role.
     * Make sure neither the access controller nor the role contains
     * strong references to the session or there will be a memory leak.</p>
     * 
     * <p>If this is called multiple times on the same session,
     * the latest call will override the effects of any previous ones.</p>
     * 
     * @param hs The hibernate session to enable checks for.
     * @param ac The access controller to use for the access checks.
     * @param role The role whose access to check.
     */
    public static void enableForSession(Session hs, AccessController ac, Role role) {
        sessionPolicies.put(hs, new SessionPolicy(ac, role));
    }
    
    /**
     * Disables access checks for a hibernate session.
     */
    public static void disableForSession(Session hs) {
        sessionPolicies.remove(hs);
    }
    
    /**
     * Checks for a permission on a resource.
     * 
     * @param hs The Hibernate session.
     * @param res The resource.
     * @param perm The permission.
     * @throws ModelAccessDeniedException If there was no permission.
     */
    protected void check(Session hs, Object res, ModelPermission perm) {
        SessionPolicy policy = sessionPolicies.get(hs);
        if (policy != null) {
            if (!policy.ac.hasPermission(policy.role, res, perm))
                throw new ModelAccessDeniedException(policy.role, res, perm);
        }
    }
    
    @Override
    public void onPostLoad(PostLoadEvent event) throws HibernateException {
        check(event.getSession(), event.getEntity(), ModelPermission.READ);
    }

    @Override
    public boolean onPreInsert(PreInsertEvent event) {
        check(event.getSession(), event.getEntity(), ModelPermission.CREATE);
        return false;
    }

    @Override
    public boolean onPreUpdate(PreUpdateEvent event) {
        check(event.getSession(), event.getEntity(), ModelPermission.UPDATE);
        return false;
    }
    
    @Override
    public boolean onPreDelete(PreDeleteEvent event) {
        check(event.getSession(), event.getEntity(), ModelPermission.DELETE);
        return false;
    }

}
