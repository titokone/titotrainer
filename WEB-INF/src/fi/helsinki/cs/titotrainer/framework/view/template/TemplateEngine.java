package fi.helsinki.cs.titotrainer.framework.view.template;

import java.io.IOException;

/**
 * <p>Represents a template engine. Acts as a factory for
 * {@link TemplateRenderer} objects. Thread-safe.</p>
 */
public interface TemplateEngine {
    public TemplateRenderer createRenderer(String templateName) throws IOException;
}
