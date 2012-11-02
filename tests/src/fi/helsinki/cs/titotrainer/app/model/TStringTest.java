package fi.helsinki.cs.titotrainer.app.model;

import static org.junit.Assert.*;

import java.util.Locale;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.junit.Before;
import org.junit.Test;

import fi.helsinki.cs.titotrainer.testsupport.TitoTestCase;
import fi.helsinki.cs.titotrainer.testsupport.app.model.TStringTestObject;
import fi.helsinki.cs.titotrainer.testsupport.framework.model.TestDb;

public class TStringTest extends TitoTestCase {
    
    @Override
    protected SessionFactory getNewSessionFactory() {
        AnnotationConfiguration conf = TestDb.createHibernateConfig();
        conf.addAnnotatedClass(TStringTestObject.class);
        this.sessionFactory = TestDb.createSessionFactory(conf);
        return this.sessionFactory;
    }
    
    private Session session;
    
    @Before
    public void setUp() {
        this.session = this.openAutoclosedSession();
    }
    
    @Test
    public void shouldBeUsableInTwoProperties() {
        TStringTestObject obj = new TStringTestObject();
        
        TString title = new TString();
        title.set(Locale.US, "Hello!");
        title.set(new Locale("fi"), "Terve!");
        obj.setTitle(title);
        
        TString msg = new TString();
        msg.set(Locale.US, "How's it going?");
        msg.set(new Locale("fi"), "Miten menee?");
        obj.setMsg(msg);
        
        session.save(obj);
        session.flush();
    }
    
    @Test
    public void shouldBeUsableAsACascadedProperty() {
        TStringTestObject obj = new TStringTestObject();
        TString msg = new TString();
        msg.set(Locale.US, "Hello!");
        msg.set(new Locale("fi"), "Terve!");
        obj.setMsg(msg);
        session.save(obj);
        assertNotNull(obj.getId());
        assertNotNull(msg.getId()); // Save was cascaded
        session.flush();
        
        assertEquals(1, session.createQuery("FROM TString").list().size());
        session.delete(obj); // Should cascade delete
        session.flush();
        assertEquals(0, session.createQuery("FROM TString").list().size());
    }
    
    @Test
    public void shouldBeUsableWithoutAnyTranslations() {
        TStringTestObject obj = new TStringTestObject();
        TString msg = new TString();
        obj.setMsg(msg);
        session.save(obj);
        session.flush();
    }
    
    @Test
    public void shouldBeEmptyByDefault() {
        TString msg = new TString();
        assertNull(msg.getTranslations()); // I'm not sure we should require this
        
        // get and unset should work properly for an empty TString
        assertEquals(null, msg.get(Locale.US));
        msg.unset(Locale.US);
    }
    
    @Test
    public void settingNullTranslationShouldUnsetTranslation() {
        TString msg = new TString();
        msg.set(Locale.US, "hello");
        msg.set(Locale.GERMAN, "hallo");
        assertEquals("hello", msg.get(Locale.US));
        assertEquals("hallo", msg.get(Locale.GERMAN));
        msg.set(Locale.US, null);
        msg.unset(Locale.GERMAN);
        assertNull(msg.get(Locale.US));
        assertNull(msg.get(Locale.GERMAN));
    }
    
    @Test
    public void getByPreferenceMethodShouldReturnTranslationInOrderOfPreference() {
        TString msg = new TString();
        assertNull(msg.getByPreference(true));
        assertNull(msg.getByPreference(false));
        
        msg.set(Locale.US, "hello");
        assertEquals("hello", msg.getByPreference(false, Locale.GERMAN, Locale.CANADA, Locale.US));
        assertEquals("hello", msg.getByPreference(true, Locale.GERMAN, Locale.CANADA, Locale.US));
        
        assertEquals("hello", msg.getByPreference(true, Locale.GERMAN, Locale.CANADA));
        assertNull(msg.getByPreference(false, Locale.GERMAN, Locale.CANADA));
    }
    

    /* Test deepCopy() */
    
    @Test
    public void deepCopyShouldPreserveNullTranslationsCollection() {
        TString source = new TString();
        assertNull(source.getTranslations());
        assertNull(source.deepCopy().getTranslations());
    }
    
    @Test
    public void deepCopyShouldNotCopyTheTranslationsCollection() {
        TString source = new TString(ENGLISH, "Message");
        TString copy = source.deepCopy();
        assertNotSame(source.getTranslations(), copy.getTranslations());
        assertEquals(source.getTranslations(), copy.getTranslations());
    }
    
    @Test
    public void deepCopyShouldNotReturnTheSameObject() {
        TString source = new TString(ENGLISH, "Message");
        TString copy = source.deepCopy();
        assertNotSame(source, copy);
        this.session.save(source);
        this.session.save(copy);
        assertFalse(source.equals(copy));
        assertFalse(copy.equals(source));
    }
    
    @Test
    public void deepCopyShouldCopyAllTranslations() {
        TString source = new TString(ENGLISH, "Message");
        source.set(GERMAN, "Nachricht");
        source.set(FINNISH, "Viesti");
        TString copy = source.deepCopy();
        assertNotSame(source, copy);
        assertSame(source.get(ENGLISH), copy.get(ENGLISH));
        assertSame(source.get(GERMAN), copy.get(GERMAN));
        assertSame(source.get(FINNISH), copy.get(FINNISH));
    }
    
    @Test
    public void afterDeepCopyModificationsToCopyShouldNotAffectSource() {
        TString source = new TString(ENGLISH, "A Message");
        source.set(GERMAN, "Eine Nachricht");
        TString copy = source.deepCopy();
        copy.set(ENGLISH, "Message");
        copy.set(GERMAN, "Nachricht");
        assertFalse(copy.get(ENGLISH).equals(source.get(ENGLISH)));
        assertFalse(copy.get(GERMAN).equals(source.get(GERMAN)));
    }
    
    @Test
    public void afterDeepCopyingAddingOrRemovingTranslationsInOneObjectShouldNotAffectTheOther() {
        TString source = new TString(ENGLISH, "Message");
        source.set(GERMAN, "Eine Nachricht");
        TString copy = source.deepCopy();
        copy.set(FINNISH, "Viesti");
        assertNull(source.get(FINNISH));
        copy.set(ENGLISH, null);
        assertNotNull(source.get(ENGLISH));
        copy.set(ENGLISH, "A Message");
        source.set(ENGLISH, null);
        assertNotNull(copy.get(ENGLISH));
        copy.set(FINNISH, null);
        source.set(FINNISH, "Viesti");
        assertNull(copy.get(FINNISH));
    }
    
    /* Test hasCompleteTranslation */
    
    @Test(expected = NullPointerException.class)
    public void hasCompleteTranslationShouldNotAcceptNullLocale() {
        TString string = new TString();
        string.hasCompleteTranslation(null);
    }
    
    @Test
    public void hasCompleteTranslationShouldBehaveCorrectly() {
        TString string = new TString();
        assertFalse(string.hasCompleteTranslation(ENGLISH));
        string.set(ENGLISH, "Message");
        assertTrue(string.hasCompleteTranslation(ENGLISH));
        string.set(ENGLISH, null);
        assertFalse(string.hasCompleteTranslation(ENGLISH));
    }

}