package fi.helsinki.cs.titotrainer.testsupport.framework.view.template;

import org.apache.velocity.app.VelocityEngine;

public class TestViewTemplates {
    /**
     * Creates a VelocityEngine with the test configuration loaded.
     * 
     * @return A configured (<code>init()</code>-ed) VelocityEngine.
     * @throws Exception If something goes wrong (it shouldn't).
     */
    public static VelocityEngine createVelocityEngine() throws Exception {
        VelocityEngine ve = new VelocityEngine();
        ve.init("tests/conf/velocity.properties");
        return ve;
    }
    
    /**
     * Returns the name of the "hello" test template, as it should be
     * passed to <code>VelocityEngine.getTemplate()</code>.
     * 
     * @return A template name.
     */
    public static String getHelloTemplateName() {
        return "hello.vm";
    }
    
    /**
     * Returns the name of the "echo" test template, as it should be
     * passed to <code>VelocityEngine.getTemplate()</code>. This template
     * contains nothing more than one variable - <code>$echo</code>.
     * 
     * @return A template name.
     */
    public static String getEchoTemplateName() {
        return "echo.vm";
    }
    
    /**
     * Returns the expected contents of the "hello" test template assuming
     * no escaping is done.
     * 
     * @param who The value of the "who" variable.
     * @return The expected contents of the template.
     */
    public static String getExpectedHelloTemplateContents(String who) {
        return "Hello " + who + "!";
    }
}
