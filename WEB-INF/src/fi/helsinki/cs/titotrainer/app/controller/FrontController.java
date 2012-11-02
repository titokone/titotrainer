package fi.helsinki.cs.titotrainer.app.controller;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.app.access.FrontAccess;
import fi.helsinki.cs.titotrainer.app.access.TitoBaseRole;
import fi.helsinki.cs.titotrainer.app.admin.controller.AdminFrontController;
import fi.helsinki.cs.titotrainer.app.model.User;
import fi.helsinki.cs.titotrainer.app.model.access.CurrentCredentials;
import fi.helsinki.cs.titotrainer.app.model.access.ModelAccessController;
import fi.helsinki.cs.titotrainer.app.session.TitoUserSession;
import fi.helsinki.cs.titotrainer.app.student.controller.StudentFrontController;
import fi.helsinki.cs.titotrainer.app.view.JsParamsView;
import fi.helsinki.cs.titotrainer.app.view.LoginView;
import fi.helsinki.cs.titotrainer.app.view.RegistrationView;
import fi.helsinki.cs.titotrainer.app.view.ResetPasswordView;
import fi.helsinki.cs.titotrainer.framework.controller.RelativeRedirectController;
import fi.helsinki.cs.titotrainer.framework.model.handler.ModelAccessChecker;
import fi.helsinki.cs.titotrainer.framework.model.handler.ModelAccessDeniedException;
import fi.helsinki.cs.titotrainer.framework.request.DefaultRequest;
import fi.helsinki.cs.titotrainer.framework.response.ErrorResponse;
import fi.helsinki.cs.titotrainer.framework.response.Response;

/**
 * This is the first controller called by the application entry point.
 */
public class FrontController extends ModuleFrontController {
    
    Logger logger = Logger.getLogger(FrontController.class);
    
    public FrontController() {
        this.setAccessController(FrontAccess.getInstance());
        
        this.addRule("", new RelativeRedirectController("/login"));
        
        this.addRule("/login", new LoginView());
        this.addRule("/dologin", new LoginController());
        this.addRule("/dologout", new LogoutController());
        
        this.addRule("/registration", new RegistrationView());
        this.addRule("/doregistration", new RegistrationController());
        
        this.addRule("/resetpassword", new ResetPasswordView());
        this.addRule("/doresetpassword", new ResetPasswordController());
        
        this.addRule("/student", new StudentFrontController());
        this.addRule("/admin", new AdminFrontController());
        
        this.addRule("/params.js", new JsParamsView());
    }
    
    @Override
    public Response handle(DefaultRequest req) throws Exception {
        Session hs = req.getAttribs().getHibernateSession();
        User user = ((TitoUserSession)req.getUserSession()).getAuthenticatedUser();
        CurrentCredentials.setCurrentUser(user);
        
        if (user != null)
            ModelAccessChecker.enableForSession(hs, ModelAccessController.getInstance(), user.getParentRole());
        else
            ModelAccessChecker.enableForSession(hs, ModelAccessController.getInstance(), TitoBaseRole.GUEST);
        
        try {
            Response resp = super.handle(req);
            return resp;
        } catch (ModelAccessDeniedException e) {
            String msg = "Access denied by the model layer";
            logger.warn(msg, e);
            return new ErrorResponse(403, msg, e);
        }
    }
    
}
