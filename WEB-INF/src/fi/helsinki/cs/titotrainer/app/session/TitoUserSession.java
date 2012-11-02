package fi.helsinki.cs.titotrainer.app.session;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import fi.helsinki.cs.titotrainer.app.access.TitoBaseRole;
import fi.helsinki.cs.titotrainer.app.model.User;
import fi.helsinki.cs.titotrainer.framework.access.Role;
import fi.helsinki.cs.titotrainer.framework.session.UserSession;

/**
 * <p>The user session data stored by TitoTrainer.</p>
 */
public class TitoUserSession implements UserSession {

    private static Role DEFAULT_ROLE = TitoBaseRole.GUEST;
    
    private User user;
    private Messenger messenger;
    private Map<String, Serializable> attributes;
    
    public TitoUserSession() {
        this.messenger = getDefaultMessenger();
        this.attributes = new HashMap<String, Serializable>();
    }
    
    /**
     * Returns the default {@link Messenger}.
     * Called from the constructor. By default,
     * simply creates a new {@link Messenger}.
     * 
     * @return A default messenger (may be null in principle).
     */
    protected Messenger getDefaultMessenger() {
        return new Messenger();
    }
    
    @Override
    public Role getRole() {
        if (user != null)
            return user.getParentRole();
        else
            return DEFAULT_ROLE;
    }
    
    public void setAuthenticatedUser(User user) {
        this.user = user;
    }
    
    public User getAuthenticatedUser() {
        return this.user;
    }
    
    /**
     * Returns the authenticated user's prefered locale,
     * or null if there is no authenticated user. Also null
     * if the user has no preference.
     * 
     * @return The locale to present for this user.
     */
    public Locale getUserLocale() {
        User user = this.getAuthenticatedUser();
        if (user != null) {
            return user.getPrefLocale();
        } else {
            return null;
        }
    }
    
    public synchronized void setAttribute(String key, Serializable value) {
        this.attributes.put(key, value);
    }
    
    public synchronized boolean hasAttribute(String key) {
        return this.attributes.containsKey(key);
    }
    
    public synchronized boolean hasAttribute(String key, Class<?> type) {
        return type.isAssignableFrom(this.attributes.get(key).getClass());
    }
    
    public synchronized Serializable getAttribute(String key) {
        return attributes.get(key);
    }
    
    public synchronized Serializable getAttribute(String key, Serializable defaultValue) {
        Serializable value = this.attributes.get(key);
        if (value != null)
            return value;
        else
            return defaultValue;
    }
    
    public synchronized <T> T getAttribute(String key, Class<T> type) {
        return getAttribute(key, type, null);
    }
    
    @SuppressWarnings("unchecked")
    public synchronized <T> T getAttribute(String key, Class<T> type, T defaultValue) {
        Serializable value = this.attributes.get(key);
        if (value != null && type.isAssignableFrom(value.getClass()))
            return (T)value;
        else
            return defaultValue;
    }
    
    public synchronized Serializable consumeAttribute(String key) {
        return this.attributes.remove(key);
    }
    
    public synchronized Serializable consumeAttribute(String key, Serializable defaultValue) {
        Serializable value = this.attributes.remove(key);
        if (value != null)
            return value;
        else
            return defaultValue;
    }
    
    public synchronized <T> T consumeAttribute(String key, Class<T> type) {
        return consumeAttribute(key, type, null);
    }
    
    @SuppressWarnings("unchecked")
    public synchronized <T> T consumeAttribute(String key, Class<T> type, T defaultValue) {
        Serializable value = this.attributes.get(key);
        if (value != null && type.isAssignableFrom(value.getClass())) {
            this.attributes.remove(key);
            return (T)value;
        } else {
            return defaultValue;
        }
    }
    
    public synchronized void clearAttributes() {
        attributes.clear();
    }
    
    /**
     * Returns the session's message store.
     * 
     * @return A non-null {@link Messenger} object.
     */
    public Messenger getMessenger() {
        return this.messenger;
    }
    
    /**
     * <p>For {@link TitoUserSession} this method
     * simply invokes {@link Messenger#hop()}.</p>
     */
    @Override
    public synchronized void hop() {
        this.messenger.hop();
    }
    
}
