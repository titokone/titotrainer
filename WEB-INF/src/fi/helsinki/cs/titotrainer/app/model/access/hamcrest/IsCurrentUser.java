package fi.helsinki.cs.titotrainer.app.model.access.hamcrest;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.TypeSafeMatcher;

import fi.helsinki.cs.titotrainer.app.model.User;
import fi.helsinki.cs.titotrainer.app.model.access.CurrentCredentials;

/**
 * A hamcrest matcher returning the current user object.
 * 
 * @see CurrentCredentials
 */
public class IsCurrentUser extends TypeSafeMatcher<User> {
    
    @Override
    public boolean matchesSafely(User item) {
        User currentUser = CurrentCredentials.getCurrentUser();
        if (currentUser == null)
            return false;
        return (currentUser.equals(item));
    }
    
    @Override
    public void describeTo(Description description) {
        description.appendText("is current user");
    }
    
    @Factory
    public static IsCurrentUser isCurrentUser() {
        return new IsCurrentUser();
    }
}
