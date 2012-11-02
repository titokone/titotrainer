package fi.helsinki.cs.titotrainer.app.admin.controller;

import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.app.admin.request.CreateCategoryRequest;
import fi.helsinki.cs.titotrainer.app.controller.TitoActionController;
import fi.helsinki.cs.titotrainer.app.i18n.TitoTranslation;
import fi.helsinki.cs.titotrainer.app.model.Category;
import fi.helsinki.cs.titotrainer.app.model.Course;
import fi.helsinki.cs.titotrainer.app.model.TString;
import fi.helsinki.cs.titotrainer.framework.response.ErrorResponse;
import fi.helsinki.cs.titotrainer.framework.response.RedirectResponse;
import fi.helsinki.cs.titotrainer.framework.response.Response;

public class CreateCategoryController extends TitoActionController<CreateCategoryRequest> {
    
    @Override
    protected Response handleValid(CreateCategoryRequest req, Session hs) throws Exception {
        Course course = (Course)hs.get(Course.class, req.courseId);
        if (course == null)
            return new ErrorResponse(404);
        
        TitoTranslation tt = req.getContext().getTitoTranslation();
        TString name = mapToTString(req.categoryName, tt, true);
        
        Category cat = new Category(name, course);
        long id = (Long)hs.save(cat);
        
        return new RedirectResponse("/admin/category?id=" + id);
    }
    
    @Override
    public Class<CreateCategoryRequest> getRequestType() {
        return CreateCategoryRequest.class;
    }
}
