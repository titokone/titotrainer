package fi.helsinki.cs.titotrainer.app.admin.controller;

import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.app.admin.request.UpdateCourseRequest;
import fi.helsinki.cs.titotrainer.app.controller.TitoActionController;
import fi.helsinki.cs.titotrainer.app.model.Course;
import fi.helsinki.cs.titotrainer.app.session.Messenger;
import fi.helsinki.cs.titotrainer.framework.response.ErrorResponse;
import fi.helsinki.cs.titotrainer.framework.response.RedirectResponse;
import fi.helsinki.cs.titotrainer.framework.response.Response;

public class UpdateCourseController extends TitoActionController<UpdateCourseRequest> {

    @Override
    protected Response handleValid(UpdateCourseRequest req, Session hs) throws Exception {
        
        Course course = (Course)hs.get(Course.class, req.id);
        if (course == null)
            return new ErrorResponse(404);
        
        course.setName(mapToTString(req.courseName, req.getContext().getTitoTranslation(), true));
        course.setHidden(req.hidden);
        
        appendMessage(req, Messenger.GLOBAL_SUCCESS_CATEGORY, getTranslator(req).tr("course_updated"));
        
        return new RedirectResponse("/admin/course?id=" + req.id);
    }

    @Override
    public Class<UpdateCourseRequest> getRequestType() {
        return UpdateCourseRequest.class;
    }
    
    
    
}
