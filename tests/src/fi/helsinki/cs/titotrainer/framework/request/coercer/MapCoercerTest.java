package fi.helsinki.cs.titotrainer.framework.request.coercer;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import fi.helsinki.cs.titotrainer.framework.misc.Some;

public class MapCoercerTest {
    
    private MapCoercer coercer;
    private Map<String, Object> params;
    
    @Before
    public void setUp() {
        this.coercer = new MapCoercer();
        this.params = new HashMap<String, Object>();
    }
    
    @Test
    public void resultTypeShouldBeMap() {
        assertSame(Map.class, this.coercer.getResultType());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void shouldReturnEmptyMapIfNoParametersMatch() {
        this.params.put("foo", "asd");
        this.params.put("bar", "bsd");
        
        Some<Map> result = this.coercer.coerce("foo", this.params, new Annotation[0]);
        assertTrue(result.value.isEmpty());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void shouldReturnMatchedParameters() {
        this.params.put("msg[en]", "Hello");
        this.params.put("msg[fi]", new Object() {
            public String toString() { return "Terve"; }
        });
        
        Some<Map> result = this.coercer.coerce("msg", this.params, new Annotation[0]);
        assertEquals("Hello", result.value.get("en"));
        assertEquals("Terve", result.value.get("fi"));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void shouldMatchNestedMaps() {
        this.params.put("msg[0][en]", "Hello");
        this.params.put("msg[0][fi]", new Object() {
            public String toString() { return "Terve"; };
        });
        this.params.put("msg[x]", "asd");
        this.params.put("msg2[y]", "asd");
        
        Some<Map> result = this.coercer.coerce("msg", this.params, new Annotation[0]);
        assertThat(result.value.get("0"), instanceOf(Map.class));
        
        assertEquals("Hello", ((Map)result.value.get("0")).get("en"));
        assertEquals("Terve", ((Map)result.value.get("0")).get("fi"));
        
        assertNotNull(result.value.get("x"));
        assertNull(result.value.get("y"));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void shouldUseSubcoercersDefinedInMapTypeAnnotation() {
        this.params.put("param[0][1]", "2");
        
        MapTypes annotation = new MapTypes() {

            @Override
            public Class<? extends SimpleFieldCoercer<?>> keyCoercer() {
                return IntegerCoercer.class;
            }

            @Override
            public Class<? extends SimpleFieldCoercer<?>> valueCoercer() {
                return IntegerCoercer.class;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return MapTypes.class;
            }
            
        };
        
        Some<Map> result = this.coercer.coerce("param", this.params, new Annotation[] { annotation });
        assertThat(result.value.get(0), instanceOf(Map.class));
        assertNull(result.value.get("0"));
        
        assertEquals(2, ((Map)result.value.get(0)).get(1));
        assertNull(((Map)result.value.get(0)).get("1"));
        
    }
    
}
