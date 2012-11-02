package fi.helsinki.cs.titotrainer.framework.access;

import java.io.Serializable;



/**
 * <p>A role, subject to access control.</p>
 * 
 * <p>Roles have a name, but it has no semantics.
 * In particular, the name is <b>not</b> guaranteed or
 * required to be in any way unique by this interface.</p>
 */
public class Role implements Serializable {
    
    private String name;
    
    private Role[] parents;
    
    public Role() {
        this.name = null;
        this.setParents(new Role[0]);
    }
    
    public Role(String name) {
        this.name = name;
        this.setParents(new Role[0]);
    }
    
    public Role(String name, Role ... parents) {
        this.name = name;
        this.setParents(parents);
    }
    
    /**
     * Returns the immediate parents of the role.
     * 
     * @return The parents of the role.
     */
    public final Role[] getParents() {
        return this.parents;
    }
    
    /**
     * Sets the immediate parents of the role.
     * 
     * @param parents The new parents. Must not contain null elements.
     */
    public void setParents(Role[] parents) {
        for (Role r : parents) {
            if (r == null)
                throw new NullPointerException("Parent role array may not contain nulls.");
        }
        this.parents = parents.clone();
    }
    
    /**
     * <p>Tells whether this role inherits another role either
     * directly or indirectly.</p>
     * 
     * <p>A role is considered to inherit itself.</p>
     * 
     * @param r The role that this role might inherit.
     * @return True iff this role inherits <code>r</code>.
     */
    public final boolean inherits(Role r) {
        if (r == this)
            return true;
        for (Role p : this.parents) {
            if (p.inherits(r))
                return true;
        }
        return false;
    }
    
    /**
     * <p>Returns the name of the role.</p>
     * 
     * <p>The name of a role is not necessarily unique.</p>
     * 
     * @return The name of the role. May be null.
     */
    public String getName() {
        return name;
    }
    
    /**
     * <p>Sets the name of the role.</p>
     * 
     * @param name The new name of the role. May be null.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Returns the name of the role as returned by {@link #getName()}.
     */
    @Override
    public String toString() {
        return this.getName();
    }
}
