package fi.helsinki.cs.titotrainer.app.admin.controller;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;

import fi.helsinki.cs.titotrainer.app.access.TitoBaseRole;
import fi.helsinki.cs.titotrainer.app.admin.request.CreateTaskRequest;
import fi.helsinki.cs.titotrainer.app.model.Category;
import fi.helsinki.cs.titotrainer.app.model.Course;
import fi.helsinki.cs.titotrainer.app.model.Task;
import fi.helsinki.cs.titotrainer.framework.response.ErrorResponse;
import fi.helsinki.cs.titotrainer.framework.response.RedirectResponse;
import fi.helsinki.cs.titotrainer.framework.response.Response;
import fi.helsinki.cs.titotrainer.testsupport.app.controller.ControllerTestCase;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleCategories;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleCourses;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleUsers;

public class CreateTaskControllerTest extends ControllerTestCase<CreateTaskRequest, CreateTaskController> {
    
    private Session hs;
    private SampleUsers sampleUsers;
    private SampleCategories sampleCategories;
    private SampleCourses sampleCourses;
    
    private Course emptyCourse;
    private Category defaultCategory; // Added to emptyCourse in setUp()
    
    @Override
    protected TitoBaseRole getModelAccessCheckerRole() {
        return TitoBaseRole.ADMINISTRATOR;
    }
    
    @Before
    public void setUp() {
        this.hs = this.openAutoclosedSession();
        
        this.sampleUsers = new SampleUsers(this.hs);
        
        this.sampleCategories = new SampleCategories(this.hs);
        this.sampleCourses = this.sampleCategories.courses;
        
        this.emptyCourse = this.sampleCourses.emptyCourse;
        this.defaultCategory = new Category();
        this.defaultCategory.setName(Locale.ENGLISH, "Empty category");
        this.defaultCategory.setCourse(this.emptyCourse);
        this.emptyCourse.getCategories().add(this.defaultCategory);
        this.hs.save(this.defaultCategory);
        this.hs.update(this.emptyCourse);
        
        this.createRequest(this.sampleUsers.editor);
        
        request.courseId = this.emptyCourse.getId();
        request.categoryId = this.defaultCategory.getId();
        request.title = new HashMap<String, String>();
        request.title.put("en", "Test task");
        request.title.put("fi", "Testitehtävä");
        request.description = new HashMap<String, String>();
        request.description.put("en", "This is a test task");
        request.hidden = false;
        request.difficulty = 123;
        request.maxSteps = 10000;
        request.type = Task.Type.PROGRAMMING.toString();
        request.preCode = null;
        request.postCode = "SVC SP,=HALT";
        
        request.modelSolution = "SVC SP,=HALT";
        
        
        request.input = new HashMap<String, String>();
        request.inputSecret = new HashMap<String, String>();
        
        
        request.criterionType = new HashMap<String, String>();
        
        request.acceptMsg = new HashMap<String, Map<String, String>>();
        request.rejectMsg = new HashMap<String, Map<String, String>>();
        
        request.inputId = new HashMap<String, String>();
        
        request.leftParam = new HashMap<String, String>();
        request.relation = new HashMap<String, String>();
        request.rightParam = new HashMap<String, String>();
    }
    
    private Task getCreatedTask() {
        hs.refresh(this.defaultCategory);
        assertEquals(1, this.defaultCategory.getTasks().size());
        return this.defaultCategory.getTasks().iterator().next();
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void shouldCreateNewTask() throws Exception {
        long before = new Date().getTime();
        Thread.sleep(2);
        Response resp = this.controller.handle(request);
        Thread.sleep(2);
        long after = new Date().getTime();
        
        assertThat(resp, instanceOf(RedirectResponse.class));
        
        Task task = getCreatedTask();
        assertEquals("Test task", task.getTitle(ENGLISH));
        assertEquals("This is a test task", task.getDescription(ENGLISH));
        assertEquals("Testitehtävä", task.getTitle(FINNISH));
        assertNull(task.getDescription(FINNISH));
        
        assertTrue(task.hasCompleteTranslation(ENGLISH));
        assertFalse(task.hasCompleteTranslation(FINNISH));
        
        assertSame(this.defaultCategory, task.getCategory());
        assertSame(Task.Type.PROGRAMMING, task.getType());
        
        assertEquals(false, task.getHidden());
        assertEquals(123, task.getDifficulty());
        assertEquals(10000, task.getMaxSteps());
        
        assertEquals(request.preCode, task.getPreCode());
        assertEquals(request.postCode, task.getPostCode());
        assertEquals(request.modelSolution, task.getModelSolution());
        
        assertThat(task.getCreationTime().getTime(), allOf(greaterThanOrEqualTo(before), lessThanOrEqualTo(after)));
        assertEquals(this.sampleUsers.editor, task.getCreator());
    }
    
    @Test
    public void shouldReturn404OnInvalidCourseId() throws Exception {
        request.courseId = Long.MAX_VALUE;
        
        Response resp = this.controller.handle(request);
        assertThat(resp, instanceOf(ErrorResponse.class));
        assertThat(resp, hasProperty("statusCode", equalTo(404)));
    }
    
    @Test
    public void shouldReturn404OnInvalidCategoryId() throws Exception {
        request.categoryId = Long.MAX_VALUE;
        
        Response resp = this.controller.handle(request);
        assertThat(resp, instanceOf(ErrorResponse.class));
        assertThat(resp, hasProperty("statusCode", equalTo(404)));
    }
    
    @Test
    public void shouldReturn404IfCategoryNotInCourse() throws Exception {
        request.categoryId = this.sampleCategories.beginnersTasks.getId();
        
        Response resp = this.controller.handle(request);
        assertThat(resp, instanceOf(ErrorResponse.class));
        assertThat(resp, hasProperty("statusCode", equalTo(404)));
    }
    
    @Test
    public void shouldAcceptNullCategory() throws Exception {
        request.categoryId = null;
        
        Response resp = this.controller.handle(request);
        assertThat(resp, instanceOf(RedirectResponse.class));
    }
    
    @Test
    public void shouldConvertWhitespaceOnlyToNullInCodeFields() throws Exception {
        request.preCode = "   ";
        request.modelSolution = "         ";
        request.postCode = "\n  \t";
        
        Response resp = this.controller.handle(request);
        assertThat(resp, instanceOf(RedirectResponse.class));
        
        Task task = getCreatedTask();
        assertNull(task.getPreCode());
        assertNull(task.getModelSolution());
        assertNull(task.getPostCode());
    }

    @Override
    protected Class<CreateTaskController> getControllerType() {
        return CreateTaskController.class;
    }
}
