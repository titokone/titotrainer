package fi.helsinki.cs.titotrainer.app.admin.controller;

import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.app.admin.request.DeleteCourseRequest;
import fi.helsinki.cs.titotrainer.app.controller.TitoActionController;
import fi.helsinki.cs.titotrainer.app.model.Category;
import fi.helsinki.cs.titotrainer.app.model.Course;
import fi.helsinki.cs.titotrainer.app.model.Task;
import fi.helsinki.cs.titotrainer.app.session.Messenger;
import fi.helsinki.cs.titotrainer.framework.response.ErrorResponse;
import fi.helsinki.cs.titotrainer.framework.response.RedirectResponse;
import fi.helsinki.cs.titotrainer.framework.response.Response;

public class DeleteCourseController extends TitoActionController<DeleteCourseRequest> {

    // TODO: unit test
    
    @Override
    protected Response handleValid(DeleteCourseRequest req, Session hs) throws Exception {
        
        Course course = (Course)hs.get(Course.class, req.id);
        if (course == null)
            return new ErrorResponse(404);
        
        // There are no cascades on tasks and categories, so we delete them by hand
        for (Task t : course.getTasks()) {
            hs.delete(t);
        }
        for (Category c : course.getCategories()) {
            hs.delete(c);
        }
        
        hs.delete(course);
        
        req.getUserSession().getMessenger().appendMessage(Messenger.GLOBAL_SUCCESS_CATEGORY,
                                                          this.getTranslator(req).tr("course_deleted"));
        
        return new RedirectResponse("/admin/courselist");
    }

    @Override
    public Class<DeleteCourseRequest> getRequestType() {
        return DeleteCourseRequest.class;
    }
    
}
