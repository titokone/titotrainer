package fi.helsinki.cs.titotrainer.app.admin.access;

import static fi.helsinki.cs.titotrainer.app.access.TitoBaseRole.*;
import static org.hamcrest.Matchers.*;
import fi.helsinki.cs.titotrainer.app.access.FrontAccess;
import fi.helsinki.cs.titotrainer.app.access.TitoBaseRole;
import fi.helsinki.cs.titotrainer.app.admin.controller.AddLoadedTasksController;
import fi.helsinki.cs.titotrainer.app.admin.controller.CopyTaskController;
import fi.helsinki.cs.titotrainer.app.admin.controller.CreateCategoryController;
import fi.helsinki.cs.titotrainer.app.admin.controller.CreateCourseController;
import fi.helsinki.cs.titotrainer.app.admin.controller.CreateTaskController;
import fi.helsinki.cs.titotrainer.app.admin.controller.CreateUserController;
import fi.helsinki.cs.titotrainer.app.admin.controller.DeleteCategoryController;
import fi.helsinki.cs.titotrainer.app.admin.controller.DeleteCourseController;
import fi.helsinki.cs.titotrainer.app.admin.controller.DeleteTaskController;
import fi.helsinki.cs.titotrainer.app.admin.controller.DeleteUserController;
import fi.helsinki.cs.titotrainer.app.admin.controller.LoadTasksController;
import fi.helsinki.cs.titotrainer.app.admin.controller.SaveGlobalSettingsController;
import fi.helsinki.cs.titotrainer.app.admin.controller.TaskBulkOpController;
import fi.helsinki.cs.titotrainer.app.admin.controller.UpdateCategoryController;
import fi.helsinki.cs.titotrainer.app.admin.controller.UpdateCourseController;
import fi.helsinki.cs.titotrainer.app.admin.controller.UpdateTaskController;
import fi.helsinki.cs.titotrainer.app.admin.controller.UpdateUserController;
import fi.helsinki.cs.titotrainer.app.admin.view.CategoryView;
import fi.helsinki.cs.titotrainer.app.admin.view.CourseListView;
import fi.helsinki.cs.titotrainer.app.admin.view.CourseView;
import fi.helsinki.cs.titotrainer.app.admin.view.GlobalSettingsView;
import fi.helsinki.cs.titotrainer.app.admin.view.LoadTasksView;
import fi.helsinki.cs.titotrainer.app.admin.view.StatsByUserView;
import fi.helsinki.cs.titotrainer.app.admin.view.TaskFileView;
import fi.helsinki.cs.titotrainer.app.admin.view.TaskView;
import fi.helsinki.cs.titotrainer.app.admin.view.UserListView;
import fi.helsinki.cs.titotrainer.app.admin.view.UserStatsView;
import fi.helsinki.cs.titotrainer.app.admin.view.UserView;
import fi.helsinki.cs.titotrainer.framework.access.hamcrest.HamcrestAccessController;
import fi.helsinki.cs.titotrainer.framework.controller.RelativeRedirectController;

/**
 * This singleton defines the access control rules for
 * TitoTrainer's admin front controller.
 * 
 * @see FrontAccess
 * @see TitoBaseRole
 */
public class AdminAccess extends HamcrestAccessController {
    
    private static final AdminAccess instance = new AdminAccess();
    
    public static AdminAccess getInstance() {
        return instance;
    }
    
    private AdminAccess() {
        allow(ANYONE, instanceOf(RelativeRedirectController.class));
        
        allow(ADMINISTRATIVE, instanceOf(CourseListView.class));
        allow(ADMINISTRATIVE, instanceOf(CourseView.class));
        allow(ADMINISTRATOR, instanceOf(CreateCourseController.class));
        allow(ADMINISTRATOR, instanceOf(UpdateCourseController.class));
        allow(ADMINISTRATOR, instanceOf(DeleteCourseController.class));
        
        allow(ADMINISTRATIVE, instanceOf(CategoryView.class));
        allow(ADMINISTRATOR, instanceOf(CreateCategoryController.class));
        allow(ADMINISTRATOR, instanceOf(UpdateCategoryController.class));
        allow(ADMINISTRATOR, instanceOf(DeleteCategoryController.class));
        
        allow(ADMINISTRATIVE, instanceOf(TaskView.class));
        allow(ADMINISTRATIVE, instanceOf(TaskFileView.class));
        allow(ADMINISTRATIVE, instanceOf(AddLoadedTasksController.class));
        allow(EDITOR, instanceOf(LoadTasksView.class));
        allow(EDITOR, instanceOf(LoadTasksController.class));
        allow(EDITOR, instanceOf(CreateTaskController.class));
        allow(EDITOR, instanceOf(UpdateTaskController.class));
        allow(EDITOR, instanceOf(DeleteTaskController.class));
        allow(EDITOR, instanceOf(CopyTaskController.class));
        allow(EDITOR, instanceOf(TaskBulkOpController.class));
        
        allow(ADMINISTRATIVE, instanceOf(StatsByUserView.class));
        allow(ADMINISTRATIVE, instanceOf(UserStatsView.class));
        
        allow(ADMINISTRATOR, instanceOf(GlobalSettingsView.class));
        allow(ADMINISTRATOR, instanceOf(SaveGlobalSettingsController.class));
        
        allow(ADMINISTRATOR, instanceOf(UserListView.class));
        allow(ADMINISTRATOR, instanceOf(UserView.class));
        allow(ADMINISTRATOR, instanceOf(CreateUserController.class));
        allow(ADMINISTRATOR, instanceOf(UpdateUserController.class));
        allow(ADMINISTRATOR, instanceOf(DeleteUserController.class));
    }
    
}
