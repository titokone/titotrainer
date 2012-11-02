package fi.helsinki.cs.titotrainer.app.access;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import fi.helsinki.cs.titotrainer.framework.access.Role;

/**
 * <p>Defines the built-in access control roles of TitoTrainer.</p>
 * 
 * <p>The names of these roles alwasy begin with an '@' character.</p>
 * 
 * <p>{@link TitoBaseRole}s can always be compared by identity as there is
 * only one instance of each.</p>
 * 
 * @see FrontAccess
 */
public class TitoBaseRole extends Role implements Serializable {
    
    private static Map<String, TitoBaseRole> lookup = new HashMap<String, TitoBaseRole>();
    
    public static TitoBaseRole ANYONE = new TitoBaseRole("@ANYONE", false);
    
    public static TitoBaseRole GUEST = new TitoBaseRole("@GUEST", true, ANYONE);
    public static TitoBaseRole STUDENT = new TitoBaseRole("@STUDENT", true, ANYONE);
    
    public static TitoBaseRole ADMINISTRATIVE = new TitoBaseRole("@ADMINISTRATIVE", false, STUDENT);
    
    public static TitoBaseRole ASSISTANT = new TitoBaseRole("@ASSISTANT", true, ADMINISTRATIVE);
    public static TitoBaseRole EDITOR = new TitoBaseRole("@EDITOR", true, ASSISTANT);
    public static TitoBaseRole ADMINISTRATOR = new TitoBaseRole("@ADMINISTRATOR", true, EDITOR);
    
    /**
     * To ensure {@link Role} objects are always unique,
     * we serialize their names instead.
     */
    public static class Serialized implements Serializable {
        public String name;
        public Serialized(String name) {
            this.name = name;
        }
        
        public Object readResolve() throws ObjectStreamException {
            return getRoleByName(this.name);
        }
    }
    
    public Object writeReplace() throws ObjectStreamException {
        return new Serialized(this.getName());
    }
    
    /**
     * Returns the localized name of the role.
     */
    public String getName(Locale locale) {
        try {
            ResourceBundle rb = ResourceBundle.getBundle(TitoBaseRole.class.getName(), locale);
            return rb.getString(this.getName());
        } catch (MissingResourceException e) {
            return this.toString();
        }
    }
    
    /**
     * Returns a base role by its name.
     * @param name The role's name (e.g. <code>"@STUDENT"</code>).
     * @return The base role, or null if not found.
     */
    public static TitoBaseRole getRoleByName(String name) {
        return lookup.get(name);
    }
    
    
    private boolean concrete;
    
    private TitoBaseRole(String name, boolean concrete, TitoBaseRole ... parents) {
        super(name, parents);
        
        this.concrete = concrete;
        
        if (!name.startsWith("@")) {
            throw new IllegalArgumentException("TitoBaseRole name must begin with an '@'");
        }
        lookup.put(name, this);
    }
    
    public boolean isConcrete() {
        return concrete;
    }
    
}
