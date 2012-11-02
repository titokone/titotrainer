package fi.helsinki.cs.titotrainer.app.admin.controller;

import static org.junit.Assert.*;

import java.util.Set;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import fi.helsinki.cs.titotrainer.app.access.TitoBaseRole;
import fi.helsinki.cs.titotrainer.app.admin.request.UpdateUserRequest;
import fi.helsinki.cs.titotrainer.app.model.User;
import fi.helsinki.cs.titotrainer.app.model.access.CurrentCredentials;
import fi.helsinki.cs.titotrainer.app.session.Messenger;
import fi.helsinki.cs.titotrainer.app.session.TitoUserSession;
import fi.helsinki.cs.titotrainer.framework.response.ErrorResponse;
import fi.helsinki.cs.titotrainer.framework.response.Response;
import fi.helsinki.cs.titotrainer.testsupport.app.controller.ControllerTestCase;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleUsers;

public class UpdateUserControllerTest extends ControllerTestCase<UpdateUserRequest, UpdateUserController> {
    
    @Override
    protected Class<UpdateUserController> getControllerType() {
        return UpdateUserController.class;
    }
    
    private Session hs;
    private SampleUsers sampleUsers;
    
    @Before
    public void setUp() {
        this.hs = this.openAutoclosedSession();
        this.sampleUsers = new SampleUsers(this.hs);
    }
    
    @Override
    protected TitoBaseRole getModelAccessCheckerRole() {
        return TitoBaseRole.ADMINISTRATOR;
    }
    
    @Override
    protected UpdateUserRequest createRequest(User authUser) {
        UpdateUserRequest req = super.createRequest(authUser);
        Mockito.stub(req.getUserSession()).toReturn(new TitoUserSession());
        req.getUserSession().setAuthenticatedUser(authUser);
        CurrentCredentials.setCurrentUser(authUser);
        return req;
    }
    
    private Response handleNoError(UpdateUserRequest req) {
        try {
            Response resp = controller.handle(req);
            if (req.getUserSession().getMessenger().getMessages(Messenger.GLOBAL_SUCCESS_CATEGORY).isEmpty())
                fail("Unsuccessful update - invalid fields: " + req.getUserSession().getAttribute("invalidFields"));
            if (resp instanceof ErrorResponse)
                fail(resp.toString());
            return resp;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    @Test
    public void shouldUpdateUserProperties() throws Exception {
        UpdateUserRequest req = this.createRequest(sampleUsers.admin);
        
        User user = sampleUsers.nykanen;
        
        req.id = user.getId();
        req.role = "@STUDENT";
        req.username = user.getUsername();
        req.firstName = " foo";
        
        handleNoError(req);
        
        hs.refresh(sampleUsers.nykanen);
        
        assertEquals(req.firstName.trim(), user.getFirstName());
    }
    
    @Test
    public void shouldNullifyOptionalStringParameters() throws Exception {
        UpdateUserRequest req = this.createRequest(sampleUsers.admin);
        
        req.id = sampleUsers.nykanen.getId();
        req.role = "@STUDENT";
        req.username = sampleUsers.nykanen.getUsername();
        req.firstName = "   ";
        req.lastName = "  ";
        req.email = "";
        req.studentNumber = " ";
        req.passwd = "    ";
        
        controller.handle(req);
        
        assertNull(req.firstName);
        assertNull(req.lastName);
        assertNull(req.email);
        assertNull(req.studentNumber);
        assertNull(req.passwd);
    }
    
    @Test
    public void shouldSetPasswordOnlyIfTheSetPasswordRequestParameterIsSet() throws Exception {
        UpdateUserRequest req = this.createRequest(sampleUsers.admin);
        
        User user = sampleUsers.nykanen;
        req.id = user.getId();
        req.role = "@STUDENT";
        req.username = user.getUsername();
        
        req.passwd = "blah";
        
        String oldHash = User.hashPassword(SampleUsers.NYKANEN_PASSWORD);
        String newHash = User.hashPassword(req.passwd);
        
        req.setPassword = false;
        handleNoError(req);
        hs.refresh(user);
        
        assertEquals(oldHash, user.getPasswordSha1());
        
        req.setPassword = true;
        handleNoError(req);
        hs.refresh(user);
        
        assertEquals(newHash, user.getPasswordSha1());
    }
    
    @Test
    public void shouldDisallowEmptyUsername() throws Exception {
        UpdateUserRequest req = this.createRequest(sampleUsers.admin);
        
        User user = sampleUsers.nykanen;
        req.id = user.getId();
        req.role = "@STUDENT";
        req.username = "   ";
        
        controller.handle(req);
        
        Set<?> invalidFields = (Set<?>)req.getUserSession().getAttribute("invalidFields");
        assertNotNull(invalidFields);
        assertTrue(invalidFields.contains("username"));
    }
    
}
