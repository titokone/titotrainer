package fi.helsinki.cs.titotrainer.app.admin.view;

import java.util.Set;

import org.apache.commons.collections.SetUtils;
import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.app.access.TitoBaseRole;
import fi.helsinki.cs.titotrainer.app.admin.request.UserViewRequest;
import fi.helsinki.cs.titotrainer.app.i18n.TitoTranslation;
import fi.helsinki.cs.titotrainer.app.model.User;
import fi.helsinki.cs.titotrainer.app.session.Messenger;
import fi.helsinki.cs.titotrainer.app.view.TitoPageView;
import fi.helsinki.cs.titotrainer.framework.response.ErrorResponseException;
import fi.helsinki.cs.titotrainer.framework.response.ViewResponse;
import fi.helsinki.cs.titotrainer.framework.view.template.TemplateRenderer;

public class UserView extends TitoPageView<UserViewRequest> {
    
    @Override
    public Class<UserViewRequest> getRequestType() {
        return UserViewRequest.class;
    }
    
    @Override
    protected String getTemplateName() {
        return "admin/user.vm";
    }
    
    @Override
    protected void handle(UserViewRequest req, Session hs, TemplateRenderer tr, ViewResponse resp) throws ErrorResponseException {
        TitoTranslation tt = req.getContext().getTitoTranslation();
        
        boolean newUser = (req.id == null);
        
        User user;
        if (newUser) {
            user = new User();
        } else {
            user = (User)hs.get(User.class, req.id);
            if (user == null)
                throw new ErrorResponseException(404);
        }
        
        user = req.getUserSession().consumeAttribute("userBeingEdited", User.class, user);
        tr.put("theUser", user); // Avoid conflict with the common "user" variable defined by the superclass.
        tr.put("newUser", newUser);
        
        tr.put("availableLocales", tt.getSupportedLocales());
        tr.put("availableCourses", hs.createQuery("FROM Course").list());
        tr.put("availableRoles", new TitoBaseRole[] {TitoBaseRole.STUDENT,
                                                     TitoBaseRole.ASSISTANT,
                                                     TitoBaseRole.EDITOR,
                                                     TitoBaseRole.ADMINISTRATOR});
        
        Object invalidFields = req.getUserSession().consumeAttribute("invalidFields");
        if (invalidFields instanceof Set<?>) {
            req.getUserSession().getMessenger().appendMessage(Messenger.GLOBAL_ERROR_CATEGORY,
                                                              this.getTranslator(req).tr("invalid_fields"));
        } else {
            invalidFields = SetUtils.EMPTY_SET;
        }
        tr.put("invalidFields", invalidFields);
    }
    
}
