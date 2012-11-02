package fi.helsinki.cs.titotrainer.framework.view.template;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.Writer;

import org.junit.Test;
import org.mockito.Mockito;

public class NullTemplateRendererTest {
    
    @Test
    public void shouldStoreVariables() {
        TemplateRenderer tr = new NullTemplateRenderer();
        tr.put("foo", "bar");
        assertEquals("bar", tr.get("foo"));
    }
    
    @Test
    public void shouldNotRenderAnything() throws IOException {
        TemplateRenderer tr = new NullTemplateRenderer();
        tr.put("foo", "bar");
        
        Writer writer = Mockito.mock(Writer.class);
        tr.render(writer);
        Mockito.verifyZeroInteractions(writer);
    }
    
}
