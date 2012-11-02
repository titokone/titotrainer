package fi.helsinki.cs.titotrainer.app.admin.request;

import static org.junit.Assert.*;

import org.junit.Test;

public class TaskRequestTest {
    
    @Test
    public void shouldNotValidateIfTaskIdAndCourseIdMissing() {
        TaskRequest req = new TaskRequest();
        req.id = null;
        req.courseId = null;
        assertFalse(req.validate().isEmpty());
    }
    
}
