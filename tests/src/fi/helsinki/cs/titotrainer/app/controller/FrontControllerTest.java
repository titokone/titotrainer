package fi.helsinki.cs.titotrainer.app.controller;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import fi.helsinki.cs.titotrainer.app.model.User;
import fi.helsinki.cs.titotrainer.app.model.access.CurrentCredentials;
import fi.helsinki.cs.titotrainer.framework.request.DefaultRequest;
import fi.helsinki.cs.titotrainer.framework.response.RedirectResponse;
import fi.helsinki.cs.titotrainer.framework.response.Response;
import fi.helsinki.cs.titotrainer.testsupport.app.controller.ControllerTestCase;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleUsers;

public class FrontControllerTest extends ControllerTestCase<DefaultRequest, FrontController> {
    
    private SampleUsers sampleUsers;
    
    @Before
    public void setUp() {
        this.sampleUsers = new SampleUsers(this.openAutoclosedSession());
    }
    
    @Test
    public void shouldNotClearCurrentCredentialsAfterExecution() throws Exception {
        User user = sampleUsers.nykanen;
        CurrentCredentials.setCurrentUser(user);
        this.controller.handle(this.createRequest(user));
        assertEquals(user, CurrentCredentials.getCurrentUser());
    }
    
    @Test
    public void shouldDirectGuestsToLoginPage() throws Exception {
        Response resp = this.controller.handle(this.request);
        
        assertThat(resp, instanceOf(RedirectResponse.class));
        assertThat(resp, hasProperty("path", startsWith("/login")));
    }
    
    @Override
    protected Class<FrontController> getControllerType() {
        return FrontController.class;
    }
}
