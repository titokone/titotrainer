package fi.helsinki.cs.titotrainer.app.controller;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;

import fi.helsinki.cs.titotrainer.app.model.User;
import fi.helsinki.cs.titotrainer.app.request.ResetPasswordRequest;
import fi.helsinki.cs.titotrainer.app.session.Messenger;
import fi.helsinki.cs.titotrainer.testsupport.app.controller.ControllerTestCase;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleUsers;

public class ResetPasswordControllerTest extends ControllerTestCase<ResetPasswordRequest, ResetPasswordController> {

    private Session hs;
    private User sampleUser;
    
    @Before
    public void setUp() {
        this.hs = this.openAutoclosedSession();
        this.sampleUser = new SampleUsers(hs).pullman;
    }
    
    @Test
    public void shouldSetResetPasswordSha1() throws Exception {
        request.username = sampleUser.getUsername();
        String before = sampleUser.getResetPasswordSha1();
        
        controller.handle(request);
        
        hs.refresh(sampleUser);
        String after = sampleUser.getResetPasswordSha1();
        
        assertNotNull(after);
        assertThat(after, not(equalTo(before)));
        assertThat(after.length(), equalTo(40));
    }
    
    // Would like to test sending of e-mail, but way too much to mock for too little gain
    
    @Test
    public void shouldRedirectToLoginWithSuccess() throws Exception {
        request.username = sampleUser.getUsername();
        assertThat(controller.handle(request), hasProperty("path", equalTo("/login")));
        assertEquals(1, sessionMock.getMessenger().getMessages(Messenger.GLOBAL_SUCCESS_CATEGORY).size());
    }
    
    @Test
    public void givenNonexistentUsernameShouldRedirectToSelfWithFailure() throws Exception {
        request.username = "something-incorrect";
        assertThat(controller.handle(request), hasProperty("path", equalTo("/resetpassword")));
        assertEquals(1, sessionMock.getMessenger().getMessages(Messenger.GLOBAL_ERROR_CATEGORY).size());
    }
    
    @Override
    protected Class<ResetPasswordController> getControllerType() {
        return ResetPasswordController.class;
    }
}
