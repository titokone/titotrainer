package fi.helsinki.cs.titotrainer.app.access;

import static fi.helsinki.cs.titotrainer.app.access.TitoBaseRole.*;
import static org.hamcrest.Matchers.*;
import fi.helsinki.cs.titotrainer.app.admin.controller.AdminFrontController;
import fi.helsinki.cs.titotrainer.app.controller.LoginController;
import fi.helsinki.cs.titotrainer.app.controller.LogoutController;
import fi.helsinki.cs.titotrainer.app.controller.RegistrationController;
import fi.helsinki.cs.titotrainer.app.controller.ResetPasswordController;
import fi.helsinki.cs.titotrainer.app.student.controller.StudentFrontController;
import fi.helsinki.cs.titotrainer.app.view.JsParamsView;
import fi.helsinki.cs.titotrainer.app.view.LoginView;
import fi.helsinki.cs.titotrainer.app.view.RegistrationView;
import fi.helsinki.cs.titotrainer.app.view.ResetPasswordView;
import fi.helsinki.cs.titotrainer.framework.access.hamcrest.HamcrestAccessController;
import fi.helsinki.cs.titotrainer.framework.controller.RelativeRedirectController;

/**
 * This singleton defines the access control rules for
 * TitoTrainer's front controller.
 * 
 * @see TitoBaseRole
 */
public final class FrontAccess extends HamcrestAccessController {
    
    private static final FrontAccess instance = new FrontAccess();
    
    public static FrontAccess getInstance() {
        return instance;
    }
    
    private FrontAccess() {
        allow(ANYONE, instanceOf(RelativeRedirectController.class));
        
        allow(ANYONE, instanceOf(LoginView.class));
        allow(ANYONE, instanceOf(LoginController.class));
        allow(ANYONE, instanceOf(LogoutController.class));
        
        allow(ANYONE, instanceOf(RegistrationView.class));
        allow(ANYONE, instanceOf(RegistrationController.class));
        
        allow(ANYONE, instanceOf(ResetPasswordView.class));
        allow(ANYONE, instanceOf(ResetPasswordController.class));
        
        allow(STUDENT, instanceOf(StudentFrontController.class));
        allow(ADMINISTRATIVE, instanceOf(AdminFrontController.class));
        
        allow(ANYONE, instanceOf(JsParamsView.class));
    }
}
