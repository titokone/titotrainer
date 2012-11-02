package fi.helsinki.cs.titotrainer.framework.view.template.velocity;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.ResourceNotFoundException;

import fi.helsinki.cs.titotrainer.framework.view.template.TemplateEngine;

/**
 * <p>A bridge to a {@link VelocityEngine}.</p>
 */
public class VelocityTemplateEngine implements TemplateEngine {
    
    protected VelocityEngine engine;
    protected EventCartridge eventCartrige;
    
    /**
     * Constructor
     * 
     * @param engine A VelocityEngine object that has been <code>init()</code>-ed.
     */
    public VelocityTemplateEngine(VelocityEngine engine) {
        this.engine = engine;
    }
    
    /**
     * Sets the event cartrige to be attached to every
     * new {@link VelocityTemplateRenderer}'s context.
     */
    public void setEventCartrige(EventCartridge ec) {
        this.eventCartrige = ec;
    }
    
    @Override
    public VelocityTemplateRenderer createRenderer(String templateName) throws IOException {
        try {
            Template template = this.engine.getTemplate(templateName);
            Context context = new VelocityContext();
            if (this.eventCartrige != null) {
                this.eventCartrige.attachToContext(context);
            }
            return new VelocityTemplateRenderer(template, context);
        } catch (ResourceNotFoundException e) {
            throw new FileNotFoundException("Velocity template " + templateName + " not found: " + e.getMessage());
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}
