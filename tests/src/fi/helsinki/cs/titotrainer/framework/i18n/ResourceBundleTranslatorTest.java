package fi.helsinki.cs.titotrainer.framework.i18n;

import static org.junit.Assert.*;

import java.util.ResourceBundle;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ResourceBundleTranslatorTest {

    // getString() and getObject() are final, so they can't be stubbed.
    // We make handleGetObject() public here so we can stub that.
    private static abstract class MockableResourceBundle extends ResourceBundle {
        @Override
        public abstract Object handleGetObject(String key);
    }
    
    private MockableResourceBundle bundleMock;
    private ResourceBundleTranslator translator;
    
    @Before
    public void setUp() {
        this.bundleMock = Mockito.mock(MockableResourceBundle.class);
        this.translator = new ResourceBundleTranslator(this.bundleMock);
    }
    
    @Test
    public void shouldFetchStringsFromResourceBundle() {
        Mockito.stub(bundleMock.handleGetObject("foo")).toReturn("bar");
        
        assertEquals("bar", translator.tr("foo"));
    }
    
    @Test
    public void shouldConcatenateContextToStringWithDot() {
        Mockito.stub(bundleMock.handleGetObject("foo")).toReturn("bar");
        assertEquals("bar", translator.tr("foo"));
    }
    
    @Test
    public void shouldReturnTranslationKeyIfNoTranslationFound() {
        String key = "something that's not there";
        
        assertEquals(key, this.translator.tr(key));
    }
    
    @Test(expected = NullPointerException.class)
    public void shouldNotBeConstructibleWithoutAResourceBundle() {
        new ResourceBundleTranslator(null);
    }
    
}
