package fi.helsinki.cs.titotrainer.app.i18n;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import fi.helsinki.cs.titotrainer.app.request.TitoRequest;
import fi.helsinki.cs.titotrainer.framework.config.Config;
import fi.helsinki.cs.titotrainer.framework.config.InvalidConfigException;
import fi.helsinki.cs.titotrainer.framework.i18n.EchoTranslator;
import fi.helsinki.cs.titotrainer.framework.i18n.Translator;
import fi.helsinki.cs.titotrainer.testsupport.TitoTestCase;
import fi.helsinki.cs.titotrainer.testsupport.app.i18n.LocalizedTestObject;

public class TitoTranslationTest extends TitoTestCase {
    
    private Locale expectedDefaultLocale;
    private Locale[] expectedSupportedLocales;
    
    private Config configMock;
    
    @Before
    public void setUp() {
        this.expectedDefaultLocale = new Locale("fi");
        this.expectedSupportedLocales = new Locale[] {Locale.ENGLISH, new Locale("fi")};
        
        this.configMock = Mockito.mock(Config.class);
        Mockito.stub(configMock.get("default_locale")).toReturn(expectedDefaultLocale.toString());
        Mockito.stub(configMock.get("supported_locales")).toReturn(StringUtils.join(expectedSupportedLocales, ", "));
    }
    
    @Test
    public void shouldGetDefaultLocaleFromConfig() throws InvalidConfigException {
        TitoTranslation tt = new TitoTranslation(configMock);
        assertEquals(expectedDefaultLocale, tt.getDefaultLocale());
    }
    
    @Test
    public void shouldGetSupportedLocalesFromConfig() throws InvalidConfigException {
        TitoTranslation tt = new TitoTranslation(configMock);
        assertArrayEquals(expectedSupportedLocales, tt.getSupportedLocales().toArray());
        for (Locale supportedLocale : expectedSupportedLocales) {
            assertTrue(tt.isSupportedLocale(supportedLocale));
        }
    }
    
    @Test
    public void parseLocaleMethodShouldBeAbleToParseLocalesWithOneToThreeParts() throws InvalidConfigException {
        assertEquals(new Locale("es"), TitoTranslation.parseLocale("es"));
        assertEquals(new Locale("es", "ES"), TitoTranslation.parseLocale("es_ES"));
        assertEquals(new Locale("es", "ES", "Traditional"), TitoTranslation.parseLocale("es_ES_Traditional"));
        assertEquals(new Locale("es", "ES", "Traditional_WIN"), TitoTranslation.parseLocale("es_ES_Traditional_WIN"));
    }
    
    @Test(expected = InvalidConfigException.class)
    public void shouldThrowIfNoDefaultLocaleDefined() throws InvalidConfigException {
        Mockito.stub(configMock.get("default_locale")).toReturn(null);
        new TitoTranslation(configMock);
    }
    
    @Test(expected = InvalidConfigException.class)
    public void shouldThrowIfNoSupportedLocalesDefined() throws InvalidConfigException {
        Mockito.stub(configMock.get("supported_locales")).toReturn(null);
        new TitoTranslation(configMock);
    }
    
    @Test(expected = InvalidConfigException.class)
    public void shouldThrowIfDefaultLocaleIsNotInSupportedLocales() throws InvalidConfigException {
        Mockito.stub(configMock.get("default_locale")).toReturn(Locale.KOREAN.toString());
        assumeThat(Locale.KOREAN, not(isIn(expectedSupportedLocales)));
        new TitoTranslation(configMock);
    }
    
    @Test
    public void getClassTranslatorMethodShouldReturnTranslationsFromClassResourceBundles() throws InvalidConfigException {
        TitoTranslation tt = new TitoTranslation(configMock);
        TitoRequest req = this.createRequest(TitoRequest.class, "");
        
        Translator translator = tt.getClassTranslator(req, LocalizedTestObject.class);
        assertEquals(expectedDefaultLocale, translator.getLocale());
        assertEquals("Avain", translator.tr("key"));
    }
    
    @Test
    public void getClassTranslatorMethodShouldReturnEchoTranslatorIfNoResourceBundleIsFound() throws InvalidConfigException {
        Mockito.stub(configMock.get("default_locale")).toReturn(Locale.ENGLISH.toString());
        
        TitoTranslation tt = new TitoTranslation(configMock);
        TitoRequest req = this.createRequest(TitoRequest.class, "");
        Mockito.stub(req.getContext().getTitoTranslation()).toReturn(tt);
        
        Translator translator = tt.getClassTranslator(req, LocalizedTestObject.class);
        assertThat(translator, instanceOf(EchoTranslator.class));
        assertEquals(Locale.ENGLISH, translator.getLocale());
    }
    
}
