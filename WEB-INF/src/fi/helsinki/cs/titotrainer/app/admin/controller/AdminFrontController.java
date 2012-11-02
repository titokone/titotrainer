package fi.helsinki.cs.titotrainer.app.admin.controller;

import fi.helsinki.cs.titotrainer.app.access.TitoBaseRole;
import fi.helsinki.cs.titotrainer.app.admin.access.AdminAccess;
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
import fi.helsinki.cs.titotrainer.app.controller.ModuleFrontController;
import fi.helsinki.cs.titotrainer.framework.controller.RelativeRedirectController;
import fi.helsinki.cs.titotrainer.framework.request.DefaultRequest;
import fi.helsinki.cs.titotrainer.framework.response.Response;

public class AdminFrontController extends ModuleFrontController {
    
    private RelativeRedirectController loginRedirect;
    private RelativeRedirectController courseListRedirect;
    
    public AdminFrontController() {
        this.setAccessController(AdminAccess.getInstance());
        
        this.loginRedirect = new RelativeRedirectController("/login");
        this.courseListRedirect = new RelativeRedirectController("/admin/courselist");
        
        this.addRule("/admin", this.courseListRedirect);
        
        this.addRule("/admin/courselist", new CourseListView());
        this.addRule("/admin/course", new CourseView());
        this.addRule("/admin/createcourse", new CreateCourseController());
        this.addRule("/admin/updatecourse", new UpdateCourseController());
        this.addRule("/admin/deletecourse", new DeleteCourseController());
        
        this.addRule("/admin/category", new CategoryView());
        this.addRule("/admin/createcategory", new CreateCategoryController());
        this.addRule("/admin/updatecategory", new UpdateCategoryController());
        this.addRule("/admin/deletecategory", new DeleteCategoryController());
        
        this.addRule("/admin/task", new TaskView());
        this.addRule("/admin/taskfile", new TaskFileView());
        this.addRule("/admin/loadtasks", new LoadTasksView());
        this.addRule("/admin/doloadtasks", new LoadTasksController());
        this.addRule("/admin/addloadedtasks", new AddLoadedTasksController());
        this.addRule("/admin/createtask", new CreateTaskController());
        this.addRule("/admin/updatetask", new UpdateTaskController());
        this.addRule("/admin/copytask", new CopyTaskController());
        this.addRule("/admin/deletetask", new DeleteTaskController());
        this.addRule("/admin/taskbulkop", new TaskBulkOpController());
        
        this.addRule("/admin/statsbyuser", new StatsByUserView());
        this.addRule("/admin/userstats", new UserStatsView());
        
        this.addRule("/admin/globalsettings", new GlobalSettingsView());
        this.addRule("/admin/saveglobalsettings", new SaveGlobalSettingsController());
        
        this.addRule("/admin/userlist", new UserListView());
        this.addRule("/admin/user", new UserView());
        this.addRule("/admin/createuser", new CreateUserController());
        this.addRule("/admin/updateuser", new UpdateUserController());
        this.addRule("/admin/deleteuser", new DeleteUserController());
    }
    
    @Override
    protected Response fallback(DefaultRequest req) throws Exception {
        
        // TODO: add an error message (or just 404 instead)
        if (req.getLocalPath().startsWith("/admin")) {
            if (req.getUserSession().getRole().inherits(TitoBaseRole.ADMINISTRATIVE))
                return this.callHandler(courseListRedirect, req);
            else
                return this.callHandler(loginRedirect, req);
        } else {
            return super.fallback(req);
        }
    }
}
