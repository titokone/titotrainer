package fi.helsinki.cs.titotrainer.app.admin.controller;

import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.app.admin.request.UpdateTaskRequest;
import fi.helsinki.cs.titotrainer.app.model.Task;
import fi.helsinki.cs.titotrainer.app.session.Messenger;
import fi.helsinki.cs.titotrainer.framework.i18n.Translator;
import fi.helsinki.cs.titotrainer.framework.response.ErrorResponse;
import fi.helsinki.cs.titotrainer.framework.response.RedirectResponse;
import fi.helsinki.cs.titotrainer.framework.response.Response;

public class UpdateTaskController extends AbstractTaskModificationController<UpdateTaskRequest> {
    
    @Override
    protected Response handleValid(UpdateTaskRequest req, Session hs) throws Exception {
        Task task = (Task)hs.get(Task.class, req.taskId);
        if (task == null)
            return new ErrorResponse(404, "Task " + req.taskId + " not found");
        
        this.saveOrUpdateTask(hs, req, task.getCourse(), task);
        
        Translator tr = this.getTranslator(req);
        req.getUserSession().getMessenger().appendMessage(Messenger.GLOBAL_SUCCESS_CATEGORY, tr.tr("task_updated"));
        return new RedirectResponse("/admin/task?id=" + req.taskId);
    }
    
    @Override
    public Class<UpdateTaskRequest> getRequestType() {
        return UpdateTaskRequest.class;
    }
    
}
