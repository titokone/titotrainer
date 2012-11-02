package fi.helsinki.cs.titotrainer.framework.i18n;

import static org.junit.Assert.*;

import java.util.Locale;

import org.junit.Test;

public class EchoTranslatorTest {
    
    @Test
    public void shouldReturnTranslationKey() {
        EchoTranslator tr = new EchoTranslator(Locale.ROOT);
        assertEquals("foo", tr.tr("foo"));
    }
    
    @Test
    public void shouldReturnFakeLocale() {
        assertEquals(Locale.ENGLISH, new EchoTranslator(Locale.ENGLISH).getLocale());
    }
    
}
