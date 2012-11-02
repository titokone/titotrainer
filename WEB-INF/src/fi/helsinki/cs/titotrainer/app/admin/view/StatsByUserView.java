package fi.helsinki.cs.titotrainer.app.admin.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.app.admin.misc.AdminStatsViewUtils;
import fi.helsinki.cs.titotrainer.app.admin.misc.AdminStatsViewUtils.UserStats;
import fi.helsinki.cs.titotrainer.app.admin.request.StatsByUserViewRequest;
import fi.helsinki.cs.titotrainer.app.model.Course;
import fi.helsinki.cs.titotrainer.app.model.User;
import fi.helsinki.cs.titotrainer.app.view.TitoPageView;
import fi.helsinki.cs.titotrainer.framework.response.ErrorResponseException;
import fi.helsinki.cs.titotrainer.framework.response.ViewResponse;
import fi.helsinki.cs.titotrainer.framework.view.template.TemplateRenderer;

public class StatsByUserView extends TitoPageView<StatsByUserViewRequest> {
    
    @Override
    protected String getTemplateName() {
        return "admin/statsbyuser.vm";
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected void handle(StatsByUserViewRequest req, Session hs, TemplateRenderer tr, ViewResponse resp) throws ErrorResponseException {
        Course course = (Course)hs.get(Course.class, req.courseId);
        if (course == null)
            throw new ErrorResponseException(404, "Course " + req.courseId);
        tr.put("course", course);
        
        List<User> users = hs.createQuery("FROM User WHERE courseId = ?")
                             .setLong(0, course.getId())
                             .list();
        tr.put("users", users);
        
        AdminStatsViewUtils utils = new AdminStatsViewUtils(hs);
        
        // Get total number of visible tasks
        tr.put("totalTasks", utils.getVisibleTaskCount(course.getId()));
        
        // Get number of solved tasks for each user
        Map<Long, Integer> tasksAttempted = new HashMap<Long, Integer>();
        Map<Long, Integer> tasksSolved = new HashMap<Long, Integer>();
        for (User user : users) {
            UserStats stats = utils.getUserStats(user.getId(), course.getId());
            tasksAttempted.put(user.getId(), stats.tasksAttempted);
            tasksSolved.put(user.getId(), stats.tasksSolved);
        }
        tr.put("tasksAttempted", tasksAttempted);
        tr.put("tasksSolved", tasksSolved);
    }
    
    @Override
    public Class<StatsByUserViewRequest> getRequestType() {
        return StatsByUserViewRequest.class;
    }
}
