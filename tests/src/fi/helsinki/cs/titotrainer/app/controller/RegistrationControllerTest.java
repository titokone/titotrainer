package fi.helsinki.cs.titotrainer.app.controller;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import fi.helsinki.cs.titotrainer.app.i18n.TitoTranslation;
import fi.helsinki.cs.titotrainer.app.model.Course;
import fi.helsinki.cs.titotrainer.app.model.User;
import fi.helsinki.cs.titotrainer.app.request.RegistrationRequest;
import fi.helsinki.cs.titotrainer.framework.model.handler.ModelAccessChecker;
import fi.helsinki.cs.titotrainer.framework.request.RequestInvalidity;
import fi.helsinki.cs.titotrainer.testsupport.app.controller.ControllerTestCase;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleCourses;

public class RegistrationControllerTest extends ControllerTestCase<RegistrationRequest, RegistrationController> {
    
    private Session hs;
    private Course targetCourse;
    
    @Before
    public void setUp() {
        this.hs = this.openAutoclosedSession();
        this.targetCourse = new SampleCourses(this.hs).emptyCourse;
    }
    
    private void fillInValidRequest(RegistrationRequest req) {
        req.username = "newuser";
        req.firstName = "New";
        req.lastName = "User";
        req.email = "newuser@example.com";
        
        req.studentNumber = "12345";
        req.prefLocale = Locale.ENGLISH.toString();
        req.courseId = targetCourse.getId();
        
        req.passwd = "testpass";
        req.passwd2 = "testpass";
    }
    
    private void assertControllerSucceeds(RegistrationRequest req) throws Exception {
        assertThat(controller.handle(req), hasProperty("path", equalTo("/login")));
    }
    
    private void assertControllerFails(RegistrationRequest req) throws Exception {
        assertThat(controller.handle(req), hasProperty("path", equalTo("/registration")));
    }
    
    private User getCreatedUser(String username) {
        return (User)this.hs.createQuery("FROM User WHERE username = ?")
                            .setString(0, username)
                            .uniqueResult();
    }
    
    private void assertUserMatchesRequest(User user, RegistrationRequest req) {
        TitoTranslation tt = req.getContext().getTitoTranslation();
        
        assertEquals(req.username, user.getUsername());
        assertEquals(req.firstName, user.getFirstName());
        assertEquals(req.lastName, user.getLastName());
        assertEquals(req.email, user.getEmail());
        
        assertEquals(req.studentNumber, user.getStudentNumber());
        assertEquals(tt.isSupportedLocale(new Locale(req.prefLocale)), user.getPrefLocale() != null);
        if (user.getPrefLocale() != null)
            assertEquals(new Locale(req.prefLocale), user.getPrefLocale());
        assertEquals(req.courseId, user.getCourseId());
        
        assertEquals(User.hashPassword(req.passwd), user.getPasswordSha1());
    }
    
    private void assertUserCreatedFromRequest(RegistrationRequest req) {
        User user = getCreatedUser(req.username);
        assertNotNull(user);
        assertUserMatchesRequest(user, req);
    }
    
    @Test
    public void shouldCreateNewUserAccount() throws Exception {
        fillInValidRequest(request);
        assertControllerSucceeds(request);
        assertUserCreatedFromRequest(request);
    }
    
    @Test
    public void shouldNotRequireValidPreferredLocale() throws Exception {
        fillInValidRequest(request);
        request.prefLocale = "not a valid locale";
        assertControllerSucceeds(request);
        assertUserCreatedFromRequest(request);
    }
    
    @Test
    public void shouldFailOnPasswordMismatch() throws Exception {
        fillInValidRequest(request);
        request.passwd2 = "oops";
        assertControllerFails(request);
    }
    
    @Test
    public void shouldFailOnHiddenCourse() throws Exception {
        ModelAccessChecker.disableForSession(hs);
        
        fillInValidRequest(request);
        targetCourse.setHidden(true);
        hs.update(targetCourse);
        hs.flush();
        
        assertControllerFails(request);
    }
    
    @Test
    public void shouldReturnUnsavedCopyOnFailure() throws Exception {
        fillInValidRequest(request);
        
        Collection<RequestInvalidity> invs = Collections.emptyList();
        assertThat(controller.handleInvalid(request, hs, invs), hasProperty("path", equalTo("/registration")));

        Mockito.verify(request.getUserSession()).setAttribute(Mockito.eq("regUser"), Mockito.argThat(new TypeSafeMatcher<User>() {
            @Override
            public boolean matchesSafely(User item) {
                assertUserMatchesRequest(item, request);
                return true;
            }
            @Override
            public void describeTo(Description description) {
            }
        }));
    }
    
    @Override
    protected Class<RegistrationController> getControllerType() {
        return RegistrationController.class;
    }
    
}
