package fi.helsinki.cs.titotrainer.framework.access.hamcrest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.hamcrest.Matcher;

import fi.helsinki.cs.titotrainer.framework.access.AccessController;
import fi.helsinki.cs.titotrainer.framework.access.Role;

/**
 * <p>An access controller based on matching resources using Hamcrest.</p>
 * 
 * <p>
 * Access is specified by a list of allow/deny rules on roles, resources and permissions.
 * A rule matches if the queried role inherits the rule's role, the queried resource
 * matches the role's Hamcrest resource object matcher and the permission matches.
 * </p>
 * 
 * <p>
 * Rules are applied from first to last so that the last rule to match is actually used.
 * That is, more general rules should be added first and specific exceptions to them
 * should be added last. If no rule matches, permission is denied by default.
 * </p>
 * 
 * <p>
 * Specifying a rule without a permission makes it apply to all permissions.
 * There is a minor caveat here: "all permissions" technically means "all possible Java objects",
 * so if you e.g. make separate allow rules for all permissions you actually use and then
 * ask whether a role has "all permissions", you will get a negative answer.
 * That is, if a role can only have "all permissions" if there is an allow rule that gives
 * "all permissions".
 * </p>
 */
public class HamcrestAccessController implements AccessController {
    
    protected static enum RuleType {
        ALLOW,
        DENY
    }
    
    protected static class Rule {
        public final Matcher<?> matcher;
        public final Role role;
        public final RuleType type;
        public final Object permission; // May be null
        public Rule(Matcher<?> matcher, Role role, RuleType type, Object permission) {
            this.matcher = matcher;
            this.role = role;
            this.type = type;
            this.permission = permission;
        }
    }
    
    protected List<Rule> rules;
    protected boolean noDenyRules; // No DENY rules in rules. Allows optimization if true.
    protected Lock rulesReadLock;
    protected Lock rulesWriteLock;
    
    public HamcrestAccessController() {
        this.rules = new ArrayList<Rule>();
        
        ReentrantReadWriteLock rulesLock = new ReentrantReadWriteLock();
        this.rulesReadLock = rulesLock.readLock();
        this.rulesWriteLock = rulesLock.writeLock();
        this.noDenyRules = true;
    }
    
    protected void addRule(Role role, Matcher<?> resourceMatcher, Object permission, RuleType ruleType) {
        if (role == null)
            throw new NullPointerException("role may not be null");
        if (resourceMatcher == null)
            throw new NullPointerException("resourceMatcher may not be null");
        
        Rule rule = new Rule(resourceMatcher, role, ruleType, permission);
        this.rulesWriteLock.lock();
        try {
            this.rules.add(rule);
            if (ruleType == RuleType.DENY)
                this.noDenyRules = false;
        } finally {
            this.rulesWriteLock.unlock();
        }
    }
    
    /**
     * <p>Adds a rule that allows access (all permissions) to a resource matching
     * a Hamcrest matcher for the given role and any roles
     * that inherit it.</p>
     * 
     * @param role The role to assign the privilege to.
     * @param resourceMatcher The Hamcrest matcher to match the affected resources.
     * @throws NullPointerException if either parameter is null.
     */
    public void allow(Role role, Matcher<?> resourceMatcher) {
        allow(role, resourceMatcher, (Object)null);
    }
    
    /**
     * <p>Adds a rule that gives a permission to a resource matching
     * a Hamcrest matcher for the given role and any roles
     * that inherit it.</p>
     * 
     * @param role The role to assign the privilege to.
     * @param resourceMatcher The Hamcrest matcher to match the affected resources.
     * @param permission The permission to allow. If this is null,
     *                   the call is equivalent to {@link #allow(Role, Matcher)}.
     * @throws NullPointerException if either parameter is null.
     */
    public void allow(Role role, Matcher<?> resourceMatcher, Object permission) {
        addRule(role, resourceMatcher, permission, RuleType.ALLOW);
    }
    
    public void allow(Role role, Matcher<?> resourceMatcher, Object ... permissions) {
        if (permissions != null) {
            for (Object p : permissions) {
                allow(role, resourceMatcher, p);
            }
        } else {
            allow(role, resourceMatcher, (Object)null);
        }
    }
    
    /**
     * <p>Adds a rule that denies access (all permissions) to a resource matching
     * a Hamcrest matcher for the given role and any roles
     * that inherit it.</p>
     * 
     * @param role The role to assign the privilege to.
     * @param resourceMatcher The Hamcrest matcher to match the affected resources.
     * @throws NullPointerException if either parameter is null.
     */
    public void deny(Role role, Matcher<?> resourceMatcher) {
        deny(role, resourceMatcher, (Object)null);
    }
    
    /**
     * <p>Adds a rule that denies a permission to a resource matching
     * a Hamcrest matcher for the given role and any roles
     * that inherit it.</p>
     * 
     * @param role The role to assign the privilege to.
     * @param resourceMatcher The Hamcrest matcher to match the affected resources.
     * @param permission The permission to allow. If this is null,
     *                   the call is equivalent to {@link #allow(Role, Matcher)}.
     * @throws NullPointerException if either parameter is null.
     */
    public void deny(Role role, Matcher<?> resourceMatcher, Object permission) {
        addRule(role, resourceMatcher, permission, RuleType.DENY);
    }
    
    public void deny(Role role, Matcher<?> resourceMatcher, Object ... permissions) {
        if (permissions != null) {
            for (Object p : permissions) {
                deny(role, resourceMatcher, p);
            }
        } else {
            deny(role, resourceMatcher, (Object)null);
        }
    }
    
    /**
     * <p>Checks access to a resource.</p>
     * 
     * <p>
     * Returns true iff there is a matcher that matches the
     * resource and that matcher was defined to allow a role
     * that is inherited by the given <code>role</code>.
     * </p>
     * 
     * @param role The role to match against.
     * @param resource The resource object to match againtst.
     */
    @Override
    public boolean hasAccess(Role role, Object resource) {
        return hasPermission(role, resource, null);
    }
    
    /**
     * <p>Checks permission on a resource.</p>
     * 
     * <p>
     * Returns true iff there is a matcher that matches the
     * resource and that matcher was defined to allow a role
     * that is inherited by the given <code>role</code>
     * and the permission for the matcher equals the queried permission.
     * </p>
     * 
     * <p>Note that a null permission is equivalent to "having access" but
     * having access does not imply having all permissions. That is,
     * even after a call to {@link #allow(Role, Matcher)} (the overload
     * taking no permission and giving "access"),
     * this method may still return false for non-null permissions.</p>
     * 
     * @param role The role to match against.
     * @param resource The resource object to match againtst.
     * @param permission The permission to match against. If this is null then
     *                   the call is equivalent to {@link #hasAccess(Role, Object)}.
     */
    @Override
    public boolean hasPermission(Role role, Object resource, Object permission) {
        if (role == null)
            throw new NullPointerException("role may not be null");
        if (resource == null)
            throw new NullPointerException("resource may not be null"); 
        
        this.rulesReadLock.lock();
        boolean allowed = false;
        try {
            for (Rule rule : this.rules) {
                if (rule.permission == null || rule.permission.equals(permission)) {
                    if (role.inherits(rule.role)) {
                        if (rule.matcher.matches(resource)) {
                            if (noDenyRules)
                                return true; // Optimization
                            
                            allowed = (rule.type == RuleType.ALLOW);
                        }
                    }
                }
            }
        } finally {
            this.rulesReadLock.unlock();
        }
        return allowed;
    }
}
