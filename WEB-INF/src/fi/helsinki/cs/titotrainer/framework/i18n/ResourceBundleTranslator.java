package fi.helsinki.cs.titotrainer.framework.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * A translator that reads translations from a
 * {@link ResourceBundle}.
 */
public class ResourceBundleTranslator extends Translator {
    
    protected ResourceBundle bundle;
    protected Locale reportedLocale;
    
    public ResourceBundleTranslator(ResourceBundle bundle) {
        if (bundle == null)
            throw new NullPointerException("bundle may not be null");
        this.bundle = bundle;
        this.reportedLocale = bundle.getLocale();
    }
    
    /**
     * Constructor that makes {@link #getLocale()} report a specified {@link Locale}.
     * 
     * @param bundle The bundle to query for translations.
     * @param reportedLocale The locale that {@link #getLocale()} shall return. Not null.
     */
    public ResourceBundleTranslator(ResourceBundle bundle, Locale reportedLocale) {
        this(bundle);
        if (reportedLocale == null)
            throw new NullPointerException("reportedLocale may not be null");
        this.reportedLocale = reportedLocale;
    }
    
    @Override
    public String tr(String key) {
        try {
            return this.bundle.getString(this.getBundleKey(key));
        } catch (Exception e) {
            return handleNoTranslation(key, e);
        }
    }
    
    /**
     * <p>Transforms a translation key into the
     * resource bundle key to use.</p>
     *
     * <p>By default, the key is returned unchanged.</p>
     * 
     * @param trKey The translation key.
     * @return The resource bundle key.
     */
    protected String getBundleKey(String trKey) {
        return trKey;
    }
    
    /**
     * <p>Handles the case where the translation was not found or
     * could not be loaded for some reason.</p>
     * 
     * <p>By default this just returns the translation key.</p>
     * 
     * @param trKey The translation key.
     * @param resourceLoadException The exception that was thrown
     *                              by {@link ResourceBundle#getString(String)}.
     * @return An alternative translation value.
     */
    protected String handleNoTranslation(String trKey, Exception resourceLoadException) {
        return trKey;
    }
    
    @Override
    public Locale getLocale() {
        return bundle.getLocale();
    }
    
}
