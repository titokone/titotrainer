package fi.helsinki.cs.titotrainer.app.view.template.velocity.event;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.app.event.EventCartridge;
import org.junit.Before;
import org.junit.Test;

import fi.helsinki.cs.titotrainer.app.view.template.NoEscapeWrapper;
import fi.helsinki.cs.titotrainer.framework.view.template.velocity.VelocityTemplateEngine;
import fi.helsinki.cs.titotrainer.framework.view.template.velocity.VelocityTemplateRenderer;
import fi.helsinki.cs.titotrainer.testsupport.framework.view.template.TestViewTemplates;

public class OverridableEscapeHtmlReferenceTest {
    
    private VelocityTemplateEngine vte;
    private VelocityTemplateRenderer echoRenderer;
    
    @Before
    public void setUp() throws Exception {
        VelocityEngine ve = TestViewTemplates.createVelocityEngine();
        this.vte = new VelocityTemplateEngine(ve);
        
        EventCartridge ec = new EventCartridge();
        ec.addReferenceInsertionEventHandler(new OverridableEscapeHtmlReference());
        this.vte.setEventCartrige(ec);
        
        this.echoRenderer = this.vte.createRenderer(TestViewTemplates.getEchoTemplateName());
    }
    
    @Test
    public void shouldEscapeHtmlReferences() throws IOException {
        this.echoRenderer.put("echo", "<foo>");
        
        StringWriter sw = new StringWriter();
        this.echoRenderer.render(sw);
        assertEquals("&lt;foo&gt;", sw.toString());
    }
    
    @Test
    public void shouldNotEscapeMarkedHtmlReferences() throws IOException {
        this.echoRenderer.put("echo", new NoEscapeWrapper("<foo>"));
        
        StringWriter sw = new StringWriter();
        this.echoRenderer.render(sw);
        assertEquals("<foo>", sw.toString());
    }
    
    @Test
    public void noEscapeWrapperObjectsToStringMethodShouldReturnObjectsToStringValue() {
        assertEquals("<foo>", new NoEscapeWrapper("<foo>").toString());
    }
    
}
