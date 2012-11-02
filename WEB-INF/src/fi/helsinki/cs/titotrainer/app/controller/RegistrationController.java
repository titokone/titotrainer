package fi.helsinki.cs.titotrainer.app.controller;

import static org.hamcrest.Matchers.*;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;

import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.app.access.TitoBaseRole;
import fi.helsinki.cs.titotrainer.app.i18n.TitoTranslation;
import fi.helsinki.cs.titotrainer.app.model.Course;
import fi.helsinki.cs.titotrainer.app.model.User;
import fi.helsinki.cs.titotrainer.app.model.misc.ArgumentUtils;
import fi.helsinki.cs.titotrainer.app.request.RegistrationRequest;
import fi.helsinki.cs.titotrainer.app.session.Messenger;
import fi.helsinki.cs.titotrainer.framework.i18n.Translator;
import fi.helsinki.cs.titotrainer.framework.request.RequestInvalidity;
import fi.helsinki.cs.titotrainer.framework.response.RedirectResponse;
import fi.helsinki.cs.titotrainer.framework.response.Response;

public class RegistrationController extends TitoActionController<RegistrationRequest> {
    
    private User makeNewStudentUser(RegistrationRequest req, Session hs) {
        User user = new User();
        
        req.username = ArgumentUtils.nullifyOnEmpty(req.username);
        req.firstName = ArgumentUtils.nullifyOnEmpty(req.firstName);
        req.lastName = ArgumentUtils.nullifyOnEmpty(req.lastName);
        req.email = ArgumentUtils.nullifyOnEmpty(req.email);
        req.studentNumber = ArgumentUtils.nullifyOnEmpty(req.studentNumber);
        req.passwd = ArgumentUtils.nullifyOnEmpty(req.passwd);
        req.passwd2 = ArgumentUtils.nullifyOnEmpty(req.passwd2);
        
        user.setParentRole(TitoBaseRole.STUDENT);
        
        user.forcefullySetUsername(req.username);
        user.setFirstName(req.firstName);
        user.setLastName(req.lastName);
        user.setEmail(req.email);
        user.setStudentNumber(req.studentNumber);
        
        user.setCourseId(req.courseId);
        
        if (req.prefLocale != null) {
            TitoTranslation tt = req.getContext().getTitoTranslation();
            Locale locale = TitoTranslation.parseLocale(req.prefLocale);
            if (tt.isSupportedLocale(locale))
                user.setPrefLocale(locale);
        } else {
            user.setPrefLocale(null);
        }
        
        if (req.passwd != null) {
            user.setPasswordSha1(User.hashPassword(req.passwd));
        }
        
        req.getUserSession().setAttribute("profile", user);
        
        hs.evict(user);
        return user;
    }
    
    @Override
    protected Response handleValid(RegistrationRequest req, Session hs) throws Exception {
        if (!isValidCourse(hs, req.courseId)) {
            Collection<RequestInvalidity> invs = Collections.emptyList();
            return handleInvalid(req, hs, invs);
        }
        
        User user = makeNewStudentUser(req, hs);
        
        hs.save(user);
        
        Translator tr = this.getTranslator(req);
        Messenger msngr = req.getUserSession().getMessenger();
        msngr.appendMessage(Messenger.GLOBAL_SUCCESS_CATEGORY, tr.trp("user_created_%s", user.getUsername()));
        
        return new RedirectResponse("/login");
    }
    
    private boolean isValidCourse(Session hs, Long courseId) {
        Course course = (Course)hs.get(Course.class, courseId);
        if (course == null)
            return false;
        if (course.getHidden())
            return false;
        return true;
    }

    @Override
    protected Response handleInvalid(RegistrationRequest req, Session hs, Collection<RequestInvalidity> invalidities) throws Exception {
        User user = makeNewStudentUser(req, hs); // Pass the unsaved copy back to the form

        HashSet<String> invFields = new HashSet<String>();
        for (RequestInvalidity inv : invalidities) {
            if (inv.getField() != null)
                invFields.add(inv.getField());
        }
        
        Translator tr = this.getTranslator(req);
        Messenger msngr = req.getUserSession().getMessenger();
        
        msngr.appendMessage(Messenger.GLOBAL_ERROR_CATEGORY, tr.tr("errors"));
        
        if (hasItem(hasProperty("msgKey", equalTo("password_mismatch"))).matches(invalidities)) {
            invFields.add("passwd");
            invFields.add("passwd2");
            msngr.appendMessage(Messenger.GLOBAL_ERROR_CATEGORY, tr.tr("passwords_did_not_match"));
        }
        
        req.getUserSession().setAttribute("regUser", user);
        req.getUserSession().setAttribute("invFields", invFields);
        
        return new RedirectResponse("/registration");
    }
    
    @Override
    public Class<RegistrationRequest> getRequestType() {
        return RegistrationRequest.class;
    }
}
