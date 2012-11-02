package fi.helsinki.cs.titotrainer.framework.request;

import static org.junit.Assert.*;

import java.util.Collection;

import org.junit.Test;


public class ManualTest {
    private static class TestRequest extends Request {
        public Integer normalField;
        @Manual
        public Integer manualField;
    }
    
    @Test
    public void getFieldsMethodShouldNotReturnManualFields() {
        Collection<String> fields = new TestRequest().getFields();
        assertTrue(fields.contains("normalField"));
        assertFalse(fields.contains("manualField"));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void getFieldValueMethodShouldThrowOnManualField() {
        new TestRequest().getFieldValue("manualField");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void getFieldTypeMethodShouldThrowOnManualField() {
        new TestRequest().getFieldType("manualField");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void isFieldOptionalMethodShouldThrowOnManualField() {
        new TestRequest().isFieldOptional("manualField");
    }
}
