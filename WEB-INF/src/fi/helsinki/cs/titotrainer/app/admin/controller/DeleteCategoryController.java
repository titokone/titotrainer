package fi.helsinki.cs.titotrainer.app.admin.controller;

import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.app.admin.request.DeleteCategoryRequest;
import fi.helsinki.cs.titotrainer.app.controller.TitoActionController;
import fi.helsinki.cs.titotrainer.app.model.Category;
import fi.helsinki.cs.titotrainer.app.model.Task;
import fi.helsinki.cs.titotrainer.app.session.Messenger;
import fi.helsinki.cs.titotrainer.framework.response.ErrorResponse;
import fi.helsinki.cs.titotrainer.framework.response.RedirectResponse;
import fi.helsinki.cs.titotrainer.framework.response.Response;

public class DeleteCategoryController extends TitoActionController<DeleteCategoryRequest> {
    
    @Override
    protected Response handleValid(DeleteCategoryRequest req, Session hs) throws Exception {
        
        Category cat = (Category)hs.get(Category.class, req.id);
        if (cat == null)
            return new ErrorResponse(404);
        
        long courseId = cat.getCourse().getId();
        
        cat.getCourse().getCategories().remove(cat);
        for (Task task : cat.getTasks()) {
            task.setCategory(null);
        }
        hs.delete(cat);
        
        appendMessage(req, Messenger.GLOBAL_SUCCESS_CATEGORY, getTranslator(req).tr("category_deleted"));
        
        return new RedirectResponse("/admin/course?id=" + courseId);
    }
    
    @Override
    public Class<DeleteCategoryRequest> getRequestType() {
        return DeleteCategoryRequest.class;
    }
    
}
