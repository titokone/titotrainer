package fi.helsinki.cs.titotrainer.app.model.misc;

import java.util.Comparator;
import java.util.Locale;

import fi.helsinki.cs.titotrainer.app.model.TString;

/**
 * Compares TStrings by a given locale's translation.
 */
public class TStringComparator implements Comparator<TString> {
    protected Locale locale;
    protected boolean ignoreCase;
    
    public TStringComparator(Locale locale) {
        this(locale, false);
    }
    
    public TStringComparator(Locale locale, boolean ignoreCase) {
        this.locale = locale;
        this.ignoreCase = ignoreCase;
    }
    
    public Locale getLocale() {
        return locale;
    }
    
    public int compare(TString o1, TString o2) {
        String s1 = ArgumentUtils.emptyOnNull(o1.get(locale));
        String s2 = ArgumentUtils.emptyOnNull(o2.get(locale));
        if (ignoreCase)
            return s1.compareToIgnoreCase(s2);
        else
            return s1.compareTo(s2);
    }
}
