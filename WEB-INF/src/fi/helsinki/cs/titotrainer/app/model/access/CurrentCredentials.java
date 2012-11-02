package fi.helsinki.cs.titotrainer.app.model.access;

import fi.helsinki.cs.titotrainer.app.model.User;

/**
 * Stores the current credentials in Thread-Local storage.
 */
public class CurrentCredentials {
    private static final InheritableThreadLocal<User> currentUser = new InheritableThreadLocal<User>();
    
    /**
     * Clears all credentials from the current thread.
     */
    public static void clear() {
        currentUser.remove();
    }
    
    /**
     * Sets the user of the current thread.
     */
    public static void setCurrentUser(User user) {
        currentUser.set(user);
    }
    
    /**
     * Returns the user of the current thread.
     * @return 
     */
    public static User getCurrentUser() {
        return currentUser.get();
    }
}
