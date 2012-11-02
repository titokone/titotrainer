package fi.helsinki.cs.titotrainer.app.student.controller;

import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.app.access.TitoBaseRole;
import fi.helsinki.cs.titotrainer.app.controller.ModuleFrontController;
import fi.helsinki.cs.titotrainer.app.model.Course;
import fi.helsinki.cs.titotrainer.app.model.User;
import fi.helsinki.cs.titotrainer.app.request.TitoRequest;
import fi.helsinki.cs.titotrainer.app.session.Messenger;
import fi.helsinki.cs.titotrainer.app.session.TitoUserSession;
import fi.helsinki.cs.titotrainer.app.student.access.StudentAccess;
import fi.helsinki.cs.titotrainer.app.student.view.PersonalInfoView;
import fi.helsinki.cs.titotrainer.app.student.view.TaskListView;
import fi.helsinki.cs.titotrainer.app.student.view.TaskView;
import fi.helsinki.cs.titotrainer.framework.controller.RelativeRedirectController;
import fi.helsinki.cs.titotrainer.framework.i18n.Translator;
import fi.helsinki.cs.titotrainer.framework.request.DefaultRequest;
import fi.helsinki.cs.titotrainer.framework.response.RedirectResponse;
import fi.helsinki.cs.titotrainer.framework.response.Response;

public class StudentFrontController extends ModuleFrontController {
    
    private RelativeRedirectController loginRedirect;
    private RelativeRedirectController taskListRedirect;
    
    public StudentFrontController() {
        this.setAccessController(StudentAccess.getInstance());
        
        this.loginRedirect = new RelativeRedirectController("/login");
        this.taskListRedirect = new RelativeRedirectController("/student/tasklist");
        
        this.addRule("/student", this.taskListRedirect);
        
        this.addRule("/student/tasklist", new TaskListView());
        this.addRule("/student/task", new TaskView());
        
        this.addRule("/student/personalinfo", new PersonalInfoView());
        this.addRule("/student/editprofile", new EditProfileController());
        
        this.addRule("/student/executeprogram", new TaskController());
    }
    
    @Override
    public Response handleValid(DefaultRequest req, Session hs) throws Exception {
        TitoUserSession userSession = ((TitoUserSession)req.getUserSession());
        User user = userSession.getAuthenticatedUser();
        if (user.inheritsRole(TitoBaseRole.ADMINISTRATIVE) &&
            (user.getCourseId() == null || hs.get(Course.class, user.getCourseId()) == null) &&
            !req.getLocalPath().startsWith("/student/personalinfo") &&
            !req.getLocalPath().startsWith("/student/editprofile")) {
            
            // An administrator has not yet chosen a course.
            TitoRequest treq = req.copy(TitoRequest.class);
            Translator tr = treq.getContext().getTitoTranslation().getClassTranslator(treq, this.getClass());
            
            userSession.getMessenger().appendMessage(Messenger.GLOBAL_WARNING_CATEGORY, tr.tr("choose_a_course"));
            return new RedirectResponse("/student/personalinfo");
        }
        
        return super.handleValid(req, hs);
    }
    
    @Override
    protected Response fallback(DefaultRequest req) throws Exception {
        
        // TODO: add an error message (or just 404 instead)
        if (req.getLocalPath().startsWith("/student")) {
            if (req.getUserSession().getRole() == TitoBaseRole.STUDENT)
                return this.callHandler(taskListRedirect, req);
            else
                return this.callHandler(loginRedirect, req);
        } else {
            return super.fallback(req);
        }
    }
}
