package fi.helsinki.cs.titotrainer.app.admin.controller;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import fi.helsinki.cs.titotrainer.app.access.TitoBaseRole;
import fi.helsinki.cs.titotrainer.app.admin.access.AdminAccess;
import fi.helsinki.cs.titotrainer.app.session.TitoUserSession;
import fi.helsinki.cs.titotrainer.framework.request.DefaultRequest;
import fi.helsinki.cs.titotrainer.framework.response.Response;
import fi.helsinki.cs.titotrainer.framework.response.ViewRenderingResponse;
import fi.helsinki.cs.titotrainer.testsupport.TitoTestCase;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleUsers;

public class AdminFrontControllerTest extends TitoTestCase {
    
    private AdminFrontController afc;
        
    @Before
    public void setUp() {
        afc = new AdminFrontController();
    }
    
    @Test
    public void shouldUseAdminAccessDefinitions() {
        assertSame(AdminAccess.getInstance(), afc.getAccessController());
    }
    
    @Test
    public void shouldAllowInstructorsToCourseListPage() throws Exception {
        DefaultRequest req = createRequest(DefaultRequest.class, "/admin/courselist");
        
        TitoUserSession us = new TitoUserSession();
        
        Session session = req.getAttribs().getHibernateSession();
        
        SampleUsers users = new SampleUsers(session);
        
        session.save(users.assistant);
        session.flush();
        
        us.setAuthenticatedUser(users.assistant);
        Mockito.stub(req.getAttribs().getUserSession()).toReturn(us);
        
        assert(req.getUserSession().getRole() == TitoBaseRole.ASSISTANT);
        
        Response resp = afc.handle(req);
        assertThat(resp, instanceOf(ViewRenderingResponse.class));
    }
}
