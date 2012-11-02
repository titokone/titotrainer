package fi.helsinki.cs.titotrainer.app.admin.controller;

import static org.junit.Assert.*;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;

import fi.helsinki.cs.titotrainer.app.access.TitoBaseRole;
import fi.helsinki.cs.titotrainer.app.admin.request.DeleteCourseRequest;
import fi.helsinki.cs.titotrainer.app.model.Category;
import fi.helsinki.cs.titotrainer.app.model.Course;
import fi.helsinki.cs.titotrainer.app.model.Task;
import fi.helsinki.cs.titotrainer.testsupport.app.controller.ControllerTestCase;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleCategories;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleCourses;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleTasks;

public class DeleteCourseControllerTest extends ControllerTestCase<DeleteCourseRequest, DeleteCourseController> {

    private Session hs;
    
    @Before
    public void setUp() {
        this.hs = this.openAutoclosedSession();
    }
    
    @Override
    protected TitoBaseRole getModelAccessCheckerRole() {
        return TitoBaseRole.ADMINISTRATOR;
    }
    
    @Test
    public void shouldDeleteCourse() throws Exception {
        DeleteCourseRequest req = this.createRequest();
        req.id = new SampleCourses(hs).emptyCourse.getId();
        
        hs.close();
        this.controller.handle(req);
        hs = this.openAutoclosedSession();
        
        assertNull(hs.get(Course.class, req.id));
    }
    
    @Test
    public void shouldDeleteTasksOfCourse() throws Exception {
        DeleteCourseRequest req = this.createRequest();
        Task task = new SampleTasks(hs).minimalTask;
        
        long taskId = task.getId();
        req.id = task.getCourse().getId();
        
        hs.close();
        this.controller.handle(req);
        hs = this.openAutoclosedSession();
        
        assertNull(hs.get(Task.class, taskId));
        assertNull(hs.get(Course.class, req.id));
    }
    
    @Test
    public void shouldDeleteCategoriesOfCourse() throws Exception {
        DeleteCourseRequest req = this.createRequest();
        Category cat = new SampleCategories(hs).beginnersTasks;
        
        long catId = cat.getId();
        req.id = cat.getCourse().getId();
        
        hs.close();
        this.controller.handle(req);
        hs = this.openAutoclosedSession();
        
        assertNull(hs.get(Category.class, catId));
        assertNull(hs.get(Course.class, req.id));
    }
    
    @Override
    protected Class<DeleteCourseController> getControllerType() {
        return DeleteCourseController.class;
    }
}
