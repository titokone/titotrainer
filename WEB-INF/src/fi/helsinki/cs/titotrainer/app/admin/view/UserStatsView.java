package fi.helsinki.cs.titotrainer.app.admin.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.app.admin.misc.AdminStatsViewUtils;
import fi.helsinki.cs.titotrainer.app.admin.misc.AdminStatsViewUtils.UserStats;
import fi.helsinki.cs.titotrainer.app.admin.request.UserStatsViewRequest;
import fi.helsinki.cs.titotrainer.app.model.Answer;
import fi.helsinki.cs.titotrainer.app.model.Course;
import fi.helsinki.cs.titotrainer.app.model.Task;
import fi.helsinki.cs.titotrainer.app.model.User;
import fi.helsinki.cs.titotrainer.app.view.TitoPageView;
import fi.helsinki.cs.titotrainer.framework.response.ErrorResponseException;
import fi.helsinki.cs.titotrainer.framework.response.ViewResponse;
import fi.helsinki.cs.titotrainer.framework.view.template.TemplateRenderer;

public class UserStatsView extends TitoPageView<UserStatsViewRequest> {
    
    @Override
    protected String getTemplateName() {
        return "/admin/userstats.vm";
    }
    
    @Override
    protected void handle(UserStatsViewRequest req, Session hs, TemplateRenderer tr, ViewResponse resp) throws ErrorResponseException {
        Course course = (Course)hs.get(Course.class, req.courseId);
        if (course == null)
            throw new ErrorResponseException(404, "Course " + req.courseId);
        tr.put("course", course);
        
        User user = (User)hs.get(User.class, req.userId);
        if (user == null)
            throw new ErrorResponseException(404, "User " + req.userId);
        tr.put("theUser", user);
        
        AdminStatsViewUtils utils = new AdminStatsViewUtils(hs);
        
        List<Task> tasks = utils.getVisibleTasks(course.getId());
        tr.put("tasks", tasks);
        tr.put("totalTasks", tasks.size());
        
        UserStats stats = utils.getUserStats(user.getId(), course.getId(), null);
        tr.put("answers", stats.answers);
        tr.put("tasksAttempted", stats.tasksAttempted);
        tr.put("tasksSolved", stats.tasksSolved);
        
        Map<Long, Answer> taskAnswers = new HashMap<Long, Answer>();
        for (Answer answer : stats.answers) {
            taskAnswers.put(answer.getTask().getId(), answer);
        }
        tr.put("taskAnswers", taskAnswers);
    }
    
    @Override
    public Class<UserStatsViewRequest> getRequestType() {
        return UserStatsViewRequest.class;
    }
}
