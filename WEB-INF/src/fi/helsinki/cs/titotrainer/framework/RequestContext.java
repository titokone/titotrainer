package fi.helsinki.cs.titotrainer.framework;

import org.apache.log4j.Logger;

import fi.helsinki.cs.titotrainer.framework.config.ConfigLoader;
import fi.helsinki.cs.titotrainer.framework.request.Request;
import fi.helsinki.cs.titotrainer.framework.view.template.TemplateEngine;

/**
 * <p>Provides access to the system configuration and the model.</p>
 * 
 * <p>A reference to the {@link RequestContext} is passed along every {@link Request}.</p>
 * 
 * <p>Every method defined here shall be thread-safe.</p>
 */
public class RequestContext {
    
    protected ConfigLoader configLoader;
    protected TemplateEngine templateEngine;
    protected Logger logger;
    
    public RequestContext(ConfigLoader configLoader, TemplateEngine templateEngine, Logger logger) {
        if (configLoader == null)
            throw new NullPointerException("configLoader may not be null");
        if (templateEngine == null)
            throw new NullPointerException("templateEngine may not be null");
        if (logger == null)
            throw new NullPointerException("logger may not be null");
        this.configLoader = configLoader;
        this.templateEngine = templateEngine;
        this.logger = logger;
    }
    
    /**
     * Returns the configuration loader.
     * 
     * @return A {@link ConfigLoader}. Never null.
     */
    public ConfigLoader getConfigLoader() {
        return this.configLoader;
    }

    /**
     * Returns the default template engine of the application.
     * 
     * @return A {@link TemplateEngine}. Never null.
     */
    public TemplateEngine getDefaultTemplateEngine() {
        return this.templateEngine;
    }
    
    /**
     * Returns the common (thread-safe) logger.
     * 
     * @return A log4j logger. Never null.
     */
    public Logger getLogger() {
        return logger;
    }
    
}
