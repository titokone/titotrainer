package fi.helsinki.cs.titotrainer.framework.view.template.velocity;

import java.io.IOException;
import java.io.Writer;

import org.apache.velocity.Template;
import org.apache.velocity.context.Context;

import fi.helsinki.cs.titotrainer.framework.view.template.AbstractTemplateRenderer;

/**
 * A class to assign variables to JSP templates and then render them.
 */
public class VelocityTemplateRenderer extends AbstractTemplateRenderer {
    protected Template template;
    protected Context context;
    
    public VelocityTemplateRenderer(Template template, Context context) {
        if (template == null)
            throw new NullPointerException("template may not be null");
        if (context == null)
            throw new NullPointerException("context may not be null");
        this.template = template;
        this.context = context;
    }
    
    public void put(String name, Object value) {
        if (name == null)
            throw new NullPointerException();
        this.context.put(name, value);
    }
    
    public Object get(String name) {
        if (name == null)
            throw new NullPointerException();
        return this.context.get(name);
    }
    
    public void render(Writer output) throws IOException {
        this.template.merge(this.context, output);
    }
}
