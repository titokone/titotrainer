package fi.helsinki.cs.titotrainer.app.admin.controller;

import java.util.Date;

import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.app.admin.request.CopyTaskRequest;
import fi.helsinki.cs.titotrainer.app.controller.TitoActionController;
import fi.helsinki.cs.titotrainer.app.model.Task;
import fi.helsinki.cs.titotrainer.app.session.Messenger;
import fi.helsinki.cs.titotrainer.framework.response.ErrorResponse;
import fi.helsinki.cs.titotrainer.framework.response.RedirectResponse;
import fi.helsinki.cs.titotrainer.framework.response.Response;

public class CopyTaskController extends TitoActionController<CopyTaskRequest> {
    
    @Override
    protected Response handleValid(CopyTaskRequest req, Session hs) throws Exception {
        Task oldTask = (Task)hs.get(Task.class, req.id);
        if (oldTask == null)
            return new ErrorResponse(404);
        
        Task newTask = oldTask.deepCopy();
        newTask.setCreationTime(new Date());
        hs.save(newTask);
        hs.flush();
        
        req.getUserSession().getMessenger().appendMessage(Messenger.GLOBAL_SUCCESS_CATEGORY,
                                                          this.getTranslator(req).tr("task_copied"));
        
        return new RedirectResponse("/admin/task?id=" + newTask.getId());
    }
    
    @Override
    public Class<CopyTaskRequest> getRequestType() {
        return CopyTaskRequest.class;
    }
    
}
