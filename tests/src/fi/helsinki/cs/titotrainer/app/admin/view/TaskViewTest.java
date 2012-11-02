package fi.helsinki.cs.titotrainer.app.admin.view;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Collection;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;

import fi.helsinki.cs.titotrainer.app.admin.request.TaskRequest;
import fi.helsinki.cs.titotrainer.app.model.Category;
import fi.helsinki.cs.titotrainer.app.model.Course;
import fi.helsinki.cs.titotrainer.app.model.Criterion;
import fi.helsinki.cs.titotrainer.app.model.Task;
import fi.helsinki.cs.titotrainer.app.model.titokone.TitokoneState;
import fi.helsinki.cs.titotrainer.framework.response.ErrorResponseException;
import fi.helsinki.cs.titotrainer.framework.view.template.TemplateRenderer;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleCourses;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleTasks;
import fi.helsinki.cs.titotrainer.testsupport.app.view.ViewTestCase;

public class TaskViewTest extends ViewTestCase<TaskRequest, TaskView> {
    
    private Session hs;
    private SampleTasks sampleTasks;
    private SampleCourses sampleCourses;
    
    @Before
    public void setUp() {
        this.hs = openAutoclosedSession();
        this.sampleTasks = new SampleTasks(hs);
        this.sampleCourses = this.sampleTasks.courses;
    }
    
    @Override
    protected Class<TaskView> getViewType() {
        return TaskView.class;
    }
    
    @Test
    public void shouldGiveTaskObjectToTemplate() throws Exception {
        request.id = sampleTasks.minimalTask.getId();
        TemplateRenderer tr = callTemplateBasedView();
        assertThat(tr.get("task"), instanceOf(Task.class));
    }
    
    @Test
    public void shouldGiveEmptyTaskObjectToTemplateIfCreatingNewTask() throws Exception {
        request.id = null;
        request.courseId = sampleCourses.autumnCourse.getId();
        TemplateRenderer tr = callTemplateBasedView();
        
        assertThat(tr.get("task"), instanceOf(Task.class));
    }
    
    @Test
    public void shouldAlwaysGiveCourseObject() throws Exception {
        request.id = null;
        request.courseId = sampleCourses.autumnCourse.getId();
        TemplateRenderer tr = callTemplateBasedView();
        
        assertThat(tr.get("course"), instanceOf(Course.class));
    }
    
    @Test
    public void formObjectShouldHaveEagerlyFetchedCriteria() throws Exception {
        request.id = sampleTasks.minimalTask.getId();
        TemplateRenderer tr = callTemplateBasedView();
        Task form = (Task)tr.get("task");
        for (Criterion c : form.getCriteria()) {
            assertNotNull(c);
        }
    }
    
    @Test
    public void shouldGiveTaskTypeClassToTemplate() throws Exception {
        request.id = sampleTasks.minimalTask.getId();
        TemplateRenderer tr = callTemplateBasedView();
        assertThat(tr.get("TaskType"), instanceOf(Class.class));
        Class<?> taskType = (Class<?>)tr.get("TaskType");
        assertSame(Task.Type.class, taskType);
    }
    
    @Test
    public void shouldGiveTitokoneStateClassToTemplate() throws Exception {
        request.id = sampleTasks.minimalTask.getId();
        TemplateRenderer tr = callTemplateBasedView();
        assertThat(tr.get("TitokoneState"), instanceOf(Class.class));
        Class<?> taskType = (Class<?>)tr.get("TitokoneState");
        assertSame(TitokoneState.class, taskType);
    }
    
    @Test
    public void shouldGiveAvailableCategoriesFromSelectedCourseToTemplate() throws Exception {
        request.courseId = sampleCourses.selfStudyCourse.getId();
        assert(!sampleCourses.selfStudyCourse.getCategories().isEmpty());
        
        TemplateRenderer tr = callTemplateBasedView();
        Collection<?> cats = (Collection<?>)tr.get("availableCategories");
        
        assertEquals(sampleCourses.selfStudyCourse.getCategories().size(), cats.size());
        for (Category cat : sampleCourses.selfStudyCourse.getCategories()) {
            assertTrue(cats.contains(cat));
        }
        
        // Try another course with no categories
        request.courseId = sampleCourses.emptyCourse.getId();
        assert(sampleCourses.emptyCourse.getCategories().isEmpty());
        
        tr = callTemplateBasedView();
        cats = (Collection<?>)tr.get("availableCategories");
        
        assertTrue(cats.isEmpty());
    }
    
    @Test(expected = ErrorResponseException.class)
    public void shouldReturn404OnInvalidTaskId() throws Exception {
        request.id = Long.MAX_VALUE;
        try {
            callTemplateBasedView();
        } catch (ErrorResponseException er) {
            assertEquals(404, er.getErrorResponse().getStatusCode());
            throw er;
        }
    }

    @Test(expected = ErrorResponseException.class)
    public void shouldReturn404OnInvalidCourseId() throws Exception {
        request.courseId = Long.MAX_VALUE;
        try {
            callTemplateBasedView();
        } catch (ErrorResponseException er) {
            assertEquals(404, er.getErrorResponse().getStatusCode());
            throw er;
        }
    }
    
}
