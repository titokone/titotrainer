package fi.helsinki.cs.titotrainer.app.admin.misc;

import java.util.List;

import org.hibernate.Session;

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
    
    public long getVisibleTaskCount(long courseId) {
        return (Long)hs.createQuery("SELECT COUNT(*) FROM Task WHERE course.id = ? AND hidden = FALSE")
                       .setLong(0, courseId)
                       .uniqueResult();
    }
    
    public static class UserStats {
        public List<Answer> answers;
        public int tasksAttempted;
        public int tasksSolved;
        
        UserStats(List<Answer> answers) {
            this.answers = answers;
            this.tasksAttempted = answers.size();
            this.tasksSolved = 0;
            for (Answer ans : answers) {
                if (ans.isSuccessful())
                    ++this.tasksSolved;
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    public UserStats getUserStats(long userId, long courseId) {
        List<Answer> answers = hs.createQuery("FROM Answer WHERE task.course.id = ? AND user.id = ? AND task.hidden = FALSE")
                                 .setLong(0, courseId)
                                 .setLong(1, userId)
                                 .list();

        return new UserStats(answers);
    }
    
}
