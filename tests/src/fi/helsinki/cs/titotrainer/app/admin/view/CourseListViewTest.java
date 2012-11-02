package fi.helsinki.cs.titotrainer.app.admin.view;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.List;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;

import fi.helsinki.cs.titotrainer.app.request.TitoRequest;
import fi.helsinki.cs.titotrainer.framework.view.template.TemplateRenderer;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleCourses;
import fi.helsinki.cs.titotrainer.testsupport.app.view.ViewTestCase;

public class CourseListViewTest extends ViewTestCase<TitoRequest, CourseListView> {
    
    private Session hs;
    private SampleCourses sampleCourses;
    
    @Before
    public void setUp() {
        this.hs = openAutoclosedSession();
        this.sampleCourses = new SampleCourses(hs);
    }
    
    @Override
    protected Class<CourseListView> getViewType() {
        return CourseListView.class;
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void shouldShowAllCourses() throws Exception {
        TemplateRenderer tr = callTemplateBasedView();
        List<Object> courses = (List<Object>)tr.get("courses");
        assertThat(courses, hasItem(hasProperty("id", equalTo(sampleCourses.autumnCourse.getId()))));
        assertThat(courses, hasItem(hasProperty("id", equalTo(sampleCourses.selfStudyCourse.getId()))));
        assertEquals(SampleCourses.NUM_ENTRIES, courses.size());
    }

}
