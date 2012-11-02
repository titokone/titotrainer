package fi.helsinki.cs.titotrainer.app.student.controller;

import static org.hamcrest.Matchers.*;

import java.util.Collection;
import java.util.Locale;

import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.app.access.TitoBaseRole;
import fi.helsinki.cs.titotrainer.app.controller.TitoActionController;
import fi.helsinki.cs.titotrainer.app.i18n.TitoTranslation;
import fi.helsinki.cs.titotrainer.app.model.User;
import fi.helsinki.cs.titotrainer.app.model.misc.ArgumentUtils;
import fi.helsinki.cs.titotrainer.app.session.Messenger;
import fi.helsinki.cs.titotrainer.app.student.request.EditProfileRequest;
import fi.helsinki.cs.titotrainer.framework.i18n.Translator;
import fi.helsinki.cs.titotrainer.framework.request.RequestInvalidity;
import fi.helsinki.cs.titotrainer.framework.response.RedirectResponse;
import fi.helsinki.cs.titotrainer.framework.response.Response;

public class EditProfileController extends TitoActionController<EditProfileRequest> {

    private User makeEditedCopy(EditProfileRequest req, Session hs) {
        hs.evict(req.getUserSession().getAuthenticatedUser());
        User user = (User)hs.load(User.class, req.getUserSession().getAuthenticatedUser().getId());
        assert(user != req.getUserSession().getAuthenticatedUser()); // "user" should be a copy
        
        req.firstName = ArgumentUtils.nullifyOnEmpty(req.firstName);
        req.lastName = ArgumentUtils.nullifyOnEmpty(req.lastName);
        req.email = ArgumentUtils.nullifyOnEmpty(req.email);
        req.passwd = ArgumentUtils.nullifyOnEmpty(req.passwd);
        req.passwd2 = ArgumentUtils.nullifyOnEmpty(req.passwd2);
        
        user.setFirstName(req.firstName);
        user.setLastName(req.lastName);
        user.setEmail(req.email);
        
        if (req.courseId != null && user.inheritsRole(TitoBaseRole.ADMINISTRATIVE)) {
            user.setCourseId(req.courseId);
        }
        
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
    protected Response handleValid(EditProfileRequest req, Session hs) throws Exception {
        User copy = makeEditedCopy(req, hs);
        
        hs.update(copy);
        
        // Reload the session's user object. For some reason hs.refresh() doesn't do the trick.
        User reloadedUser = (User)hs.load(User.class, req.getUserSession().getAuthenticatedUser().getId());
        req.getUserSession().setAuthenticatedUser(reloadedUser);
        
        Translator tr = this.getTranslator(req);
        Messenger msngr = req.getUserSession().getMessenger();
        msngr.appendMessage(Messenger.GLOBAL_SUCCESS_CATEGORY, tr.tr("changes_saved"));
        if (req.passwd != null) {
            msngr.appendMessage(Messenger.GLOBAL_SUCCESS_CATEGORY, tr.tr("password_changed"));
        }
        return new RedirectResponse("/student/personalinfo");

    }
    
    @Override
    protected Response handleInvalid(EditProfileRequest req, Session hs, Collection<RequestInvalidity> invalidities) throws Exception {
        makeEditedCopy(req, hs); // Pass the unsaved copy back to the form
        
        Translator tr = this.getTranslator(req);
        Messenger msngr = req.getUserSession().getMessenger();
        if (hasItem(hasProperty("msgKey", equalTo("passwd_do_not_match"))).matches(invalidities))
            msngr.appendMessage(Messenger.GLOBAL_ERROR_CATEGORY, tr.tr("passwords_did_not_match"));
        return new RedirectResponse("/student/personalinfo");
    }

    @Override
    public Class<EditProfileRequest> getRequestType() {
        return EditProfileRequest.class;
    }
    
    
    
}
