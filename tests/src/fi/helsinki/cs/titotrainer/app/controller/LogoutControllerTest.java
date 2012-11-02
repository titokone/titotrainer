package fi.helsinki.cs.titotrainer.app.controller;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.mockito.Mockito;

import fi.helsinki.cs.titotrainer.app.request.TitoRequest;
import fi.helsinki.cs.titotrainer.framework.response.Response;
import fi.helsinki.cs.titotrainer.testsupport.app.controller.ControllerTestCase;

public class LogoutControllerTest extends ControllerTestCase<TitoRequest, LogoutController> {
    
    @Override
    protected Class<LogoutController> getControllerType() {
        return LogoutController.class;
    }
    
    @Test
    public void shouldClearAuthenticatedUserFromSession() throws Exception {
        controller.handle(request);
        
        Mockito.verify(sessionMock).setAuthenticatedUser(null);
    }
    
    @Test
    public void shouldRedirectToLoginPage() throws Exception {
        Response resp = controller.handle(request);
        
        assertThat(resp, instanceOf(Response.class));
        assertThat(resp, hasProperty("path", endsWith("/login")));
    }
    
}
