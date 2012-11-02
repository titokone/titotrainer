package fi.helsinki.cs.titotrainer.app.admin.controller;

import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.app.admin.request.UpdateUserRequest;
import fi.helsinki.cs.titotrainer.app.model.User;
import fi.helsinki.cs.titotrainer.app.session.Messenger;
import fi.helsinki.cs.titotrainer.framework.i18n.Translator;
import fi.helsinki.cs.titotrainer.framework.response.ErrorResponseException;
import fi.helsinki.cs.titotrainer.framework.response.RedirectResponse;
import fi.helsinki.cs.titotrainer.framework.response.Response;

public class UpdateUserController extends AbstractUserModificationController<UpdateUserRequest> {

    @Override
    protected User getUnmodifiedUser(UpdateUserRequest req, Session hs) throws ErrorResponseException {
        hs.evict(req.getUserSession().getAuthenticatedUser()); // In case it's the same one
        
        User user = (User)hs.get(User.class, req.id);
        if (user == null)
            throw new ErrorResponseException(404);
        assert(user != req.getUserSession().getAuthenticatedUser()); // "user" should be a copy even if it's the authenticated user
        
        return user;
    }
    
    @Override
    protected Response handleValid(UpdateUserRequest req, Session hs) throws Exception {
        
        User copy = getUnmodifiedUser(req, hs);
        modifyUser(copy, req, hs); // Pass the unsaved copy back to the form
        
        hs.update(copy);
        
        // Reload the session's user object if we were updating it. For some reason hs.refresh() doesn't do the trick.
        if (req.getUserSession().getAuthenticatedUser().getId() == req.id) {
            User reloadedUser = (User)hs.load(User.class, req.getUserSession().getAuthenticatedUser().getId());
            req.getUserSession().setAuthenticatedUser(reloadedUser);
        }
        
        Translator tr = this.getTranslator(req);
        Messenger msngr = req.getUserSession().getMessenger();
        msngr.appendMessage(Messenger.GLOBAL_SUCCESS_CATEGORY, tr.tr("changes_saved"));
        if (req.passwd != null && req.setPassword) {
            msngr.appendMessage(Messenger.GLOBAL_SUCCESS_CATEGORY, tr.tr("password_set"));
        }
        
        return new RedirectResponse("/admin/user?id=" + req.id);
    }
    
    @Override
    protected void maybeSetPassword(UpdateUserRequest req, User user) {
        if (req.setPassword)
            super.maybeSetPassword(req, user);
    }
    
    @Override
    protected String getRedirectPathForInvalidRequest(UpdateUserRequest req) {
        return "/admin/user?id=" + req.id;
    }
    
    @Override
    public Class<UpdateUserRequest> getRequestType() {
        return UpdateUserRequest.class;
    }
}
