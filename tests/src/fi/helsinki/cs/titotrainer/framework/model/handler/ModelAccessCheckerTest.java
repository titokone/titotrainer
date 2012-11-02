package fi.helsinki.cs.titotrainer.framework.model.handler;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import fi.helsinki.cs.titotrainer.framework.access.AccessController;
import fi.helsinki.cs.titotrainer.framework.access.Role;
import fi.helsinki.cs.titotrainer.testsupport.TitoTestCase;
import fi.helsinki.cs.titotrainer.testsupport.framework.model.TestDbEntity;

public class ModelAccessCheckerTest extends TitoTestCase {
    
    private Role role;
    private AccessController ac;
    private Session hs;
    
    private TestDbEntity res1;
    private TestDbEntity res2;
    
    @Before
    public void setUp() {
        this.role = new Role("role");
        this.hs = this.openSession();
        
        this.res1 = new TestDbEntity("foo", "FOO");
        this.res2 = new TestDbEntity("bar", "BAR");
        this.hs.save(res1);
        this.hs.save(res2);
        this.hs.flush();
        this.hs.close();
        
        this.hs = this.openAutoclosedSession(); // Our load handlers don't get called if we reuse the same session, which I find weird to say the least
        
        this.ac = Mockito.mock(AccessController.class);
        Mockito.doReturn(true).when(ac).hasPermission((Role)Mockito.anyObject(), Mockito.anyObject(), Mockito.anyObject());
        
        ModelAccessChecker.enableForSession(this.hs, ac, this.role);
    }
    
    @Test
    public void shouldCheckReadPermissionOnLoad() {
        Mockito.doReturn(false).when(ac).hasPermission(role, res2, ModelPermission.READ);
        
        assertThat(this.hs.load(TestDbEntity.class, "foo"), instanceOf(TestDbEntity.class)); // Should not throw
        assertThat(this.hs.createQuery("FROM TestDbEntity WHERE key = ?").setString(0, "foo").uniqueResult(), instanceOf(TestDbEntity.class)); // Should not throw
        
        boolean caught = false;
        try {
            this.hs.createQuery("FROM TestDbEntity WHERE key = ?").setString(0, "bar").uniqueResult();
        } catch (Exception e) {
            caught = true;
        }
        assertTrue(caught);
    }
    
    @Test(expected = ModelAccessDeniedException.class)
    public void shouldCheckReadPermissionOnInsert() {
        TestDbEntity newRes = new TestDbEntity("newOne", "newOne");
        Mockito.doReturn(false).when(ac).hasPermission(role, newRes, ModelPermission.CREATE);
        
        this.hs.save(newRes);
        this.hs.flush();
    }
    
    @Test(expected = ModelAccessDeniedException.class)
    public void shouldCheckReadPermissionOnUpdate() {
        Mockito.doReturn(false).when(ac).hasPermission(role, res1, ModelPermission.UPDATE);
        
        res1.setValue("newValue");
        this.hs.saveOrUpdate(res1);
        this.hs.flush();
    }
    
    @Test(expected = ModelAccessDeniedException.class)
    public void shouldCheckReadPermissionOnDelete() {
        Mockito.doReturn(false).when(ac).hasPermission(role, res1, ModelPermission.DELETE);
        
        this.hs.delete(res1);
        this.hs.flush();
    }
}
