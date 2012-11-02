package fi.helsinki.cs.titotrainer.app.session;

import static org.junit.Assert.*;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import fi.helsinki.cs.titotrainer.app.access.TitoBaseRole;
import fi.helsinki.cs.titotrainer.app.model.User;
import fi.helsinki.cs.titotrainer.app.model.titokone.TitokoneState;

public class TitoUserSessionTest {
    
    private TitoUserSession session;
    private TitokoneState state;
    
    @Before
    public void setUp() {
        this.session = new TitoUserSession();
        this.state = new TitokoneState();
    }
    
    @Test
    public void shouldReturnNullIfUserIsNotAuthenticated() {
        assertEquals(null, this.session.getUserLocale());
        
    }
    
    @Test
    public void shouldReturnDefaultRoleIfUserIsNotAuthenticated() {
        assertEquals(TitoBaseRole.GUEST, this.session.getRole());
    }
    
    @Test
    public void shouldReturnAuthenticatedUser() {
        this.session.setAuthenticatedUser(new User("Testuser", TitoBaseRole.STUDENT));
        assertEquals(TitoBaseRole.STUDENT, this.session.getRole());
    }
    
    @Test
    public void shouldReturnPreferredLocale() {
        User user = new User();
        user.setPrefLocale(Locale.FRANCE);
        this.session.setAuthenticatedUser(user);
        
        assertEquals(Locale.FRANCE, session.getUserLocale());
    }
    
    @Test
    public void shouldBeAbleToGetAttribute() {

        this.session.setAttribute("testvalue", this.state);
        
        assertEquals(this.state, session.getAttribute("testvalue"));
        
        TitokoneState anotherState = new TitokoneState();
        anotherState.setGeneralRegister(1, 1);        
        assertEquals(this.state, session.getAttribute("testvalue", anotherState));
        assertEquals(anotherState, session.getAttribute("qwerty", anotherState));
    }
    
    public void attributeShouldNotBeConsumedIfTypeDoesNotMatch() {
        this.session.setAttribute("test", "foo");
        
        this.session.consumeAttribute("test", Integer.class);
        assertEquals("foo", this.session.consumeAttribute("test", String.class));
        assertNull(this.session.consumeAttribute("test", String.class));
        assertNull(this.session.consumeAttribute("test"));
    }
    
    @Test
    public void shouldBeAbleToConsumeAttribute() {
        this.session.setAttribute("testvalue", this.state);
        
        assertEquals(this.state, session.consumeAttribute("testvalue"));
        assertEquals(null, session.consumeAttribute("testvalue"));
        
        this.session.setAttribute("testvalue2", this.state);
        TitokoneState anotherState = new TitokoneState();
        anotherState.setGeneralRegister(1, 1);  
        assertEquals(anotherState, session.consumeAttribute("qwerty", anotherState));
        assertEquals(this.state, session.consumeAttribute("testvalue2", anotherState));
    }
    
    @Test
    public void shouldContainMessenger() {
        assertNotNull(this.session.getMessenger());
    }
    
    @Test
    public void hopMethodShouldForwardHopToMessenger() {
        this.session = new TitoUserSession() {
            @Override
            protected Messenger getDefaultMessenger() {
                return Mockito.mock(Messenger.class);
            }
        };
        
        Messenger messengerMock = this.session.getMessenger();
        
        this.session.hop();
        Mockito.verify(messengerMock, Mockito.times(1)).hop();
    }
    
    
    
}