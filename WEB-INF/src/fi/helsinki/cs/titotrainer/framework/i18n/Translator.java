package fi.helsinki.cs.titotrainer.framework.i18n;

import java.util.Formatter;
import java.util.Locale;

/**
 * <p>An interface to fetching translations for static strings.</p>
 */
public abstract class Translator {

    /**
     * Creates the formatter used by {@link #trp(String, Object...)}.
     */
    protected Formatter createFormatter() {
        return new Formatter();
    }
    
    /**
     * <p>Returns the translation of a simple string.</p>
     * 
     * @param key The translation key. Not null.
     * @return The translated string, or something defined
     *         by a subclass if no translation was found.
     * @throws NullPointerException If key is null.
     */
    public abstract String tr(String key);
    
    /**
     * <p>Translates a string and fills in placeholders.</p>
     * 
     * <p>By default this is equivalent to calling {@link #tr(String)}
     * and calling {@link Formatter#format(String, Object...)}
     * on the result.</p>
     * 
     * @param key The translation key.
     * @param args The placeholder arguments.
     * @return The translation, with placeholders replaced with their arguments.
     */
    public String trp(String key, Object ... args) {
        return this.createFormatter().format(this.tr(key), args).toString();
    }
    
    /**
     * Returns the locale to which the translator is translating.
     * Should return {@link Locale#ROOT} (instead of null) when not applicable.
     * 
     * @return The locale that this translator translates to.
     */
    public abstract Locale getLocale();
    
}
