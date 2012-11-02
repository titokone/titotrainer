package fi.helsinki.cs.titotrainer.framework.view.template.velocity;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.velocity.app.VelocityEngine;
import org.junit.Before;
import org.junit.Test;

import fi.helsinki.cs.titotrainer.testsupport.framework.view.template.TestViewTemplates;

public class VelocityTemplateEngineTest {
    
    private VelocityEngine velocityEngine;
    private VelocityTemplateEngine velocityTemplateEngine;
    
    @Before
    public void setUp() throws Exception {
        this.velocityEngine = TestViewTemplates.createVelocityEngine();
        this.velocityTemplateEngine = new VelocityTemplateEngine(this.velocityEngine);
    }
    
    @Test
    public void shouldCreateVelocityTemplateRenderers() throws IOException {
        assertNotNull(this.velocityTemplateEngine.createRenderer(TestViewTemplates.getHelloTemplateName()));
    }
    
    @Test(expected = FileNotFoundException.class)
    public void shouldThrowFileNotFoundIfFileNotFound() throws IOException {
        this.velocityTemplateEngine.createRenderer("someNonexistentTemplate");
    }
    
    //TODO: should test setEventCartrige
    
}
