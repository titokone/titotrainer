package fi.helsinki.cs.titotrainer.framework.misc;

import static org.junit.Assert.*;

import org.junit.Test;

public class SomeTest {

    @Test
    public void hasValueMethodShouldReturnTrue() {
        assertTrue(new Some<String>("foo").hasValue());
        assertTrue(new Some<String>(null).hasValue());
    }
    
    @Test
    public void getValueMethodShouldReturnValue() {
        Object o = new Object();
        assertSame(new Some<Object>(o).getValue(), o);
        assertSame(new Some<Object>(null).getValue(), null);
    }
    
    @Test
    public void equalsMethodShouldCompareValues() {
        String a = "foo";
        String b = new String(a);
        String c = "bar";
        
        assertTrue(new Some<String>(a).equals(new Some<String>(b)));
        assertFalse(new Some<String>(a).equals(new Some<String>(c)));
        assertTrue(new Some<Object>(null).equals(new Some<String>(null)));
        assertFalse(new Some<Object>(a).equals(new Some<String>(null)));
        assertFalse(new Some<Object>(null).equals(new Some<String>(a)));
    }
    
    @Test
    public void equalsMethodShouldNotReturnTrueWhenComparingToDirectValue() {
        String a = "foo";
        assertFalse(new Some<String>(a).equals(a));
    }
    
    @Test
    public void hashCodeMethodShouldReturnValuesHashCode() {
        String a = "foo";
        assertEquals(new Some<String>(a).hashCode(), a.hashCode());
    }
    
    @Test
    public void hashCodeMethodShouldReturnSameValueForNulls() {
        assertEquals(new Some<Object>(null).hashCode(), new Some<Object>(null).hashCode());
    }
    
}
