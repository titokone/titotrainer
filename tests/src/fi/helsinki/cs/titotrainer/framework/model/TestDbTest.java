package fi.helsinki.cs.titotrainer.framework.model;

import static org.junit.Assert.*;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Test;

import fi.helsinki.cs.titotrainer.testsupport.framework.model.TestDb;
import fi.helsinki.cs.titotrainer.testsupport.framework.model.TestDbEntity;

/**
 * This test verifies that the test db class {@link TestDb}
 * used by other tests works as expected.
 */
public class TestDbTest {
    
    @Test
    public void shouldCreateIndependentDatabases() {
        SessionFactory factory1 = TestDb.createSessionFactory();
        SessionFactory factory2 = TestDb.createSessionFactory();
        
        Session session1 = factory1.openSession();
        Session session2 = factory2.openSession();
        
        assertNotSame(session1, session2);
        
        TestDbEntity ent = new TestDbEntity("foo", "bar");
        session1.save(ent);
        session1.flush();
        ent = (TestDbEntity)session1.get(TestDbEntity.class, "foo");
        assertNotNull(ent);
        assertEquals("foo", ent.getKey());
        assertEquals("bar", ent.getValue());
        
        session1.close();
        
        // Session 2 should use a different DB and thus not see "foo".
        ent = (TestDbEntity)session2.get(TestDbEntity.class, "foo");
        assertNull(ent);
    }
}
