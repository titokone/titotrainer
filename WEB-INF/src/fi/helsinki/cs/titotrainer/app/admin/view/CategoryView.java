package fi.helsinki.cs.titotrainer.app.admin.view;

import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.app.admin.request.CategoryRequest;
import fi.helsinki.cs.titotrainer.app.admin.request.TaskListFragmentViewRequest;
import fi.helsinki.cs.titotrainer.app.model.Category;
import fi.helsinki.cs.titotrainer.app.model.Course;
import fi.helsinki.cs.titotrainer.app.view.TitoPageView;
import fi.helsinki.cs.titotrainer.framework.response.ErrorResponseException;
import fi.helsinki.cs.titotrainer.framework.response.ViewResponse;
import fi.helsinki.cs.titotrainer.framework.view.template.TemplateRenderer;

public class CategoryView extends TitoPageView<CategoryRequest> {

    private TaskListFragmentView taskListFragmentView;
    
    public CategoryView() {
        this.taskListFragmentView = new TaskListFragmentView();
    }
    
    @Override
    protected String getTemplateName() {
        return "admin/category.vm";
    }
    
    @Override
    protected void handle(CategoryRequest req, Session hs, TemplateRenderer tr, ViewResponse resp) throws ErrorResponseException {
        
        boolean newCategory = (req.id == null);
        
        Category category;
        if (newCategory) {
            category = new Category();
            
            assert(req.courseId != null); // Has been checked by req.validate()
            Course course = (Course)hs.get(Course.class, req.courseId);
            if (course == null)
                throw new ErrorResponseException(404, "course " + req.courseId);
            
            category.setCourse(course);
        } else {
            category = (Category)hs.get(Category.class, req.id);
            if (category == null)
                throw new ErrorResponseException(404, "category " + req.id);
        }
        
        tr.put("category", category);
        tr.put("newCategory", newCategory);
        
        
        TaskListFragmentViewRequest taskListReq = new TaskListFragmentViewRequest();
        try {
            req.copyTo(taskListReq);
            taskListReq.categoryId = category.getId();
            
            tr.put("taskListFragment", renderFragment(taskListFragmentView, taskListReq));
        } catch (Exception e) {
            throw new ErrorResponseException(500, "Failed to render task list fragment", e);
        }
        
        tr.put("supportedLocales", req.getContext().getTitoTranslation().getSupportedLocales());
    }
    
    @Override
    public Class<CategoryRequest> getRequestType() {
        return CategoryRequest.class;
    }
    
}
