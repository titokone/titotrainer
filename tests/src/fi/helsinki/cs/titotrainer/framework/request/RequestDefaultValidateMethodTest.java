package fi.helsinki.cs.titotrainer.framework.request;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

public class RequestDefaultValidateMethodTest {
    
    private static class TestRequestWithCallCounters extends Request {
        @Manual
        public int validateMandatoryFieldsCalls = 0;
        @Manual
        public int validateRegexAnnotatedFieldsCalls = 0;
        @Manual
        public int validatePositiveAnnotatedFieldsCalls = 0;
        
        @Override
        public Collection<RequestInvalidity> validateMandatoryFields() {
            ++this.validateMandatoryFieldsCalls;
            return new LinkedList<RequestInvalidity>();
        }
        
        @Override
        protected Collection<RequestInvalidity> validateRegexAnnotatedFields() {
            ++this.validateRegexAnnotatedFieldsCalls;
            return new LinkedList<RequestInvalidity>();
        }
        
        @Override
        protected Collection<RequestInvalidity> validatePositiveAnnotatedFields() {
            ++this.validatePositiveAnnotatedFieldsCalls;
            return new LinkedList<RequestInvalidity>();
        }
    }
    
    private TestRequestWithCallCounters req;
    
    @Before
    public void setUp() {
        this.req = new TestRequestWithCallCounters();
    }
    
    @Test
    public void validateMethodShouldCallValidateMandatoryFieldsMethod() {
        assertTrue(req.validate().isEmpty());
        assertEquals(1, req.validateMandatoryFieldsCalls);
    }
    
    @Test
    public void validateMethodShouldCallValidateRegexAnnotatedFieldsMethod() {
        assertTrue(req.validate().isEmpty());
        assertEquals(1, req.validateRegexAnnotatedFieldsCalls);
    }
    
    @Test
    public void validateMehtodShouldCAllValidatePositiveAnnotatedFieldsMethod() {
        assertTrue(req.validate().isEmpty());
        assertEquals(1, req.validatePositiveAnnotatedFieldsCalls);
    }
    
}
