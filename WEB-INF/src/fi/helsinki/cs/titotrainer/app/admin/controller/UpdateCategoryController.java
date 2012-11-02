package fi.helsinki.cs.titotrainer.app.admin.controller;

import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.app.admin.request.UpdateCategoryRequest;
import fi.helsinki.cs.titotrainer.app.controller.TitoActionController;
import fi.helsinki.cs.titotrainer.app.model.Category;
import fi.helsinki.cs.titotrainer.app.session.Messenger;
import fi.helsinki.cs.titotrainer.framework.response.ErrorResponse;
import fi.helsinki.cs.titotrainer.framework.response.RedirectResponse;
import fi.helsinki.cs.titotrainer.framework.response.Response;

public class UpdateCategoryController extends TitoActionController<UpdateCategoryRequest> {
    
    @Override
    protected Response handleValid(UpdateCategoryRequest req, Session hs) throws Exception {
        Category category = (Category)hs.get(Category.class, req.id);
        if (category == null)
            return new ErrorResponse(404);
        
        category.setName(mapToTString(req.categoryName, req.getContext().getTitoTranslation(), true));
        
        appendMessage(req, Messenger.GLOBAL_SUCCESS_CATEGORY, getTranslator(req).tr("category_saved"));
        
        return new RedirectResponse("/admin/category?id=" + category.getId());
    }
    
    @Override
    public Class<UpdateCategoryRequest> getRequestType() {
        return UpdateCategoryRequest.class;
    }
}
