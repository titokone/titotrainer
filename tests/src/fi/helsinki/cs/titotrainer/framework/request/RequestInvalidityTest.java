package fi.helsinki.cs.titotrainer.framework.request;

import static org.junit.Assert.*;

import org.junit.Test;


public class RequestInvalidityTest {
    
    @Test
    public void defaultConstructorShouldLeaveFieldAsNull() {
        RequestInvalidity ri = new RequestInvalidity("foo");
        assertEquals(ri.field, null);
        assertEquals(ri.msg, "foo");
    }
    
    @Test
    public void constructorsShouldAcceptNullParameters() {
        RequestInvalidity ri = new RequestInvalidity(null);
        assertEquals(ri.field, null);
        assertEquals(ri.msg, "");
        
        new RequestInvalidity(null, null);
        assertEquals(ri.field, null);
        assertEquals(ri.msg, "");
    }
    
}