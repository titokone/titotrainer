package fi.helsinki.cs.titotrainer.app.controller;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import fi.helsinki.cs.titotrainer.app.i18n.TitoTranslation;
import fi.helsinki.cs.titotrainer.app.model.TString;
import fi.helsinki.cs.titotrainer.framework.config.Config;
import fi.helsinki.cs.titotrainer.framework.config.InvalidConfigException;
import fi.helsinki.cs.titotrainer.framework.config.PropertyConfig;
import fi.helsinki.cs.titotrainer.testsupport.app.controller.ControllerTestCase;
import fi.helsinki.cs.titotrainer.testsupport.app.controller.TestTitoActionController;
import fi.helsinki.cs.titotrainer.testsupport.app.controller.TestTitoRequest;

public class TitoActionControllerTest extends ControllerTestCase<TestTitoRequest, TestTitoActionController> {
    
    private TitoTranslation tt;
    
    @Before
    public void setUp() throws InvalidConfigException {
        Properties ttProps = new Properties();
        ttProps.put("default_locale", "en");
        ttProps.put("supported_locales", "en, fi");
        
        Config ttConfig = new PropertyConfig(ttProps);
        this.tt = new TitoTranslation(ttConfig);
    }
    
    @Test
    public void mapToTStringMethodShouldConvertKeysToLocales() {
        Map<String, String> testMap = new HashMap<String, String>();
        
        testMap.put("en", "fun");
        testMap.put("fi", "kivaa");
        testMap.put("de", "spass");
        testMap.put("asd__", "mökki");
        
        TString result = TitoActionController.mapToTString(testMap, tt, false);
        assertNotNull(result.get(Locale.ENGLISH));
        assertNotNull(result.get(new Locale("fi")));
        assertNull(result.get(new Locale("de")));
    }
    
    @Test
    public void mapToTStringMethodShouldBeAbleToConvertEmptyStringsToNull() {
        Map<String, String> testMap = new HashMap<String, String>();
        
        testMap.put("en", "");
        testMap.put("fi", "   ");
        
        TString result = TitoActionController.mapToTString(testMap, tt, true);
        assertNull(result.get(Locale.ENGLISH));
        assertNull(result.get(new Locale("fi")));
    }

    @Override
    protected Class<TestTitoActionController> getControllerType() {
        return TestTitoActionController.class;
    }
    
}
