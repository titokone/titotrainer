package fi.helsinki.cs.titotrainer.app.model.access.hamcrest;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.TypeSafeMatcher;

import fi.helsinki.cs.titotrainer.app.model.User;
import fi.helsinki.cs.titotrainer.app.model.access.CurrentCredentials;
import fi.helsinki.cs.titotrainer.app.model.access.ModelAccessController;
import fi.helsinki.cs.titotrainer.framework.model.handler.ModelPermission;

/**
 * A hamcrest matcher checking whether the current user has access to a model object.
 */
public class CurrentUserHasAccess extends TypeSafeMatcher<Object> {
    
    private ModelPermission perm;
    
    public CurrentUserHasAccess(ModelPermission perm) {
        this.perm = perm;
    }
    
    @Override
    public void describeTo(Description description) {
        description.appendText("user has " + perm.toString() + " access");
    }
    
    @Override
    public boolean matchesSafely(Object item) {
        User user = CurrentCredentials.getCurrentUser();
        if (user != null) {
            return ModelAccessController.getInstance().hasPermission(user.getParentRole(), item, this.perm);
        } else {
            return false;
        }
    }
    
    @Factory
    public static CurrentUserHasAccess currentUserHasAccess(ModelPermission perm) {
        return new CurrentUserHasAccess(perm);
    }
    
}
