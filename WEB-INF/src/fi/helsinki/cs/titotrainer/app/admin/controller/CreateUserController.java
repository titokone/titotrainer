package fi.helsinki.cs.titotrainer.app.admin.controller;

import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.app.admin.request.CreateUserRequest;
import fi.helsinki.cs.titotrainer.app.model.User;
import fi.helsinki.cs.titotrainer.app.session.Messenger;
import fi.helsinki.cs.titotrainer.framework.i18n.Translator;
import fi.helsinki.cs.titotrainer.framework.response.ErrorResponseException;
import fi.helsinki.cs.titotrainer.framework.response.RedirectResponse;
import fi.helsinki.cs.titotrainer.framework.response.Response;

public class CreateUserController extends AbstractUserModificationController<CreateUserRequest> {
    
    @Override
    protected User getUnmodifiedUser(CreateUserRequest req, Session hs) throws ErrorResponseException {
        return new User();
    }
    
    @Override
    protected Response handleValid(CreateUserRequest req, Session hs) throws Exception {
        User user = getUnmodifiedUser(req, hs);
        
        modifyUser(user, req, hs);
        
        long id = (Long)hs.save(user);
        
        Translator tr = this.getTranslator(req);
        Messenger msngr = req.getUserSession().getMessenger();
        msngr.appendMessage(Messenger.GLOBAL_SUCCESS_CATEGORY, tr.tr("user_created"));
        
        return new RedirectResponse("/admin/user?id=" + id);
    }
    
    @Override
    protected String getRedirectPathForInvalidRequest(CreateUserRequest req) {
        return "/admin/user";
    }
    
    @Override
    public Class<CreateUserRequest> getRequestType() {
        return CreateUserRequest.class;
    }
}
