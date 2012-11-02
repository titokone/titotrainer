package fi.helsinki.cs.titotrainer.app.model.access;

import static fi.helsinki.cs.titotrainer.app.access.TitoBaseRole.*;
import static fi.helsinki.cs.titotrainer.app.model.access.hamcrest.CurrentUserHasAccess.*;
import static fi.helsinki.cs.titotrainer.app.model.access.hamcrest.InheritsRole.*;
import static fi.helsinki.cs.titotrainer.app.model.access.hamcrest.IsCurrentUser.*;
import static fi.helsinki.cs.titotrainer.framework.model.handler.ModelPermission.*;
import static org.hamcrest.Matchers.*;
import fi.helsinki.cs.titotrainer.app.access.TitoBaseRole;
import fi.helsinki.cs.titotrainer.app.model.Answer;
import fi.helsinki.cs.titotrainer.app.model.Category;
import fi.helsinki.cs.titotrainer.app.model.Course;
import fi.helsinki.cs.titotrainer.app.model.Criterion;
import fi.helsinki.cs.titotrainer.app.model.ExecStatus;
import fi.helsinki.cs.titotrainer.app.model.Input;
import fi.helsinki.cs.titotrainer.app.model.TString;
import fi.helsinki.cs.titotrainer.app.model.Task;
import fi.helsinki.cs.titotrainer.app.model.User;
import fi.helsinki.cs.titotrainer.app.model.Validation;
import fi.helsinki.cs.titotrainer.framework.access.hamcrest.HamcrestAccessController;

/**
 * Defines the access rules for the data model of the application.
 */
public class ModelAccessController extends HamcrestAccessController {
    
    private static ModelAccessController instance = new ModelAccessController();
    
    public static ModelAccessController getInstance() {
        return instance;
    }
    
    @SuppressWarnings("unchecked")
    private ModelAccessController() {
        
        allow(ANYONE, instanceOf(TString.class)); // There is no easy way to guard the TStrings.
        
        // -------------------------
        // --- Guest permissions ---
        // -------------------------
        allow(GUEST, instanceOf(User.class), READ); // For logging in
        allow(GUEST, instanceOf(Course.class), READ); // For a log in check
        allow(GUEST, instanceOf(User.class), CREATE); // For registering
        allow(GUEST, instanceOf(User.class), UPDATE); // For password reset
        
        // ---------------------------
        // --- Student permissions ---
        // ---------------------------
        
        // General read access
        //TODO: allow access only to selected course
        allow(STUDENT, instanceOf(Category.class), READ);
        allow(STUDENT, instanceOf(Course.class), READ);
        allow(STUDENT, allOf(instanceOf(Task.class), hasProperty("hidden", is(false))), READ);
        allow(STUDENT, instanceOf(Criterion.class), READ);
        allow(STUDENT, instanceOf(Input.class), READ);
        
        // Users read access
        allow(STUDENT, allOf(instanceOf(User.class),
                             anyOf(isCurrentUser(),
                                   inheritsRole(TitoBaseRole.ADMINISTRATIVE) // Because of Task.creator
                                   )), READ);
        
        // Own profile
        allow(STUDENT, isCurrentUser(), UPDATE);
        
        // Create answers, validations and execStatuses
        allow(STUDENT, allOf(instanceOf(Answer.class),
                             hasProperty("user", isCurrentUser()),
                             hasProperty("task", currentUserHasAccess(READ))), READ, CREATE, DELETE);
        allow(STUDENT, allOf(instanceOf(Validation.class),
                             hasProperty("answer", hasProperty("user", isCurrentUser()))), READ, CREATE, DELETE);
        allow(STUDENT, allOf(instanceOf(ExecStatus.class),
                             hasProperty("answer", hasProperty("user", isCurrentUser()))), READ, CREATE, DELETE);
        
        // ----------------------------------
        // --- Administrative permissions ---
        // ----------------------------------
        
        // General
        allow(ADMINISTRATIVE, anything(), READ);
        allow(ADMINISTRATOR, anything());
        
        // Editors may edit tasks and their dependencies
        allow(EDITOR, instanceOf(Task.class));
        allow(EDITOR, instanceOf(Criterion.class));
        allow(EDITOR, instanceOf(Input.class));
        // For editing a task with existing answers
        allow(EDITOR, instanceOf(Answer.class));
        allow(EDITOR, instanceOf(Validation.class));
        allow(EDITOR, instanceOf(ExecStatus.class));
        // Because of bidirectional dependency:
        allow(EDITOR, instanceOf(Category.class), UPDATE);
        allow(EDITOR, instanceOf(Course.class), UPDATE);
    }
    
}
