package fi.helsinki.cs.titotrainer.app.admin.view;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import fi.helsinki.cs.titotrainer.app.admin.request.TaskListFragmentViewRequest;
import fi.helsinki.cs.titotrainer.app.model.Category;
import fi.helsinki.cs.titotrainer.app.model.Task;
import fi.helsinki.cs.titotrainer.app.view.TitoPageFragmentView;
import fi.helsinki.cs.titotrainer.framework.response.ErrorResponseException;
import fi.helsinki.cs.titotrainer.framework.response.ViewResponse;
import fi.helsinki.cs.titotrainer.framework.view.template.TemplateRenderer;

/**
 * Displays a task list.
 */
public class TaskListFragmentView extends TitoPageFragmentView<TaskListFragmentViewRequest> {

    
    @Override
    protected String getTemplateName() {
        return "admin/frag/tasklist.vm";
    }
    
    @Override
    protected void handle(TaskListFragmentViewRequest req, Session hs, TemplateRenderer tr, ViewResponse resp) throws ErrorResponseException {
        
        tr.put("courseId", req.courseId);
        tr.put("categoryId", req.categoryId);
        
        Criteria taskCriteria = hs.createCriteria(Task.class);
        Criteria categoryCriteria = hs.createCriteria(Category.class);
        if (req.courseId != null) {
            taskCriteria.add(Restrictions.eq("course.id", req.courseId));
            categoryCriteria.add(Restrictions.eq("course.id", req.courseId));
        }
        if (req.categoryId != null) {
            taskCriteria.add(Restrictions.eq("category.id", req.categoryId));
            
            Category thisCat = (Category)hs.get(Category.class, req.categoryId);
            if (thisCat != null)
                categoryCriteria.add(Restrictions.eq("course.id", thisCat.getCourse().getId()));
        }
        
        taskCriteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        categoryCriteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        
        tr.put("tasks", taskCriteria.list());
        
        tr.put("courses", hs.createQuery("FROM Course")
                            .list());
        
        tr.put("categories", categoryCriteria.list());
    }
    
    @Override
    public Class<TaskListFragmentViewRequest> getRequestType() {
        return TaskListFragmentViewRequest.class;
    }
    
}
