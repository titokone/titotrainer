package fi.helsinki.cs.titotrainer.app.admin.controller;

import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.app.admin.request.CreateTaskRequest;
import fi.helsinki.cs.titotrainer.app.model.Course;
import fi.helsinki.cs.titotrainer.app.model.Task;
import fi.helsinki.cs.titotrainer.app.session.Messenger;
import fi.helsinki.cs.titotrainer.framework.i18n.Translator;
import fi.helsinki.cs.titotrainer.framework.response.ErrorResponse;
import fi.helsinki.cs.titotrainer.framework.response.RedirectResponse;
import fi.helsinki.cs.titotrainer.framework.response.Response;

public class CreateTaskController extends AbstractTaskModificationController<CreateTaskRequest> {

    @Override
    protected Response handleValid(CreateTaskRequest req, Session hs) throws Exception {
        
        Course course = (Course)hs.get(Course.class, req.courseId);
        
        if (course == null) {
            return new ErrorResponse(404, "Course " + req.courseId + " not found");
        }
        
        Task task = new Task();
        
        course.getTasks().add(task);
        task.setCourse(course);
        
        task.setCreator(req.getUserSession().getAuthenticatedUser());
        
        this.saveOrUpdateTask(hs, req, course, task);
        
        Translator tr = this.getTranslator(req);
        req.getUserSession().getMessenger().appendMessage(Messenger.GLOBAL_SUCCESS_CATEGORY, tr.tr("task_created"));
        return new RedirectResponse("/admin/task?id=" + task.getId());
    }

    @Override
    public Class<CreateTaskRequest> getRequestType() {
        return CreateTaskRequest.class;
    }
    
}
