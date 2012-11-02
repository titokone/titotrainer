package fi.helsinki.cs.titotrainer.app.controller;

import java.util.Collection;

import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.app.access.TitoBaseRole;
import fi.helsinki.cs.titotrainer.app.model.Course;
import fi.helsinki.cs.titotrainer.app.model.User;
import fi.helsinki.cs.titotrainer.app.request.LoginRequest;
import fi.helsinki.cs.titotrainer.app.session.Messenger;
import fi.helsinki.cs.titotrainer.app.session.TitoUserSession;
import fi.helsinki.cs.titotrainer.framework.i18n.Translator;
import fi.helsinki.cs.titotrainer.framework.request.RequestInvalidity;
import fi.helsinki.cs.titotrainer.framework.response.RedirectResponse;
import fi.helsinki.cs.titotrainer.framework.response.Response;

public class LoginController extends TitoActionController<LoginRequest> {
    @Override
    public Class<LoginRequest> getRequestType() {
        return LoginRequest.class;
    }
    
    @Override
    protected Response handleValid(LoginRequest req, Session hs) throws Exception {
        String passwordHash = User.hashPassword(req.password);
        
        User user = (User)hs.createQuery("FROM User WHERE username = ? AND (passwordSha1 = ? OR resetPasswordSha1 = ?)")
                            .setString(0, req.username)
                            .setString(1, passwordHash)
                            .setString(2, passwordHash)
                            .uniqueResult();
        
        if (user == null) {
            return handleInvalid(req, hs, null);
        }
        
        if (passwordHash.equals(user.getResetPasswordSha1())) {
            user.setPasswordSha1(passwordHash);
            user.setResetPasswordSha1(null);
            hs.update(user);
            hs.flush();
        }
        
        boolean isAdmin = user.inheritsRole(TitoBaseRole.ADMINISTRATIVE);
        
        if (!isAdmin) {
            // Check that the student's course exists and is not hidden.
            if (user.getCourseId() == null)
                return handleInvalid(req, hs, null);
            
            Course course = (Course)hs.get(Course.class, user.getCourseId());
            
            if (course == null || course.getHidden())
                return handleInvalid(req, hs, null);
        }
        
        TitoUserSession userSession = req.getUserSession();
        userSession.setAuthenticatedUser(user);
        
        if (isAdmin) {
            return new RedirectResponse("/admin");
        } else {
            return new RedirectResponse("/student");
        }
    }
    
    @Override
    protected Response handleInvalid(LoginRequest req, Session hs, Collection<RequestInvalidity> invalidities) throws Exception {
        Translator tr = this.getTranslator(req);
        req.getUserSession().getMessenger().appendMessage(Messenger.GLOBAL_ERROR_CATEGORY, tr.tr("login_failed"), 1);
        return new RedirectResponse("/login");
    }
    
}
