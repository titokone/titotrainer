package fi.helsinki.cs.titotrainer.app.admin.controller;

import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.app.admin.request.DeleteTaskRequest;
import fi.helsinki.cs.titotrainer.app.controller.TitoActionController;
import fi.helsinki.cs.titotrainer.app.model.Task;
import fi.helsinki.cs.titotrainer.framework.response.ErrorResponse;
import fi.helsinki.cs.titotrainer.framework.response.RedirectResponse;
import fi.helsinki.cs.titotrainer.framework.response.Response;

public class DeleteTaskController extends TitoActionController<DeleteTaskRequest> {

    @Override
    protected Response handleValid(DeleteTaskRequest req, Session hs) throws Exception {
        Task task = (Task)hs.get(Task.class, req.id);
        if (task == null)
            return new ErrorResponse(404, "Task " + req.id + " not found");
        
        long courseId = task.getCourse().getId();
        Long categoryId = task.getCategory() != null ? task.getCategory().getId() : null;
        
        hs.delete(task);
        
        if (req.returnTo.equals("category"))
            return new RedirectResponse("/admin/category?id=" + categoryId);
        else
            return new RedirectResponse("/admin/course?id=" + courseId);
    }
    
    @Override
    public Class<DeleteTaskRequest> getRequestType() {
        return DeleteTaskRequest.class;
    }
    
}
