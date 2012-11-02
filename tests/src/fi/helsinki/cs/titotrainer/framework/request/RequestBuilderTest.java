package fi.helsinki.cs.titotrainer.framework.request;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;


public class RequestBuilderTest {

    private Map<String, Object> params;
    
    @Before
    public void setUp() {
        this.params = new HashMap<String, Object>();
    }
    
    @Test
    public void shouldCreateEmptyRequestObjectWithEmptyParameterMap() throws InstantiationException, IllegalAccessException {
        RequestBuilder.getDefaultInstance().build(new HashMap<String, Object>(), DefaultRequest.class);
    }
    
    public static class IntRequest extends Request {
        public int intField;
        public Integer integerField;
    }
    
    @Test
    public void shouldInstantiateIntegerParameter() throws InstantiationException, IllegalAccessException {
        params.put("intField", "123");
        params.put("integerField", "456");
        
        IntRequest req = RequestBuilder.getDefaultInstance().build(params, IntRequest.class);
        assertEquals(123, req.intField);
        assertEquals(new Integer(456), req.integerField);
        assertNull(req.getExtraParameters().get("intField"));
        assertNull(req.getExtraParameters().get("integerField"));
    }
    
    public static class StringRequest extends Request {
        public String stringField;
    }
    
    @Test
    public void shouldInstantiateStringParameter() throws InstantiationException, IllegalAccessException {
        params.put("stringField", "hello");
        
        StringRequest req = RequestBuilder.getDefaultInstance().build(params, StringRequest.class);
        assertEquals("hello", req.stringField);
    }
    
    public static class MapRequest extends Request {
        public Map<String, String> mapField;
        public Map<String, Map<String, String>> nestedMapField;
    }
    
    @Test
    public void shouldInstantiateMapParameter() throws InstantiationException, IllegalAccessException {
        params.put("mapField[foo]", "bar");
        params.put("nestedMapField[bar][baz]", "asd");
        
        MapRequest req = RequestBuilder.getDefaultInstance().build(params, MapRequest.class);
        assertNotNull(req.mapField);
        assertEquals("bar", req.mapField.get("foo"));
        assertEquals("asd", req.nestedMapField.get("bar").get("baz"));
    }
    
    public static class ArrayRequest extends Request {
        public Integer[] arrayField;
    }
    
    @Test
    public void shouldInstantiateArrayParameter() throws InstantiationException, IllegalAccessException {
        params.put("arrayField", "1, 2,3,4,-5");
        
        ArrayRequest req = RequestBuilder.getDefaultInstance().build(params, ArrayRequest.class);
        assertNotNull(req.arrayField);
        
        Integer[] expected = {1, 2, 3, 4, -5};
        assertArrayEquals(expected, req.arrayField);
    }
    
    @Test
    public void shouldIgnoreExtraParameters() throws InstantiationException, IllegalAccessException {
        params.put("nonexistentField", "hello");
        
        assertNotNull(RequestBuilder.getDefaultInstance().build(params, DefaultRequest.class));
    }
    
    public static class FunnyRequest extends Request {
        public FunnyRequest funnyField;
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfNoTypeCoercionDefinedForField() throws InstantiationException, IllegalAccessException {
        params.put("funnyField", "hello");
        
        RequestBuilder.getDefaultInstance().build(params, FunnyRequest.class);
    }
    
}
