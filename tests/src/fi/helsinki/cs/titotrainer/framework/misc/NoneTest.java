package fi.helsinki.cs.titotrainer.framework.misc;

import static org.junit.Assert.*;

import org.junit.Test;

public class NoneTest {
    
    @Test
    public void hasValueMethodShouldReturnFalse() {
        assertFalse(new None<Object>().hasValue());
    }
    
    @Test(expected = ClassCastException.class)
    public void getValueMethodShouldThrowClassCastException() {
        new None<Object>().getValue();
    }
    
    @Test
    public void shouldBeEqualToAllOtherNoneObjects() {
        assertEquals(new None<Object>(), new None<Object>());
    }
    
    @Test
    public void hashCodeShouldBeEqualBetweenAllNoneObjects() {
        assertEquals(new None<Object>().hashCode(), new None<Object>().hashCode());
    }
}
