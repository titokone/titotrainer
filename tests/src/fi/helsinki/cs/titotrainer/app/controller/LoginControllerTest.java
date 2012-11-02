package fi.helsinki.cs.titotrainer.app.controller;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import fi.helsinki.cs.titotrainer.app.model.Course;
import fi.helsinki.cs.titotrainer.app.model.User;
import fi.helsinki.cs.titotrainer.app.request.LoginRequest;
import fi.helsinki.cs.titotrainer.app.session.Messenger;
import fi.helsinki.cs.titotrainer.framework.model.handler.ModelAccessChecker;
import fi.helsinki.cs.titotrainer.framework.response.RedirectResponse;
import fi.helsinki.cs.titotrainer.framework.response.Response;
import fi.helsinki.cs.titotrainer.testsupport.app.controller.ControllerTestCase;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleCourses;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleUsers;

public class LoginControllerTest extends ControllerTestCase<LoginRequest, LoginController> {

    private Session hs;
    
    private SampleCourses courses;
    private SampleUsers users;
    
    @Before
    public void setUp() {
        this.hs = this.openAutoclosedSession();
        this.courses = new SampleCourses(this.hs);
        this.users = new SampleUsers(this.hs);
    }
    
    @Override
    protected Class<LoginController> getControllerType() {
        return LoginController.class;
    }
    
    private void verifyLoginFailed(Response resp) {
        assertThat(resp, instanceOf(RedirectResponse.class));
        assertThat(resp, hasProperty("path", endsWith("/login")));
        
        Mockito.verify(sessionMock, Mockito.never()).setAuthenticatedUser((User)Mockito.anyObject());
        
        assertEquals(1, sessionMock.getMessenger().getMessages(Messenger.GLOBAL_ERROR_CATEGORY).size());
    }
    
    private void verifyLoginSucceededFor(User user) {
        Mockito.verify(sessionMock).setAuthenticatedUser(
            (User)Mockito.argThat(hasProperty("id", equalTo(user.getId())))
        );
    }
    
    @Test
    public void shouldRedirectAdminToAdminModuleAfterSuccessfulLogin() throws Exception {
        request.username = SampleUsers.EDITOR_USERNAME;
        request.password = SampleUsers.EDITOR_PASSWORD;
        
        Response resp = controller.handle(request);
        assertThat(resp, instanceOf(RedirectResponse.class));
        assertThat(resp, hasProperty("path", endsWith("/admin")));
        
        verifyLoginSucceededFor(users.editor);
    }
    
    @Test
    public void shouldRedirectStudentToStudentModuleAfterSuccessfulLogin() throws Exception {
        disableModelAccessCheckerForSession(this.hs);
        users.nykanen.setCourseId(courses.emptyCourse.getId());
        hs.update(users.nykanen);
        hs.flush();
        enableModelAccessCheckerForSession(this.hs);
        
        request.username = SampleUsers.NYKANEN_USERNAME;
        request.password = SampleUsers.NYKANEN_PASSWORD;
        
        Response resp = controller.handle(request);
        assertThat(resp, instanceOf(RedirectResponse.class));
        assertThat(resp, hasProperty("path", endsWith("/student")));
        
        verifyLoginSucceededFor(users.nykanen);
    }
    
    @Test
    public void shouldRedirectBackToLoginPageWithErrorMessageAfterUnsuccessfulLogin() throws Exception {
        request.username = "invalid";
        request.password = "invalid";
        
        Response resp = controller.handle(request);
        verifyLoginFailed(resp);
    }
    
    @Test
    public void shouldNotAcceptLoginIfStudentHasNoAssociatedCourse() throws Exception {
        User user = users.nykanen;
        assert(user.getCourseId() == null);
        
        request.username = SampleUsers.NYKANEN_USERNAME;
        request.password = SampleUsers.NYKANEN_PASSWORD;
        
        verifyLoginFailed(controller.handle(request));
        
    }
    
    @Test
    public void shouldNotAcceptLoginIfStudentsCourseHasBeenDeleted() throws Exception {
        ModelAccessChecker.disableForSession(hs);
        
        User user = users.nykanen;
        user.setCourseId(123123123l);
        hs.update(user);
        hs.flush();
        
        request.username = SampleUsers.NYKANEN_USERNAME;
        request.password = SampleUsers.NYKANEN_PASSWORD;
        
        verifyLoginFailed(controller.handle(request));
    }
    
    @Test
    public void shouldNotAcceptStudentLoginIfStudentsCourseIsHidden() throws Exception {
        ModelAccessChecker.disableForSession(hs);
        
        Course course = courses.autumnCourse;
        
        users.nykanen.setCourseId(course.getId());
        hs.update(users.nykanen);
        hs.flush();
        
        course.setHidden(true);
        hs.update(course);
        hs.flush();
        
        request.username = SampleUsers.NYKANEN_USERNAME;
        request.password = SampleUsers.NYKANEN_PASSWORD;
        
        verifyLoginFailed(controller.handle(request));
    }
    
    @Test
    public void shouldAcceptAdministiveLoginEvenIfSelectedCourseIsHidden() throws Exception {
        ModelAccessChecker.disableForSession(hs);
        
        Course course = courses.autumnCourse;
        
        users.editor.setCourseId(course.getId());
        hs.update(users.editor);
        hs.flush();
        
        course.setHidden(true);
        hs.update(course);
        hs.flush();
        
        request.username = SampleUsers.EDITOR_USERNAME;
        request.password = SampleUsers.EDITOR_PASSWORD;
        
        controller.handle(request);
        verifyLoginSucceededFor(users.editor);
    }
    
    @Test
    public void shouldAcceptLoginWithResetPassword() throws Exception {
        ModelAccessChecker.disableForSession(hs);
        
        User user = users.nykanen;
        String password = "someAutogeneratedPassword";
        user.setResetPasswordSha1(User.hashPassword(password));
        user.setCourseId(courses.autumnCourse.getId());
        hs.update(user);
        hs.flush();
        
        request.username = user.getUsername();
        request.password = password;
        
        controller.handle(request);
        verifyLoginSucceededFor(user);
    }
    
    @Test
    public void whenLoggingInWithResetPasswordShouldOverwriteOldPassword() throws Exception {
        ModelAccessChecker.disableForSession(hs);
        
        User user = users.nykanen;
        String password = "someAutogeneratedPassword";
        String hashedPassword = User.hashPassword(password);
        user.setResetPasswordSha1(hashedPassword);
        user.setCourseId(courses.autumnCourse.getId());
        hs.update(user);
        hs.flush();
        
        request.username = user.getUsername();
        request.password = password;
        
        controller.handle(request);
        
        hs.refresh(user);
        assertEquals(hashedPassword, user.getPasswordSha1());
        assertNull(user.getResetPasswordSha1());
    }
    
}
