package fi.helsinki.cs.titotrainer.framework.access.hamcrest;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import fi.helsinki.cs.titotrainer.framework.access.Role;
import fi.helsinki.cs.titotrainer.framework.misc.Maybe;

public class HamcrestAccessControllerTest {

    private HamcrestAccessController hac;
    
    @Before
    public void setUp() {
        this.hac = new HamcrestAccessController();
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void resourceMatcherShouldDetermineAccess() {
        Role role = new Role("role");
        
        Maybe<String> resMock = Mockito.mock(Maybe.class);
        Mockito.stub(resMock.getValue()).toReturn("foo");
        
        hac.allow(role, hasProperty("value", equalTo("foo")));
        
        assertTrue(hac.hasAccess(role, resMock));
        Mockito.verify(resMock).getValue();
    }
    
    @Test
    public void childRoleShouldInheritParentRoleAccess() {
        Role parent = new Role("parent");
        Role child = new Role("child", parent);
        Role other = new Role("other");
        Object res = new Object();
        
        assertFalse(hac.hasAccess(child, res));
        hac.allow(parent, anything());
        assertTrue(hac.hasAccess(child, res));
        assertFalse(hac.hasAccess(other, res));
        
        hac = new HamcrestAccessController();
        
        assertFalse(hac.hasPermission(child, res, "foo"));
        hac.allow(parent, anything(), "foo");
        assertTrue(hac.hasPermission(child, res, "foo"));
        assertFalse(hac.hasPermission(other, res, "foo"));
    }
    
    @Test
    public void denyRulesCanBeUsedToMakeExceptions() {
        Role parent = new Role("parent");
        Role child = new Role("child", parent);
        Object res = new Object();

        assertFalse(hac.hasPermission(parent, res, "foo"));
        assertFalse(hac.hasPermission(child, res, "foo"));
        assertFalse(hac.hasPermission(parent, res, "bar"));
        assertFalse(hac.hasPermission(child, res, "bar"));
        
        hac.allow(parent, anything(), "foo", "bar");
        
        assertTrue(hac.hasPermission(parent, res, "foo"));
        assertTrue(hac.hasPermission(child, res, "foo"));
        assertTrue(hac.hasPermission(parent, res, "bar"));
        assertTrue(hac.hasPermission(child, res, "bar"));
        
        hac.deny(child, anything(), "foo");
        
        assertTrue(hac.hasPermission(parent, res, "foo"));
        assertFalse(hac.hasPermission(child, res, "foo"));
        assertTrue(hac.hasPermission(parent, res, "bar"));
        assertTrue(hac.hasPermission(child, res, "bar"));
    }
    
    @Test
    public void nullPermissionShouldEqualAccess() {
        Role role = new Role("role");
        Object res = new Object();
        Object perm = new Object();
        
        hac.allow(role, anything(), perm);
        assertFalse(hac.hasAccess(role, res));
        assertFalse(hac.hasPermission(role, res, null));
        
        hac.allow(role, anything(), (Object)null);
        assertTrue(hac.hasAccess(role, res));
        assertTrue(hac.hasPermission(role, res, null));
    }

    @Test
    public void accessShouldImplyAllPermissions() {
        Role role = new Role("role");
        Object res = new Object();
        Object perm = new Object();
        
        hac.allow(role, anything());
        assertTrue(hac.hasPermission(role, res, perm));
        hac.deny(role, anything());
        assertFalse(hac.hasPermission(role, res, perm));
    }
}
