package fi.helsinki.cs.titotrainer.framework.i18n;

import java.util.Locale;

/**
 * <p>A translator that always returns the string to be translated.</p>
 * 
 * <p>This is a useful default in cases where a real translator is
 * unavailable.</p>
 */
public class EchoTranslator extends Translator {

    protected Locale fakeLocale;
    
    /**
     * A constructor that takes the locale {@link #getLocale()} should return.
     * 
     * @param fakeLocale The locale that {@link #getLocale()} should return.
     */
    public EchoTranslator(Locale fakeLocale) {
        this.fakeLocale = fakeLocale;
    }
    
    /**
     * Returns the translation key.
     * 
     * @param key The translation key. This is returned.
     */
    @Override
    public String tr(String key) {
        return key;
    }
    
    /**
     * {@link EchoTranslator} returns {@link Locale#ROOT} here.
     */
    @Override
    public Locale getLocale() {
        return this.fakeLocale;
    }
    
}
