package fi.helsinki.cs.titotrainer.app.student.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.app.model.Answer;
import fi.helsinki.cs.titotrainer.app.model.Course;
import fi.helsinki.cs.titotrainer.app.model.Task;
import fi.helsinki.cs.titotrainer.app.model.User;
import fi.helsinki.cs.titotrainer.app.request.TitoRequest;
import fi.helsinki.cs.titotrainer.app.view.TitoPageView;
import fi.helsinki.cs.titotrainer.framework.response.ViewResponse;
import fi.helsinki.cs.titotrainer.framework.view.template.TemplateRenderer;

public class TaskListView extends TitoPageView<TitoRequest> {
    
    public Class<TitoRequest> getRequestType() {
        return TitoRequest.class;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected void handle(TitoRequest req, Session hs, TemplateRenderer tr, ViewResponse resp) {
        Course course = null;
        User user = req.getUserSession().getAuthenticatedUser();
        
        if (user.getCourseId() != null)
            course = (Course)hs.get(Course.class, req.getUserSession().getAuthenticatedUser().getCourseId());
        if (course == null)
            throw new RuntimeException("Student has no associated course.");
        
        List<Task> tasks = hs.createQuery("FROM Task WHERE hidden = FALSE AND course.id = ?")
                             .setLong(0, course.getId())
                             .list();
        
        Map<Long, Answer> taskAnswers = new HashMap<Long, Answer>();
        
        for (Task task : tasks) {
            // Make sure the necessary lazy data is loaded so the template can call hasCompleteTranslation()
            task.hasCompleteTranslation(this.getTranslator(req).getLocale());
            
            // See if the task has been solved by the student.
            Answer answer = (Answer)hs.createQuery("FROM Answer WHERE task.id = ? AND user.id = ?")
                                      .setLong(0, task.getId())
                                      .setLong(1, user.getId())
                                      .uniqueResult();
            if (answer != null) {
                answer.isSuccessful(); // Load lazy stuff needed to evaluate this
                taskAnswers.put(task.getId(), answer);
            }
        }
        
        tr.put("tasks", tasks);
        tr.put("taskAnswers", taskAnswers);
    }
    
    @Override
    protected String getTemplateName() {
        return "student/tasklist.vm";
    }
    
}
