package fi.helsinki.cs.titotrainer.app.admin.controller;

import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.app.admin.request.DeleteUserRequest;
import fi.helsinki.cs.titotrainer.app.admin.view.UserListView;
import fi.helsinki.cs.titotrainer.app.controller.TitoActionController;
import fi.helsinki.cs.titotrainer.app.model.User;
import fi.helsinki.cs.titotrainer.app.session.Messenger;
import fi.helsinki.cs.titotrainer.framework.response.ErrorResponse;
import fi.helsinki.cs.titotrainer.framework.response.RedirectResponse;
import fi.helsinki.cs.titotrainer.framework.response.Response;

public class DeleteUserController extends TitoActionController<DeleteUserRequest> {
    
    @Override
    public Class<DeleteUserRequest> getRequestType() {
        return DeleteUserRequest.class;
    }
    
    @Override
    protected Response handleValid(DeleteUserRequest req, Session hs) throws Exception {
        User user = (User)hs.get(User.class, req.id);
        if (user == null)
            return new ErrorResponse(404);
        
        boolean selfDelete = (user.equals(req.getUserSession().getAuthenticatedUser()));
        
        hs.createQuery("UPDATE Task SET creator = NULL WHERE creator = ?").setParameter(0, user).executeUpdate();
        
        hs.delete(user);
        
        this.appendMessage(req, Messenger.GLOBAL_SUCCESS_CATEGORY, this.getTranslator(req).tr("user_deleted"));
        
        if (selfDelete)
            return new RedirectResponse("/dologout");
        
        UserListView.FilterParams params = UserListView.FilterParams.getFromSession(req.getUserSession());
        String paramString = "";
        if (params != null)
            paramString = "?" + params.makeQueryString();
        
        return new RedirectResponse("/admin/userlist" + paramString);
    }
    
}
