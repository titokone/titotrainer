package fi.helsinki.cs.titotrainer.framework.access;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.hamcrest.collection.IsArrayContaining;
import org.junit.Test;

public class RoleTest {
    
    @Test
    public void shouldInheritIndirectParents() {
        Role a = new Role("a");
        Role b = new Role("b", a);
        Role c = new Role("c", b);

        assertThat(c.getParents(), new IsArrayContaining<Role>(sameInstance(b)));
        assertThat(c.getParents(), not(new IsArrayContaining<Role>(sameInstance(a))));
        assertThat(c.getParents(), not(new IsArrayContaining<Role>(sameInstance(c))));
        
        assertTrue(c.inherits(c));
        assertTrue(c.inherits(b));
        assertTrue(c.inherits(a));
        
        assertFalse(a.inherits(b));
        assertFalse(a.inherits(c));
    }
    
    @Test
    public void shouldSupportMultipleInheritance() {
        Role a = new Role("a");
        Role b = new Role("b", a);
        Role c = new Role("c", a, b);
        
        assertThat(c.getParents(), new IsArrayContaining<Role>(sameInstance(b)));
        assertThat(c.getParents(), new IsArrayContaining<Role>(sameInstance(a)));
        
        assertTrue(c.inherits(c));
        assertTrue(c.inherits(b));
        assertTrue(c.inherits(a));
        
        assertFalse(a.inherits(b));
        assertFalse(a.inherits(c));
    }
    
}