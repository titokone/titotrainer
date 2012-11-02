package fi.helsinki.cs.titotrainer.framework.access;

/**
 * <p>Defines access of roles to resources.</p>
 * 
 * <p>An implementation must be fully thread-safe.</p>
 */
public interface AccessController {
    /**
     * Checks whether a role has access to a resource.
     * 
     * An implementation is free to define what it means to
     * "have access to a resource". For instance an implementation
     * only giving specific permissions might always return false here.
     * 
     * @param role The role. Not null.
     * @param resource The resource. Not null.
     * @return Whether the <code>role</code> has access to <code>resource</code>.
     * @throws NullPointerException if either parameter is null.
     */
    public boolean hasAccess(Role role, Object resource);
    
    /**
     * Checks whether a role has the specified permission on a resource.
     * 
     * @param role The role. Not null.
     * @param resource The resource. Not null.
     * @param permission The permission. May be null. Compared using {@link Object#equals(Object)}. Usually a string or an enum.
     * @return Whether the <code>role</code> has <code>permission</code> to <code>resource</code>. 
     * @throws NullPointerException if <code>role</code> or <code>resource</code> is null.
     */
    public boolean hasPermission(Role role, Object resource, Object permission);
}
