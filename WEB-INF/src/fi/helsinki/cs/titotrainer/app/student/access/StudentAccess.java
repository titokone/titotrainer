package fi.helsinki.cs.titotrainer.app.student.access;

import static fi.helsinki.cs.titotrainer.app.access.TitoBaseRole.*;
import static org.hamcrest.Matchers.*;
import fi.helsinki.cs.titotrainer.app.access.FrontAccess;
import fi.helsinki.cs.titotrainer.app.access.TitoBaseRole;
import fi.helsinki.cs.titotrainer.framework.RequestHandler;
import fi.helsinki.cs.titotrainer.framework.access.hamcrest.HamcrestAccessController;
import fi.helsinki.cs.titotrainer.framework.controller.RelativeRedirectController;

/**
 * This singleton defines the access control rules for
 * TitoTrainer's student front controller.
 * 
 * @see FrontAccess
 * @see TitoBaseRole
 */
public class StudentAccess extends HamcrestAccessController {
    
    private static final StudentAccess instance = new StudentAccess();
    
    public static StudentAccess getInstance() {
        return instance;
    }
    
    private StudentAccess() {
        allow(ANYONE, instanceOf(RelativeRedirectController.class));
        
        allow(STUDENT, instanceOf(RequestHandler.class));
    }
}
