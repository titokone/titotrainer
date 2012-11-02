package fi.helsinki.cs.titotrainer.app.admin.controller;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;

import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.app.access.TitoBaseRole;
import fi.helsinki.cs.titotrainer.app.admin.request.AbstractUserModificationRequest;
import fi.helsinki.cs.titotrainer.app.controller.TitoActionController;
import fi.helsinki.cs.titotrainer.app.i18n.TitoTranslation;
import fi.helsinki.cs.titotrainer.app.model.User;
import fi.helsinki.cs.titotrainer.app.model.misc.ArgumentUtils;
import fi.helsinki.cs.titotrainer.framework.request.RequestInvalidity;
import fi.helsinki.cs.titotrainer.framework.response.ErrorResponseException;
import fi.helsinki.cs.titotrainer.framework.response.RedirectResponse;
import fi.helsinki.cs.titotrainer.framework.response.Response;

public abstract class AbstractUserModificationController<RequestType extends AbstractUserModificationRequest> extends TitoActionController<RequestType> {
    protected String trimAndNullify(String s) {
        if (s != null)
            return ArgumentUtils.nullifyOnEmpty(s.trim());
        else
            return s;
    }
    
    protected abstract User getUnmodifiedUser(RequestType req, Session hs) throws ErrorResponseException;
    
    protected void modifyUser(User user, RequestType req, Session hs) throws ErrorResponseException {
        
        req.username = trimAndNullify(req.username);
        req.firstName = trimAndNullify(req.firstName);
        req.lastName = trimAndNullify(req.lastName);
        req.email = trimAndNullify(req.email);
        req.studentNumber = trimAndNullify(req.studentNumber);
        req.passwd = trimAndNullify(req.passwd);
        
        user.forcefullySetUsername(req.username); // The request's validator shall prevent a null or empty username
        user.setFirstName(req.firstName);
        user.setLastName(req.lastName);
        user.setEmail(req.email);
        user.setStudentNumber(req.studentNumber);
        
        TitoBaseRole role = TitoBaseRole.getRoleByName(req.role);
        user.forcefullySetParentRole(role); // The request's validator shall prevent invalid roles.
        
        user.setCourseId(req.courseId);
        
        if (req.prefLocale != null) {
            TitoTranslation tt = req.getContext().getTitoTranslation();
            Locale locale = TitoTranslation.parseLocale(req.prefLocale);
            if (tt.isSupportedLocale(locale))
                user.setPrefLocale(locale);
        } else {
            user.setPrefLocale(null);
        }
        
        maybeSetPassword(req, user);
        
        req.getUserSession().setAttribute("userBeingEdited", user);
        
        hs.evict(user);
    }
    
    protected void maybeSetPassword(RequestType req, User user) {
        if (req.passwd != null)
            user.setPasswordSha1(User.hashPassword(req.passwd));
    }
    
    protected abstract String getRedirectPathForInvalidRequest(RequestType req);
    
    @Override
    protected Response handleInvalid(RequestType req, Session hs, Collection<RequestInvalidity> invalidities) throws Exception {
        User user = getUnmodifiedUser(req, hs);
        modifyUser(user, req, hs); // Pass the unsaved copy back to the form
        
        HashSet<String> invalidFields = new HashSet<String>();
        for (RequestInvalidity inv : invalidities) {
            if (inv.getField() != null)
                invalidFields.add(inv.getField());
            else
                logger.warn("Unexpected non-field-specific request invalidity: " + inv.getMsgKey());
        }
        req.getUserSession().setAttribute("invalidFields", invalidFields);
        
        return new RedirectResponse(getRedirectPathForInvalidRequest(req));
    }
    
}
