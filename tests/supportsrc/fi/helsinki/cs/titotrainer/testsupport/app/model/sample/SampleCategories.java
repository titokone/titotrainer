package fi.helsinki.cs.titotrainer.testsupport.app.model.sample;

import java.util.Locale;

import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.app.model.Category;
import fi.helsinki.cs.titotrainer.app.model.Course;
import fi.helsinki.cs.titotrainer.app.model.TString;

public class SampleCategories {

    /////////////////
    // SAMPLE DATA //
    /////////////////
    
    public static final String BEGINNERS_TASKS_NAME_EN    = "Beginner's Tasks";
    public static final String INTERMEDIATE_TASKS_NAME_EN = "Intermediate Tasks";
    public static final String TRICKY_TASKS_NAME_EN       = "Tricky Tasks";
    
    public SampleCourses courses;
    
    public final Category beginnersTasks;
    public final Category intermediateTasks;
    public final Category trickyTasks;
        
    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public SampleCategories(Session session) {
        this.courses = new SampleCourses(session);
        session.save(this.beginnersTasks = SampleCategories.createBeginnersTasks(this.courses.autumnCourse));
        session.save(this.intermediateTasks = SampleCategories.createIntermediateTasks(this.courses.selfStudyCourse));
        session.save(this.trickyTasks = SampleCategories.createTrickyTasks(this.courses.selfStudyCourse));
        session.flush();
    }
    
    public static Category createBeginnersTasks(Course course) {
        Category category;
        TString name = new TString(Locale.ENGLISH, SampleCategories.BEGINNERS_TASKS_NAME_EN);
        category = new Category(name, course);
        course.getCategories().add(category);
        return category;
    }
    
    public static Category createIntermediateTasks(Course course) {
        Category category;
        TString name = new TString(Locale.ENGLISH, SampleCategories.INTERMEDIATE_TASKS_NAME_EN);
        category = new Category(name, course);
        course.getCategories().add(category);
        return category;
    }
    
    public static Category createTrickyTasks(Course course) {
        Category category;
        TString name = new TString(Locale.ENGLISH, SampleCategories.TRICKY_TASKS_NAME_EN);
        category = new Category(name, course);
        course.getCategories().add(category);
        return category;        
    }
    
}