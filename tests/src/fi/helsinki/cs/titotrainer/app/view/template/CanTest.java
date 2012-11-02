package fi.helsinki.cs.titotrainer.app.view.template;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import fi.helsinki.cs.titotrainer.app.access.TitoBaseRole;
import fi.helsinki.cs.titotrainer.app.controller.ModuleFrontController;
import fi.helsinki.cs.titotrainer.app.model.Course;
import fi.helsinki.cs.titotrainer.app.model.User;
import fi.helsinki.cs.titotrainer.app.model.access.CurrentCredentials;
import fi.helsinki.cs.titotrainer.framework.RequestHandler;
import fi.helsinki.cs.titotrainer.framework.access.AccessController;
import fi.helsinki.cs.titotrainer.framework.access.Role;

public class CanTest {
    
    private User user;
    private RequestHandler<?> mockHandler;
    private ModuleFrontController mockFc;
    private Can can;
    
    @Before
    public void setUp() {
        switchRole(TitoBaseRole.STUDENT);

        this.mockHandler = Mockito.mock(RequestHandler.class);
        
        AccessController ac = new AccessController() {
            @Override
            public boolean hasAccess(Role role, Object resource) {
                if (resource == null)
                    throw new NullPointerException();
                return (role == CurrentCredentials.getCurrentUser().getParentRole());
            }

            @Override
            public boolean hasPermission(Role role, Object resource, Object permission) {
                return hasAccess(role, resource);
            }
        };
        
        this.mockFc = Mockito.mock(ModuleFrontController.class);
        Mockito.doReturn(null).when(mockFc).getHandlerForPrefix(Mockito.anyString());
        Mockito.doReturn(ac).when(mockFc).getAccessController();
        
        this.can = new Can(mockFc);
    }
    
    private void switchRole(TitoBaseRole role) {
        this.user = new User("testUser", role);
        CurrentCredentials.setCurrentUser(user);
    }
    
    @Test
    public void shouldReturnFalseForUnregisteredPaths() {
        assertFalse(can.accessPath("foobar"));
    }
    
    @Test
    public void shouldReturnTrueForRegisteredPathsIfUserHasAccess() {
        Mockito.doReturn(mockHandler).when(mockFc).getHandlerForPrefix("abc");
        assertTrue(can.accessPath("abc"));
    }
    
    @Test
    public void shouldReturnEntityCreatePermissionsCorrectly() {
        assertFalse(can.createAny("Course"));
        
        switchRole(TitoBaseRole.ADMINISTRATOR);
        
        assertTrue(can.createAny("Course"));
        
    }
    
    @Test
    public void shouldReturnEntityUpdatePermissionsCorrectly() {
        Course course = new Course();
        
        assertFalse(can.update(course));
        assertFalse(can.updateAny("Course"));
        
        switchRole(TitoBaseRole.ADMINISTRATOR);
        
        assertTrue(can.update(course));
        assertTrue(can.updateAny("Course"));
    }
    
    @Test
    public void shouldReturnEntityDeletePermissionsCorrectly() {
        Course course = new Course();
        
        assertFalse(can.delete(course));
        assertFalse(can.deleteAny("Course"));
        
        switchRole(TitoBaseRole.ADMINISTRATOR);
        
        assertTrue(can.delete(course));
        assertTrue(can.deleteAny("Course"));
    }
}
