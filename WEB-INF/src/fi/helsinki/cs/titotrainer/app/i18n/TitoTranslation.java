package fi.helsinki.cs.titotrainer.app.i18n;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.log4j.Logger;

import fi.helsinki.cs.titotrainer.app.request.TitoRequest;
import fi.helsinki.cs.titotrainer.framework.config.Config;
import fi.helsinki.cs.titotrainer.framework.config.InvalidConfigException;
import fi.helsinki.cs.titotrainer.framework.i18n.EchoTranslator;
import fi.helsinki.cs.titotrainer.framework.i18n.ResourceBundleTranslator;
import fi.helsinki.cs.titotrainer.framework.i18n.Translator;

/**
 * <p>Contains information and loader methods related to locales and
 * translation in TitoTrainer.</p>
 * 
 * @see Translator
 */
public class TitoTranslation {
    
    private static final ResourceBundle.Control translationLoaderControl =
        ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES);
    
    private final Locale defaultLocale;
    private final Set<Locale> supportedLocales;
    
    private Map<Class<?>, Map<Locale, Translator>> translatorCache; // must be accessed in a synchronized way
    
    /**
     * <p>Constructs the translation information based on a configuration.</p>
     * 
     * <p>
     * The configuration parameters are:
     * <ul>
     *   <li><b>default_locale</b> - the default locale</li>
     *   <li><b>supported_locales</b> - a comma-separated list of supported locales</li>
     * </ul>
     * </p>
     * 
     * @param config The configuration.
     */
    public TitoTranslation(Config config) throws InvalidConfigException {
        String defaultLocaleStr = config.get("default_locale");
        if (defaultLocaleStr == null)
            throw new InvalidConfigException("No default locale");
        
        try {
            this.defaultLocale = parseLocale(defaultLocaleStr.trim());
        } catch (IllegalArgumentException e) {
            throw new InvalidConfigException(e);
        }

        String supportedLocalesStr = config.get("supported_locales");
        if (supportedLocalesStr == null)
            throw new InvalidConfigException("No supported locales list");
        
        this.supportedLocales = new HashSet<Locale>();
        for (String locale : supportedLocalesStr.split(",")) {
            this.supportedLocales.add(parseLocale(locale.trim()));
        }
        
        if (!this.isSupportedLocale(this.getDefaultLocale()))
            throw new InvalidConfigException("Default locale not in list of supported locales");
    }
    
    /**
     * <p>Parses a string like <code>language_country_variant</code>
     * into a {@link Locale}.</p>
     * 
     * @param s A string.
     * @return A locale.
     * @throws IllegalArgumentException If the locale could not be parsed.
     */
    public static Locale parseLocale(String s) {
        String[] parts = s.split("_", 3);
        
        for (int i = 0; i < parts.length; ++i) {
            if (parts[i].isEmpty())
                throw new IllegalArgumentException("Empty locale name part");
        }
        
        Locale locale;
        if (parts.length == 1) {
            locale = new Locale(parts[0]);
        } else if (parts.length == 2) {
            locale = new Locale(parts[0], parts[1]);
        } else {
            assert(parts.length == 3);
            locale = new Locale(parts[0], parts[1], parts[2]);
        }
        
        return locale;
    }
    
    /**
     * Returns the default locale.
     * 
     * @return The default locale. Never null.
     */
    public Locale getDefaultLocale() {
        return defaultLocale;
    }
    
    /**
     * Returns the set of supported locales.
     * 
     * @return A non-null immutable set of non-null supported locales.
     */
    public Set<Locale> getSupportedLocales() {
        return Collections.unmodifiableSet(this.supportedLocales);
    }
    
    /**
     * <p>Tells whether a locale is a supported locale.</p>
     * 
     * <p>This method only returns true if there is an exact match in the set of supported locales.</p>
     * 
     * @param locale The locale to test.
     * @return Wether the locale is in the set returned by {@link #getSupportedLocales()}.
     */
    public boolean isSupportedLocale(Locale locale) {
        return this.supportedLocales.contains(locale);
    }
    
    
    /**
     * <p>Returns a translator for a given class for a locale.</p>
     * 
     * <p>TitoTrainer stores translations in view/controller-specific
     * resource bundles. This method loads them.</p>
     * 
     * @param locale The locale the translator will translate to.
     * @param cls The class whose translations to load.
     * @return The translator.
     */
    public synchronized Translator getClassTranslator(Locale locale, Class<?> cls) {
        
        if (translatorCache == null)
            translatorCache = new HashMap<Class<?>, Map<Locale, Translator>>();
        
        Map<Locale, Translator> localeMap = translatorCache.get(cls);
        if (localeMap == null) {
            localeMap = new HashMap<Locale, Translator>();
            translatorCache.put(cls, localeMap);
        }
        
        Translator tr = localeMap.get(locale);
        
        if (tr == null) {
            ResourceBundle rb = null;
            try {
                if (cls.getCanonicalName() != null)
                    rb = ResourceBundle.getBundle(cls.getCanonicalName(), locale, translationLoaderControl);
            } catch (MissingResourceException e) {
            }
            if (rb != null) {
                tr = new ResourceBundleTranslator(rb);
                Logger.getLogger(TitoTranslation.class).debug("Loaded " + locale + " translation for " + cls);
            } else {
                tr = new EchoTranslator(locale);
                Logger.getLogger(TitoTranslation.class).warn("No " + locale + " translation bundle found for " + cls);
            }
            
            localeMap.put(locale, tr);
        }
        
        return tr;
    }
    
    /**
     * <p>Returns a translator for a given class for the request's locale.</p>
     * 
     * <p>TitoTrainer stores translations in view/controller-specific
     * resource bundles. This method loads them.</p>
     * 
     * @param req The request from which to derive take the locale.
     * @param cls The class whose translations to load.
     * @return The translator.
     */
    public synchronized Translator getClassTranslator(TitoRequest req, Class<?> cls) {
        
        Locale locale = req.getUserSession().getUserLocale();
        if (locale == null)
            locale = req.getContext().getTitoTranslation().getDefaultLocale();
        assert(locale != null);
        
        return getClassTranslator(locale, cls);
    }
    
}
