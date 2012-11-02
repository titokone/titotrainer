package fi.helsinki.cs.titotrainer.app.model;

import static fi.helsinki.cs.titotrainer.app.model.misc.ArgumentUtils.*;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.MapKey;
import org.hibernate.annotations.Type;

/**
 * <p>Encapsulates a translated string stored in the database.</p>
 */
@Entity
@Table(name="tstring")
public class TString extends AbstractTitoEntity {
    
    private long id;
    private Map<Locale, String> translations;
    
    /**
     * Constructs a new TString with all fields unset. 
     */
    public TString() {
    }
    
    /**
     * Constructs a new TString with one localized message.
     * 
     * @param locale The locale for the message
     * @param translation The localized message
     */
    public TString(Locale locale, String translation) {
        this.translations = new HashMap<Locale, String>();
        this.translations.put(locale, translation);
    }
    
    @Id
    @GeneratedValue
    @Override
    public long getId() {
        return id;
    }
    
    @Override
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Returns the translation in the specified language.
     * 
     * @param locale The locale for which to query the translation.
     * @return The translated string, or null if the translation didn't exist.
     */
    public String get(Locale locale) {
        validateLocaleArgument(locale, "locale", LOCALE_CONSTRAINT_NOT_NULL);
        if (translations != null) {
            return translations.get(locale);
        }
        return null;
    }
    
    /**
     * Returns the translation in the language of the given name.
     * 
     * @param localeName The name of the locale for which to query the translation.
     * @return The translated string, or null if the translation didn't exist.
     */
    public String get(String localeName) {
        validateStringArgument(localeName, "localeName", STRING_CONSTRAINT_NOT_NULL);
        return this.get(new Locale(localeName));
    }
    
    /**
     * Returns the translation by trying locales in a given order.
     * 
     * @param acceptAny Whether to accept any locale if no preference matched.
     * @param pref The locales in order of preference.
     * @return The translation in the prefered locale, or in any locale if
     *         <code>acceptAny</code> is true, or null if not found.
     */
    public String getByPreference(boolean acceptAny, Locale ... pref) {
        for (Locale locale : pref) {
            String s = get(locale);
            if (s != null)
                return s;
        }
        if (acceptAny && translations != null && !translations.isEmpty()) {
            return translations.values().iterator().next();
        }
        return null;
    }
    
    /**
     * Sets a translation in a specific language.
     * 
     * @param locale The locale for which to set the translation.
     * @param translation The translation string to set, or null to unset.
     */
    public void set(Locale locale, String translation) {
        validateLocaleArgument(locale, "locale", LOCALE_CONSTRAINT_NOT_NULL);
        if (translation == null) {
            this.unset(locale);
            return;
        }
        if (this.translations == null) {
            this.translations = new HashMap<Locale, String>();
        }
        this.translations.put(locale, translation);
    }
    
    /**
     * Removes a translation in a specific language.
     * 
     * @param locale The locale whose translation to remove.
     */
    public void unset(Locale locale) {
        validateLocaleArgument(locale, "locale", LOCALE_CONSTRAINT_NOT_NULL);
        if (this.translations != null) {
            this.translations.remove(locale);
        }
        if ((this.translations != null) && (this.translations.size() == 0)) {
            this.translations = null;
        }
    }
    
    @MapKey(targetElement = Locale.class, columns = {
        @Column(name = "locale")
    })
    @CollectionOfElements(targetElement = String.class, fetch = FetchType.EAGER)
    @Column(name = "msg")
    @Type(type = "text")
    @Cascade( { CascadeType.ALL })
    public Map<Locale, String> getTranslations() {
        return translations;
    }
    
    public void setTranslations(Map<Locale, String> translations) {
        this.translations = translations;
    }
    
    //////////////////////
    // INSTANCE METHODS //
    //////////////////////
    
    /**
     * Copies this TString. The deep-copy semantics for a TString are the following:<br /><br />
     * 
     * <ul>
     *   <li>The id is not copied at all (it's later assigned by the persistence framework)</li>
     *   <li>All localized messages stored in this TString are copied one by one</li>
     * </ul>
     * 
     * @return A true copy of this TString
     */
    public TString deepCopy() {
        String message;
        TString copy = new TString();
        if (this.translations != null) {
            for (Locale locale : this.translations.keySet()) {
                message = this.translations.get(locale);
                if (message != null) {
                    copy.set(locale, message);
                }
            }
        }
        return copy;
    }
    
    /**
     * Determines if this TString contains a translation for a given locale.
     * 
     * <h4 class="implementation">Implementation Note</h4>
     * The result of this method is equivalent to {@code this.get(locale) != null}.
     * 
     * @param locale The locale for which to determine, if this TString contains a corresponding localization. Must not be {@code null}.
     * @return {@code true} - If this TString contains a translation for the given locale<br />
     *         {@code false} - Otherwise
     * @throws NullPointerException - If the argument {@code locale} is {@code null} 
     */
    @Override
    public boolean hasCompleteTranslation(Locale locale) {
        validateLocaleArgument(locale, "locale", LOCALE_CONSTRAINT_NOT_NULL);
        return (this.get(locale) != null);
    }
    
    /**
     * Returns a string with all translations for debugging.
     */
    public String toDebugString() {
        if (this.translations == null) {
            this.translations = new HashMap<Locale, String>();
        }
        return this.toString() + ": " + this.translations.toString();
    }

}