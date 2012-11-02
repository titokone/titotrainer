package fi.helsinki.cs.titotrainer.app.student.controller;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;

import fi.helsinki.cs.titotrainer.app.access.TitoBaseRole;
import fi.helsinki.cs.titotrainer.app.student.access.StudentAccess;
import fi.helsinki.cs.titotrainer.framework.request.DefaultRequest;
import fi.helsinki.cs.titotrainer.framework.response.ErrorResponse;
import fi.helsinki.cs.titotrainer.framework.response.Response;
import fi.helsinki.cs.titotrainer.framework.response.ViewRenderingResponse;
import fi.helsinki.cs.titotrainer.testsupport.app.controller.ControllerTestCase;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleCourses;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleUsers;

public class StudentFrontControllerTest extends ControllerTestCase<DefaultRequest, StudentFrontController> {
    
    private StudentFrontController sfc;
    private SampleUsers users;
        
    @Before
    public void setUp() {
        this.sfc = new StudentFrontController();
        this.users = new SampleUsers(this.openAutoclosedSession());
    }
    
    @Test
    public void shouldUseStudentAccessDefinitions() {
        assertSame(StudentAccess.getInstance(), sfc.getAccessController());
    }
    
    @Test
    public void shouldAllowStudentsToTaskListPage() throws Exception {
        DefaultRequest req = createRequest(users.nykanen, "/student/tasklist");
        
        Session setupHs = this.openAutoclosedSession();
        disableModelAccessCheckerForSession(setupHs);
        users.nykanen.setCourseId(new SampleCourses(setupHs).autumnCourse.getId());
        assert(req.getUserSession().getRole() == TitoBaseRole.STUDENT);
        
        Response resp = sfc.handle(req);
        assertThat(resp, instanceOf(ViewRenderingResponse.class));
    }

    @Test
    public void shouldReturn404OnIncorrectPathPrefix() throws Exception {
        DefaultRequest req = createRequest(users.nykanen, "/wrong/tasklist");
        assert(req.getUserSession().getRole() == TitoBaseRole.STUDENT);
        
        Response resp = sfc.handle(req);
        assertThat(resp, instanceOf(ErrorResponse.class));
        assertThat(resp, hasProperty("statusCode", equalTo(404)));
    }
    
    @Override
    protected Class<StudentFrontController> getControllerType() {
        return StudentFrontController.class;
    }
}
