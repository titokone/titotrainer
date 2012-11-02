package fi.helsinki.cs.titotrainer.framework.request;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.Collection;

import org.junit.Test;
import org.mockito.Mockito;

import fi.helsinki.cs.titotrainer.framework.misc.MutableRef;

public class RequestTest {
    
    @SuppressWarnings("unused")
    private static class GenericTestRequest extends Request {
        public Integer normalField;
        @Optional
        public Integer optionalField;
        @Manual
        public Integer manualField;
        
        Integer packageField;
        protected Integer protectedField;
        private Integer privateField;
    }
    
    private static class ReflectFieldTestRequest extends GenericTestRequest {
        @Manual
        public int reflectFieldCalls = 0;
        
        @Override
        public Field reflectField(String field) throws IllegalArgumentException {
            ++reflectFieldCalls;
            return super.reflectField(field);
        }
    }
    
    // Not really a "requirement" but it's convenient to have this verified
    @Test
    public void copyMethodShouldCallCopyToMethod() throws Exception {
        final MutableRef<Boolean> copyCalled = new MutableRef<Boolean>(false);
        Request req = new Request() {
            @Override
            public <T extends Request> T copy(Class<T> cls) throws Exception {
                copyCalled.value = true;
                return super.copy(cls);
            }
        };
        
        req.copy(DefaultRequest.class);
        
        assertEquals(true, copyCalled.value);
    }
    
    @Test
    public void copyToMethodShouldCopyAttribsReference() throws Exception {
        RequestAttribs attribs = Mockito.mock(RequestAttribs.class);
        Request req = new DefaultRequest();
        req.setAttribs(attribs);
        Request dest = new DefaultRequest();
        req.copyTo(dest);
        assertSame(attribs, dest.getAttribs());
    }
    
    @Test
    public void copyToMethodShouldCopyFields() throws Exception {
        GenericTestRequest req = new GenericTestRequest();
        req.normalField = 123;
        GenericTestRequest dest = new GenericTestRequest();
        req.copyTo(dest);
        assertEquals(new Integer(123), dest.normalField);
    }
    
    @Test
    public void copyToMethodShouldCopyExtraParameters() throws Exception {
        DefaultRequest req = new DefaultRequest();
        req.setExtraParameter("hello", "boss");
        DefaultRequest dest = new DefaultRequest();
        req.copyTo(dest);
        assertEquals("boss", dest.getExtraParameter("hello"));
    }
    
    @Test
    public void copyToMethodShouldSetMissingFieldsAsExtraParameters() throws Exception {
        GenericTestRequest req = new GenericTestRequest();
        req.normalField = 123;
        Request dest = new DefaultRequest();
        req.copyTo(dest);
        assertEquals(123, dest.getExtraParameter("normalField"));
    }
    
    @Test
    public void getFieldsShouldReturnAllPublicNonManualFields() {
        Collection<String> fields = new GenericTestRequest().getFields();
        assertEquals(2, fields.size());
        assertTrue(fields.contains("normalField"));
        assertTrue(fields.contains("optionalField"));
    }
    
    @Test
    public void getFieldValueMethodShouldCallReflectFieldMethod() {
        ReflectFieldTestRequest req = new ReflectFieldTestRequest();
        req.getFieldValue("normalField");
        assertEquals(1, req.reflectFieldCalls);
    }
    
    @Test
    public void getFieldTypeMethodShouldCallReflectFieldMethod() {
        ReflectFieldTestRequest req = new ReflectFieldTestRequest();
        req.getFieldType("normalField");
        assertEquals(1, req.reflectFieldCalls);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void extraParameterMayNotHaveSameNameAsAField() {
        GenericTestRequest req = new GenericTestRequest();
        req.setExtraParameter("normalField", "foo");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void extraParameterGetterMethodArgumentMayNotBeTheSameAsAFieldName() {
        GenericTestRequest req = new GenericTestRequest();
        req.getExtraParameter("normalField");
    }
    
    @Test
    public void extraParameterMayHaveSameNameAsAManualOrNonPublic() {
        GenericTestRequest req = new GenericTestRequest();
        req.setExtraParameter("manualField", "foo1");
        req.setExtraParameter("packageField", "foo2");
        req.setExtraParameter("protectedField", "foo3");
        req.setExtraParameter("privateField", "foo4");
        
        assertEquals("foo1", req.getExtraParameter("manualField"));
        assertEquals("foo2", req.getExtraParameters().get("packageField"));
    }
    
    @Test
    public void getAllParametersMethodShouldReturnFieldsAndExtraParameters() {
        GenericTestRequest req = new GenericTestRequest();
        req.normalField = 13;
        req.setExtraParameter("extra", "cookies!");
        assertEquals(new Integer(13), req.getAllParameters().get("normalField"));
        assertEquals("cookies!", req.getAllParameters().get("extra"));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void reflectFieldMethodShouldThrowOnManualField() {
        GenericTestRequest req = new GenericTestRequest();
        req.reflectField("manualField");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void reflectFieldMethodShouldThrowOnNonPublicField() {
        GenericTestRequest req = new GenericTestRequest();
        req.reflectField("protectedField");
    }
    
    @Test
    public void hasFieldMethodShouldReturnTrueOnRegularFields() {
        GenericTestRequest req = new GenericTestRequest();
        assertTrue(req.hasField("normalField"));
        assertTrue(req.hasField("optionalField"));
    }
    
    @Test
    public void hasFieldMethodShouldReturnFalseOnManualField() {
        GenericTestRequest req = new GenericTestRequest();
        assertFalse(req.hasField("manualField"));
    }
    
    @Test
    public void hasFieldMethodReturnFalseOnNonPublicField() {
        GenericTestRequest req = new GenericTestRequest();
        assertFalse(req.hasField("packageField"));
        assertFalse(req.hasField("protectedField"));
        assertFalse(req.hasField("privateField"));
    }
    
    @Test
    public void hasFieldMethodShouldReturnFalseOnNonexistentField() {
        GenericTestRequest req = new GenericTestRequest();
        assertFalse(req.hasField("nonexistentField"));
    }
    
}
