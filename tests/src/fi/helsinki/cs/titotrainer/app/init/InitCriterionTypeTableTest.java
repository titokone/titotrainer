package fi.helsinki.cs.titotrainer.app.init;

import static org.junit.Assert.*;

import java.util.Collection;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import fi.helsinki.cs.titotrainer.app.i18n.TitoTranslation;
import fi.helsinki.cs.titotrainer.app.model.CriterionType;
import fi.helsinki.cs.titotrainer.framework.config.Config;
import fi.helsinki.cs.titotrainer.framework.config.InvalidConfigException;
import fi.helsinki.cs.titotrainer.testsupport.TitoTestCase;

public class InitCriterionTypeTableTest extends TitoTestCase {

    private Session hs;
    private TitoTranslation tt;
    
    @Before
    public void setUp() throws InvalidConfigException {
        this.hs = this.openAutoclosedSession();
        
        Config configMock = Mockito.mock(Config.class);
        Mockito.stub(configMock.get("default_locale")).toReturn("fi");
        Mockito.stub(configMock.get("supported_locales")).toReturn("fi, en");
        this.tt = new TitoTranslation(configMock);
    }
    
    private void runInit() throws Exception {
        new InitCriterionTypeTable().run(hs, tt);
    }
    
    @SuppressWarnings("unchecked")
    private Collection<CriterionType> getAllRecords() {
        return hs.createQuery("FROM CriterionType").list();
    }
    
    @Test
    public void shouldCreateCriterionTypeRecordsForEachCriterionClassIfMissing() throws Exception {
        assertTrue(getAllRecords().isEmpty());
        
        runInit();
        
        assertFalse(getAllRecords().isEmpty());
        
        int correctNumRecords = getAllRecords().size();
        
        {
            // Check that a missing record is recreated
            hs.delete(getAllRecords().iterator().next());
            hs.flush();
            
            assertEquals(correctNumRecords - 1, getAllRecords().size());
            
            runInit();
            
            assertEquals(correctNumRecords, getAllRecords().size());
        }
        
        {
            // Check that a removed criterion's record is deleted
            CriterionType dummy = new CriterionType();
            dummy.setClassName("foo");
            hs.save(dummy);
            hs.flush();
            
            assertEquals(correctNumRecords + 1, getAllRecords().size());
            
            runInit();
            
            assertEquals(correctNumRecords, getAllRecords().size());
        }
    }
    
}
