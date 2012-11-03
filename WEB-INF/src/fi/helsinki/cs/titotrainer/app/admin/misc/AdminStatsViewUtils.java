package fi.helsinki.cs.titotrainer.app.admin.misc;

import java.util.Collections;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import fi.helsinki.cs.titotrainer.app.model.Answer;
import fi.helsinki.cs.titotrainer.app.model.Task;

/**
 * Utility functions for the statistics views.
 */
public class AdminStatsViewUtils {
    
    private Session hs;
    
    public AdminStatsViewUtils(Session hs) {
        this.hs = hs;
    }
    
    @SuppressWarnings("unchecked")
    public List<Task> getVisibleTasks(long courseId) {
        return hs.createQuery("FROM Task WHERE course.id = ? AND hidden = FALSE")
                 .setLong(0, courseId)
                 .list();
    }
    
    public long getVisibleTaskCount(long courseId, Long[] taskIdsOrNull) {
        Criteria c = hs.createCriteria(Task.class)
            .setProjection(Projections.rowCount())
            .createAlias("course", "c")
            .add(Restrictions.eq("c.id", courseId))
            .add(Restrictions.eq("hidden", false));
        
        if (taskIdsOrNull != null) {
            if (taskIdsOrNull.length > 0) {
                c = c.add(Restrictions.in("id", taskIdsOrNull));
            } else {
                c = c.add(Restrictions.sqlRestriction("FALSE"));
            }
        }
            
        return (Integer)c.uniqueResult();
    }
    
    public static class UserStats {
        public final List<Answer> answers;
        public final int tasksAttempted;
        public final int tasksSolved;
        
        UserStats(List<Answer> answers) {
            this.answers = Collections.unmodifiableList(answers);
            this.tasksAttempted = answers.size();
            int solved = 0;
            for (Answer ans : answers) {
                if (ans.isSuccessful()) {
                    ++solved;
                }
            }
            this.tasksSolved = solved;
        }
    }
    
    @SuppressWarnings("unchecked")
    public UserStats getUserStats(long userId, long courseId, Long[] taskIdsOrNull) {
        Criteria c = hs.createCriteria(Answer.class)
            .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
            .createAlias("task", "t")
            .createAlias("user", "u")
            .add(Restrictions.eq("t.course.id", courseId))
            .add(Restrictions.eq("u.id", userId))
            .add(Restrictions.eq("t.hidden", false));
        if (taskIdsOrNull != null) {
            if (taskIdsOrNull.length > 0) {
                c = c.add(Restrictions.in("t.id", taskIdsOrNull));
            } else {
                c = c.add(Restrictions.sqlRestriction("FALSE"));
            }
        }
        
        List<Answer> answers = c.list();
        return new UserStats(answers);
    }
    
}
