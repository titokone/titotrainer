package fi.helsinki.cs.titotrainer.app.admin.controller;

import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.app.admin.request.CreateCourseRequest;
import fi.helsinki.cs.titotrainer.app.controller.TitoActionController;
import fi.helsinki.cs.titotrainer.app.model.Course;
import fi.helsinki.cs.titotrainer.app.session.Messenger;
import fi.helsinki.cs.titotrainer.framework.response.RedirectResponse;
import fi.helsinki.cs.titotrainer.framework.response.Response;

public class CreateCourseController extends TitoActionController<CreateCourseRequest> {
    @Override
    public Class<CreateCourseRequest> getRequestType() {
        return CreateCourseRequest.class;
    }
    
    @Override
    protected Response handleValid(CreateCourseRequest req, Session hs) throws Exception {
        Course course = new Course(mapToTString(req.courseName, req.getContext().getTitoTranslation(), true));
        course.setHidden(req.hidden);
        long id = (Long)hs.save(course);
        
        appendMessage(req, Messenger.GLOBAL_SUCCESS_CATEGORY, getTranslator(req).tr("course_created"));
        
        return new RedirectResponse("/admin/course?id=" + id);
    }
}
