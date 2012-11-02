package fi.helsinki.cs.titotrainer.testsupport.app.model.sample;

import java.util.Locale;

import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.app.model.Course;
import fi.helsinki.cs.titotrainer.app.model.TString;

public class SampleCourses {
    
    /////////////////
    // SAMPLE DATA //
    /////////////////

    private static final String SELF_STUDY_COURSE_NAME_EN = "Self Study Course";
    private static final String AUTUMN_COURSE_NAME_EN     = "Computer Systems Organization - Fall 2008";
    private static final String EMPTY_COURSE_NAME_EN      = "Empty test course";
    
    public static final int NUM_ENTRIES = 3;
    
    public final Course autumnCourse;
    public final Course selfStudyCourse;
    public final Course emptyCourse;
    
    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public SampleCourses(Session session) {
        session.save(this.autumnCourse = SampleCourses.createAutumnCourse());
        session.save(this.selfStudyCourse = SampleCourses.createSelfStudyCourse());
        session.save(this.emptyCourse = SampleCourses.createEmptyCourse());
        session.flush();
    }

    /////////////////////
    // FACTORY METHODS //
    /////////////////////
    
    public static Course createAutumnCourse() {
        Course course;
        TString name = new TString(Locale.ENGLISH, SampleCourses.AUTUMN_COURSE_NAME_EN);
        course = new Course(name);
        return course;
    }

    public static Course createSelfStudyCourse() {
        Course course;
        TString name = new TString(Locale.ENGLISH, SampleCourses.SELF_STUDY_COURSE_NAME_EN);
        course = new Course(name);
        return course;
    }
    
    public static Course createEmptyCourse() {
        Course course;
        TString name = new TString(Locale.ENGLISH, SampleCourses.EMPTY_COURSE_NAME_EN);
        course = new Course(name);
        return course;
    }

}