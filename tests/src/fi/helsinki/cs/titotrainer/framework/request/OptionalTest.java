package fi.helsinki.cs.titotrainer.framework.request;

import static org.junit.Assert.*;

import java.util.Collection;

import org.junit.Test;

import fi.helsinki.cs.titotrainer.testsupport.framework.request.RequestWithPublicMethods;


public class OptionalTest {
    private static class GenericTestRequest extends Request {
        
        public Integer normalField;
        
        @Optional
        public Integer optionalField;
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void isOptionalShouldThrowOnNonexistentField() {
        new GenericTestRequest().isFieldOptional("foo");
    }
    
    @Test
    public void fieldsWithOptionalAnnotationShouldBeConsideredOptional() {
        assertTrue(new GenericTestRequest().isFieldOptional("optionalField"));
    }
    
    @Test
    public void fieldsWithoutOptionalAnnotationShouldNotBeConsideredOptional() {
        assertFalse(new GenericTestRequest().isFieldOptional("normalField"));
    }
    
    @Test
    public void validateMandatoryFieldsMethodShouldDetectNullValue() {
        @SuppressWarnings("unused")
        class TestRequest extends RequestWithPublicMethods {
            public Object field = null;
        }
        
        assertEquals(Request.INVALIDITY_MSG_MANDATORY_FIELD_EMPTY,
            new TestRequest().validateMandatoryFields().iterator().next().getMsgKey());
    }
    
    @Test
    public void validateMandatoryFieldsMethodShouldDetectEmptyString() {
        @SuppressWarnings("unused")
        class TestRequest extends RequestWithPublicMethods {
            public String emptyString = "";
            @Optional
            public String optionalEmptyString = "";
        }
        
        Collection<RequestInvalidity> invs = new TestRequest().validateMandatoryFields();
        assertEquals(1, invs.size());
        assertEquals("emptyString", invs.iterator().next().getField());
        assertEquals(Request.INVALIDITY_MSG_MANDATORY_FIELD_EMPTY, invs.iterator().next().getMsgKey());
    }
    
    @Test
    public void validateMandatoryFieldsMethodShouldNotReactToNonEmptyString() {
        @SuppressWarnings("unused")
        class TestRequest extends RequestWithPublicMethods {
            public String field = "hello";
        }
        
        assertTrue(new TestRequest().validateMandatoryFields().isEmpty());
    }
}
