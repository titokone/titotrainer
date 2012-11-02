package fi.helsinki.cs.titotrainer.framework.view.template.velocity;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.junit.Before;
import org.junit.Test;

import fi.helsinki.cs.titotrainer.framework.view.template.TemplateRenderer;
import fi.helsinki.cs.titotrainer.testsupport.framework.view.template.TestViewTemplates;

public class VelocityTemplateRendererTest {
    
    private VelocityEngine engine;
    private Template helloTemplate;
    private Context context;
    
    @Before
    public void setUp() throws Exception {
        this.engine = TestViewTemplates.createVelocityEngine();
        this.helloTemplate = this.engine.getTemplate(TestViewTemplates.getHelloTemplateName());
        this.context = new VelocityContext();
    }
    
    @Test(expected = NullPointerException.class)
    public void constructorShouldNotAcceptNullTemplate() {
        new VelocityTemplateRenderer(null, this.context);
    }
    
    @Test(expected = NullPointerException.class)
    public void constructorShouldNotAcceptNullContext() {
        new VelocityTemplateRenderer(this.helloTemplate, null);
    }
    
    @Test(expected = NullPointerException.class)
    public void variableSetterShouldNotAcceptNullName() {
        TemplateRenderer tr = new VelocityTemplateRenderer(this.helloTemplate, this.context);
        tr.put(null, new Object());
    }
    
    @Test(expected = NullPointerException.class)
    public void variableGetterShouldNotAcceptNullName() {
        TemplateRenderer tr = new VelocityTemplateRenderer(this.helloTemplate, this.context);
        tr.get(null);
    }
    
    @Test
    public void variableGetterShouldReturnPreviouslySetVariables() {
        TemplateRenderer tr = new VelocityTemplateRenderer(this.helloTemplate, this.context);
        Object o = new Object();
        tr.put("test", o);
        assertEquals(o, tr.get("test"));
    }
    
    @Test
    public void shouldRenderVelocityTemplatesFromFilesUsingAssignedVariables() throws IOException {
        TemplateRenderer tr = new VelocityTemplateRenderer(this.helloTemplate, this.context);
        tr.put("who", "Boss");
        StringWriter sw = new StringWriter();
        tr.render(sw);
        assertEquals(TestViewTemplates.getExpectedHelloTemplateContents("Boss"), (sw.toString().trim()));
    }
}
