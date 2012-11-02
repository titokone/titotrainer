package fi.helsinki.cs.titotrainer.framework.request;

import static org.junit.Assert.*;

import org.junit.Test;

import fi.helsinki.cs.titotrainer.testsupport.framework.request.RequestWithPublicMethods;

public class PositiveTest {
    
    @Test
    public void validatePositiveAnnotatedFieldsMethodShouldConsiderPositiveValuesValid() {
        @SuppressWarnings("unused")
        class TestRequest extends RequestWithPublicMethods {
            @Positive
            public Integer field = 1;
        }
        
        assertTrue(new TestRequest().validatePositiveAnnotatedFields().isEmpty());
    }

    @Test
    public void validatePositiveAnnotatedFieldsMethodShouldConsiderNegativeValuesInvalid() {
        @SuppressWarnings("unused")
        class TestRequest extends RequestWithPublicMethods {
            @Positive
            public byte field = -3;
        }
        
        assertEquals(Request.INVALIDITY_MSG_NOT_POSITIVE,
            new TestRequest().validatePositiveAnnotatedFields().iterator().next().getMsgKey());
    }
    
    @Test
    public void validatePositiveAnnotatedFieldsMethodShouldConsiderZeroValuesInvalid() {
        @SuppressWarnings("unused")
        class TestRequest extends RequestWithPublicMethods {
            @Positive
            public Integer field = 0;
        }
        
        assertEquals(Request.INVALIDITY_MSG_NOT_POSITIVE,
            new TestRequest().validatePositiveAnnotatedFields().iterator().next().getMsgKey());
    }

    @Test
    public void validatePositiveAnnotatedFieldsMethodShouldIgnoreNulls() {
        @SuppressWarnings("unused")
        class TestRequest extends RequestWithPublicMethods {
            @Positive
            public Long field = null;
        }
        
        assertTrue(new TestRequest().validatePositiveAnnotatedFields().isEmpty());
    }
    
    @Test
    public void validatePositiveAnnotatedFieldsMethodShouldIgnoreUnannotatedFields() {
        @SuppressWarnings("unused")
        class TestRequest extends RequestWithPublicMethods {
            public long field = -7;
        }
        
        assertTrue(new TestRequest().validatePositiveAnnotatedFields().isEmpty());
    }
    
}
