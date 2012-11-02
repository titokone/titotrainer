package fi.helsinki.cs.titotrainer.app.admin.view;

import static org.junit.Assert.*;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;

import fi.helsinki.cs.titotrainer.app.admin.request.CourseRequest;
import fi.helsinki.cs.titotrainer.framework.response.ErrorResponseException;
import fi.helsinki.cs.titotrainer.framework.view.template.TemplateRenderer;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleCourses;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleTasks;
import fi.helsinki.cs.titotrainer.testsupport.app.view.ViewTestCase;

public class CourseViewTest extends ViewTestCase<CourseRequest, CourseView> {
    
    private Session hs;
    private SampleCourses sampleCourses;
    private SampleTasks sampleTasks;
    
    @Before
    public void setUp() {
        this.hs = this.openAutoclosedSession();
        this.sampleTasks = new SampleTasks(hs);
        this.sampleCourses = this.sampleTasks.courses;
    }
    
    @Override
    protected Class<CourseView> getViewType() {
        return CourseView.class;
    }

    @Test
    public void shouldGiveCourseObjectToTemplate() throws Exception {
        request.id = sampleCourses.autumnCourse.getId();
        TemplateRenderer tr = this.callTemplateBasedView();
        assertEquals(sampleCourses.autumnCourse, tr.get("course"));
    }
    
    @Test(expected = ErrorResponseException.class)
    public void shouldReturn404OnInvalidCourseId() throws Exception {
        request.id = 0l;
        try {
            this.callTemplateBasedView();
        } catch (ErrorResponseException er) {
            assertEquals(404, er.getErrorResponse().getStatusCode());
            throw er;
        }
    }
    
    @Test
    public void shouldShowCourseCreationPageIfNoCourseIdGiven() throws Exception {
        request.id = null;
        this.callTemplateBasedView();
    }
    
}
