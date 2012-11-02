package fi.helsinki.cs.titotrainer.framework.request;

import static org.junit.Assert.*;

import org.junit.Test;


public class RegexTest {
    
    @Test(expected = IllegalStateException.class)
    public void regexAnnotationOnNonStringShouldCauseIllegalArgumentExceptionOnValidate() {
        @SuppressWarnings("unused")
        class TestRequest extends Request {
            @Regex("")
            public int field;
        }
        
        TestRequest req = null;
        try {
            req = new TestRequest();
        } catch (IllegalStateException e) {
        }
        if (req != null)
            req.validate();
    }
    
    @Test
    public void regexAnnotationShouldBeCheckedDuringValidate() {
        @SuppressWarnings("unused")
        class TestRequest extends Request {
            @Regex("[a-z]")
            public String field1;
            
            @Optional
            @Regex("[0-9]+")
            public String field2;
        }
        
        TestRequest validReq = new TestRequest();
        validReq.field1 = "x";
        validReq.field2 = "123";
        
        TestRequest invalidReq = new TestRequest();
        invalidReq.field1 = "abc";
        invalidReq.field2 = "asd";
        
        assertTrue(validReq.validate().isEmpty());
        assertEquals(2, invalidReq.validate().size());
        assertEquals(Request.INVALIDITY_MSG_BAD_FORMAT, invalidReq.validate().iterator().next().getMsgKey());
    }
    
    @Test
    public void regexAnnotationShouldNotBeCheckedOnEmptyOptionalFields() {
        @SuppressWarnings("unused")
        class TestRequest extends Request {
            @Optional
            @Regex("^[a-z]$")
            public String field;
        }
        
        TestRequest req = new TestRequest();
        req.field = null;
        assertTrue(req.validate().isEmpty());
        
        req.field = "";
        assertTrue(req.validate().isEmpty());
    }
    
}
