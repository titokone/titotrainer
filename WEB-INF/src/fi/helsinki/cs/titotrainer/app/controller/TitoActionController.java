package fi.helsinki.cs.titotrainer.app.controller;

import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;

import fi.helsinki.cs.titotrainer.app.i18n.TitoTranslation;
import fi.helsinki.cs.titotrainer.app.model.TString;
import fi.helsinki.cs.titotrainer.app.request.TitoRequest;
import fi.helsinki.cs.titotrainer.app.session.Messenger;
import fi.helsinki.cs.titotrainer.framework.controller.AbstractController;
import fi.helsinki.cs.titotrainer.framework.i18n.Translator;

/**
 * A convenience base class for all TitoTrainer controllers
 * that provide functionality.
 */
public abstract class TitoActionController<RequestType extends TitoRequest> extends AbstractController<RequestType> {
    
    protected Logger logger;
    
    protected TitoActionController() {
        this.logger = Logger.getLogger(this.getClass());
    }
    
    /**
     * Returns the translator for this controller class.
     */
    public Translator getTranslator(TitoRequest req) {
        return getTranslator(req, this.getClass());
    }
    
    /**
     * Returns the translator for the given class.
     */
    public Translator getTranslator(TitoRequest req, Class<?> cls) {
        return req.getContext().getTitoTranslation().getClassTranslator(req, cls);
    }
    
    /**
     * Appends a message to the request's {@link Messenger}.
     * 
     * @param req The request.
     * @param category The message category.
     * @param message The message to add.
     */
    public void appendMessage(RequestType req, String category, String message) {
        req.getUserSession().getMessenger().appendMessage(category, message);
    }
    
    /**
     * <p>A convenience method for converting a mapping of a locale
     * code to a value to a TString.</p>
     * 
     * <p>If the map has values that don't correspond to any supported
     * locale then they are ignored.</p>
     *
     * @param data The map.
     * @param tt
     * @param emptyToNull Whether to treat empty and whitespace-only strings as nulls.
     * @return A TString, which may be empty but never null.
     */
    protected static TString mapToTString(Map<String, String> data, TitoTranslation tt, boolean emptyToNull) {
        TString result = new TString();
        
        for (String key : data.keySet()) {
            try {
                String value = data.get(key);
                if (emptyToNull) {
                    if (value != null && value.trim().isEmpty())
                        value = null;
                }
                
                Locale locale = TitoTranslation.parseLocale(key);
                if (tt.isSupportedLocale(locale))
                    result.set(locale, value);
                
            } catch (IllegalArgumentException e) { // Invalid locale
                continue;
            }
        }
        
        return result;
    }
    
}
