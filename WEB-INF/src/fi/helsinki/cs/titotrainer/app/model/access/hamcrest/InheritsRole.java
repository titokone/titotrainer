package fi.helsinki.cs.titotrainer.app.model.access.hamcrest;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.TypeSafeMatcher;

import fi.helsinki.cs.titotrainer.app.model.User;
import fi.helsinki.cs.titotrainer.framework.access.Role;

public class InheritsRole extends TypeSafeMatcher<User> {
    
    private Role role;
    
    public InheritsRole(Role role) {
        this.role = role;
    }
    
    public void describeTo(Description description) {
        description.appendText("inherits role " + role);
    };
    
    @Override
    public boolean matchesSafely(User item) {
        return item.getParentRole().inherits(role);
    }
    
    @Factory
    public static InheritsRole inheritsRole(Role role) {
        return new InheritsRole(role);
    }
    
}
