package fi.helsinki.cs.titotrainer.app;

import org.apache.log4j.Logger;

import fi.helsinki.cs.titotrainer.app.i18n.TitoTranslation;
import fi.helsinki.cs.titotrainer.app.model.titokone.TitokoneFacade;
import fi.helsinki.cs.titotrainer.framework.RequestContext;
import fi.helsinki.cs.titotrainer.framework.config.ConfigLoader;
import fi.helsinki.cs.titotrainer.framework.view.template.TemplateEngine;

/**
 * <p>A TitoTrainer-specific request context subclass.</p>
 * 
 * <p>This adds a {@link TitoTranslation} instance to the
 * standard request context.</p>
 * 
 * <p>It also adds a {@link TitokoneFacade} instance.</p>
 */
public class TitoRequestContext extends RequestContext {

    protected TitoTranslation titoTranslation;
    protected TitokoneFacade titokoneFacade;
    
    public TitoRequestContext(ConfigLoader configLoader, TemplateEngine templateEngine, Logger logger, TitoTranslation titoTranslation, TitokoneFacade titokoneFacade) {
        super(configLoader, templateEngine, logger);
        this.titoTranslation = titoTranslation;
        this.titokoneFacade = titokoneFacade;
    }
    
    public TitoTranslation getTitoTranslation() {
        return titoTranslation;
    }
    
    public TitokoneFacade getTitokoneFacade() {
        return titokoneFacade;
    }
    
}
