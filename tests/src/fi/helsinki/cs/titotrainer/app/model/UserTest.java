package fi.helsinki.cs.titotrainer.app.model;

import static org.junit.Assert.*;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.validator.InvalidStateException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fi.helsinki.cs.titotrainer.app.access.TitoBaseRole;
import fi.helsinki.cs.titotrainer.testsupport.TitoTestCase;

public class UserTest extends TitoTestCase {
    
    private Session session;
    
    @Before
    public void setUp() {
        this.session = this.openSession();
        if (this.session == null) {
            fail("Hibernate Session couldn't be opened!");
        }
    }
    
    @After
    public void tearDown() {
        if (this.session.isOpen())
            this.session.close();
    }
    
    @Test(expected = ConstraintViolationException.class)
    public void usernameShouldBeUnique() {
        User u = new User("jussi", TitoBaseRole.STUDENT);
        try {
            session.save(u);
        } catch (InvalidStateException e) {
            fail("Unexpected " + e.getClass());
        }
        session.flush();
        session.close();
        
        session = this.openSession();
        u = new User("jussi", TitoBaseRole.STUDENT);
        session.save(u);
        session.flush();
    }
    
    @Test(expected = NullPointerException.class)
    public void usernameMayNotBeNull() {
        User u = new User();
        u.setUsername(null);
        session.save(u);
        session.flush();
    }
    
    @Test(expected = InvalidStateException.class)
    public void usernameMayNotBeEmpty() {
        User u = new User("", TitoBaseRole.STUDENT);
        session.save(u);
        session.flush();
    }
    
    @Test(expected = InvalidStateException.class)
    public void usernameMayNotBeTooShort() {
        assert(User.MIN_USERNAME_LENGTH > 1);
        User u = new User("a", TitoBaseRole.STUDENT);
        session.save(u);
        session.flush();
    }
    
    @Test(expected = InvalidStateException.class)
    public void usernameMayNotBeTooLong() {
        assert(User.MAX_USERNAME_LENGTH <= 1000);
        User u = new User(StringUtils.repeat("a", User.MAX_USERNAME_LENGTH + 1), TitoBaseRole.STUDENT);
        session.save(u);
        session.flush();
    }
    
    @Test
    public void shouldApplySha1ToPassword() {
        String hash = User.hashPassword("foo");
        String expectedHash = "ab3c19b474d6db35e8e243ebd7b3edfd140851ae";
        assertEquals(expectedHash, hash);
    }
    
    //TODO: test parentRole constraints
    
    //TODO: test more...
    
}
