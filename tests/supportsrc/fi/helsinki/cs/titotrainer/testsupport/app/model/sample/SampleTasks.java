package fi.helsinki.cs.titotrainer.testsupport.app.model.sample;

import java.util.GregorianCalendar;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.app.model.Category;
import fi.helsinki.cs.titotrainer.app.model.Course;
import fi.helsinki.cs.titotrainer.app.model.Criterion;
import fi.helsinki.cs.titotrainer.app.model.Input;
import fi.helsinki.cs.titotrainer.app.model.Task;
import fi.helsinki.cs.titotrainer.app.model.User;
import fi.helsinki.cs.titotrainer.app.model.criteria.ModelScreenOutputCriterion;

public class SampleTasks {
    
    /////////////////
    // SAMPLE DATA //
    /////////////////

    public static final String MINIMAL_TASK_DESCRIPTION_EN = "This is a minimal task for\ntesting purposes only.";
    public static final String MINIMAL_TASK_TITLE_EN       = "A minimal task";
    
    public static final String STANDARD_TASK_DESCRIPTION_EN = "A quite minimal task\nused in testing.";
    public static final String STANDARD_TASK_TITLE_EN       = "A standard task";
    
    public static final String SUM_TASK_DESCRIPTION_EN      = "Read two inputs,\nadd them together,\nwrite the result as output.";
    public static final String SUM_TASK_TITLE_EN            = "Sum";
    
    public static final int NUM_ENTRIES = 3;
    
    public final SampleCategories categories;
    public final SampleCourses    courses;
    public final SampleUsers      users;
            
    public final Task minimalTask;
    public final Task standardTask;
    public final Task sumTask;

    //////////////////
    // CONSTRUCTORS //
    //////////////////
    
    public SampleTasks(Session session) {
        this.categories = new SampleCategories(session);
        this.users = new SampleUsers(session);
        this.courses = this.categories.courses;
        session.save(this.minimalTask = SampleTasks.createMinimalTask(this.categories.beginnersTasks, this.users.editor, this.courses.autumnCourse, Task.Type.FILL_IN));
        session.save(this.standardTask = SampleTasks.createStandardTask(this.categories.intermediateTasks, this.users.editor, this.courses.selfStudyCourse, Task.Type.PROGRAMMING));
        session.save(this.sumTask = SampleTasks.createSumTask(this.categories.intermediateTasks, this.users.editor, this.courses.selfStudyCourse, Task.Type.PROGRAMMING));
        session.flush();
    }

    /////////////////////
    // FACTORY METHODS //
    /////////////////////
    
    public static Task createMinimalTask(Category category, User creator, Course course, Task.Type type) {
        Task task;
        task = new Task(category, creator, course);
        category.getTasks().add(task);
        course.getTasks().add(task);
        task.setTitle(Locale.ENGLISH, SampleTasks.MINIMAL_TASK_TITLE_EN);
        task.setDescription(Locale.ENGLISH, SampleTasks.MINIMAL_TASK_DESCRIPTION_EN);
        task.setType(type);
        return task;
    }
    
    public static Task createStandardTask(Category category, User creator, Course course, Task.Type type) {
        Task task;
        task = new Task(category, creator, course);
        category.getTasks().add(task);
        course.getTasks().add(task);
        task.setTitle(Locale.ENGLISH, SampleTasks.STANDARD_TASK_TITLE_EN);
        task.setDescription(Locale.ENGLISH, SampleTasks.STANDARD_TASK_DESCRIPTION_EN);
        task.setType(type);
        return task;
    }
    
    public static Task createSumTask(Category category, User creator, Course course, Task.Type type) {
        Task task = new Task();
        task.setTitle(Locale.ENGLISH, "Compute a sum");
        task.setType(type);
        task.setDescription(Locale.ENGLISH, "Write a program that reads two integers as input and writes their sum as output.");
        task.setDifficulty(110);
        task.setCourse(course);
        course.getTasks().add(task);
        task.setCategory(category);
        category.getTasks().add(task);
        task.setCreator(creator);
        task.setCreationTime(new GregorianCalendar(2008, 11, 7, 12, 42, 9).getTime());
        
        task.setModelSolution(StringUtils.join(
            new String[] {
                "IN R1,=KBD",
                "IN R2,=KBD",
                "ADD R1,R2",
                "OUT R1,=CRT",
                "SVC SP,=HALT"
            }, '\n'));
        
        {
            Criterion c = new ModelScreenOutputCriterion();
            c.setAcceptMessage(Locale.ENGLISH, "The output was correct.");
            c.setRejectMessage(Locale.ENGLISH, "The output was incorrect.");
            c.setTask(task);
            task.getCriteria().add(c);
        }
        
        {
            task.getInputs().add(new Input(task, "3,5", false));
            task.getInputs().add(new Input(task, "-9,55", false));
            task.getInputs().add(new Input(task, "1,7", false));
        }
        return task;
    }
    
}