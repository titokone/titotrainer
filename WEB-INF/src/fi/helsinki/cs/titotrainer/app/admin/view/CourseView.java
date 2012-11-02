package fi.helsinki.cs.titotrainer.app.admin.view;

import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.app.admin.request.CourseRequest;
import fi.helsinki.cs.titotrainer.app.admin.request.TaskListFragmentViewRequest;
import fi.helsinki.cs.titotrainer.app.model.Course;
import fi.helsinki.cs.titotrainer.app.view.TitoPageView;
import fi.helsinki.cs.titotrainer.framework.response.ErrorResponseException;
import fi.helsinki.cs.titotrainer.framework.response.ViewResponse;
import fi.helsinki.cs.titotrainer.framework.view.template.TemplateRenderer;

public class CourseView extends TitoPageView<CourseRequest> {
    
    private TaskListFragmentView taskListFragmentView;
    
    public CourseView() {
        this.taskListFragmentView = new TaskListFragmentView();
    }
    
    @Override
    protected void handle(CourseRequest req, Session hs, TemplateRenderer tr, ViewResponse resp) throws ErrorResponseException {
        Course course;
        
        boolean newCourse = (req.id == null);
        if (newCourse) {
            course = new Course();
        } else {
            course = (Course)hs.get(Course.class, req.id);
            
            if (course == null) {
                throw new ErrorResponseException(404);
            }
        }
        
        loadLazyCollection(course.getTasks());
        loadLazyCollection(course.getCategories());
        
        tr.put("course", course);
        tr.put("newCourse", newCourse);
        
        tr.put("supportedLocales", req.getContext().getTitoTranslation().getSupportedLocales());
        
        
        TaskListFragmentViewRequest taskListReq = new TaskListFragmentViewRequest();
        try {
            req.copyTo(taskListReq);
        } catch (Exception e) {
            throw new ErrorResponseException(500, e);
        }
        taskListReq.courseId = course.getId();
        
        tr.put("taskListFragment", renderFragment(taskListFragmentView, taskListReq));
    }
    
    @Override
    public Class<CourseRequest> getRequestType() {
        return CourseRequest.class;
    }
    
    @Override
    protected String getTemplateName() {
        return "admin/course.vm";
    }
}
