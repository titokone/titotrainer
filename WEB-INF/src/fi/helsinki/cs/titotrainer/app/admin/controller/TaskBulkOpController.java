package fi.helsinki.cs.titotrainer.app.admin.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.app.admin.request.TaskBulkOpRequest;
import fi.helsinki.cs.titotrainer.app.controller.TitoActionController;
import fi.helsinki.cs.titotrainer.app.model.Category;
import fi.helsinki.cs.titotrainer.app.model.Course;
import fi.helsinki.cs.titotrainer.app.model.Task;
import fi.helsinki.cs.titotrainer.app.model.User;
import fi.helsinki.cs.titotrainer.app.request.TitoRequest;
import fi.helsinki.cs.titotrainer.app.session.Messenger;
import fi.helsinki.cs.titotrainer.framework.i18n.Translator;
import fi.helsinki.cs.titotrainer.framework.response.ErrorResponseException;
import fi.helsinki.cs.titotrainer.framework.response.RedirectResponse;
import fi.helsinki.cs.titotrainer.framework.response.Response;

public class TaskBulkOpController extends TitoActionController<TaskBulkOpRequest> {
    
    private void successMsg(TitoRequest req, String msg) {
        req.getUserSession().getMessenger().appendMessage(Messenger.GLOBAL_SUCCESS_CATEGORY, msg);
    }
    
    private List<Long> getSelectedIds(TaskBulkOpRequest req, Session hs) {
        List<Long> ret = new LinkedList<Long>();
        
        for (Entry<Long, Boolean> entry : req.selected.entrySet()) {
            if (entry.getValue() == true) {
                ret.add(entry.getKey());
            }
        }
        
        return ret;
    }
    
    private List<Task> getSelectedTasks(TaskBulkOpRequest req, Session hs) {
        List<Task> ret = new LinkedList<Task>();
        
        for (long id : getSelectedIds(req, hs)) {
            Task task = (Task)hs.get(Task.class, id);
            if (task != null) {
                ret.add(task);
            }
        }
        
        return ret;
    }
    
    private void handleDelete(TaskBulkOpRequest req, Session hs) throws Exception {
        List<Task> tasks = getSelectedTasks(req, hs);
        for (Task task : tasks) {
            hs.delete(task);
        }
        
        hs.getTransaction().commit();
        
        Translator tr = this.getTranslator(req);
        successMsg(req, tr.trp("deleted_%d_tasks", tasks.size()));
    }
    
    private Category findMatchingCategory(Category oldCategory, Course targetCourse, Session hs) {
        if (oldCategory == null)
            return null;
        
        for (Category candidate : targetCourse.getCategories()) {
            if (candidate.getName().getTranslations().equals(oldCategory.getName().getTranslations()))
                return candidate;
        }
        
        return null;
    }
    
    private void handleCopyToCourse(TaskBulkOpRequest req, Session hs) throws Exception {
        User me = req.getUserSession().getAuthenticatedUser();
        
        Course targetCourse = (Course)hs.get(Course.class, req.targetCourse);
        if (targetCourse == null)
            throw new ErrorResponseException(404, "course " + req.targetCourse);
        
        List<Task> tasks = getSelectedTasks(req, hs);
        for (Task task : tasks) {
            Category destCategory = findMatchingCategory(task.getCategory(), targetCourse, hs); // Possibly null
            Task copy = task.deepCopy(targetCourse, destCategory, me, true);
            
            Category newCat = copy.getCategory();
            if (newCat != null)
                hs.saveOrUpdate(newCat);
            hs.save(copy);
        }
        
        hs.update(targetCourse);
        hs.getTransaction().commit();
        
        Translator tr = this.getTranslator(req);
        successMsg(req, tr.trp("copied_%d_tasks", tasks.size()));
    }
    
    private void handleMoveToCategory(TaskBulkOpRequest req, Session hs) throws Exception {
        
        Category targetCategory = null;
        if (req.targetCategory != null) {
            targetCategory = (Category)hs.get(Category.class, req.targetCategory);
            if (targetCategory == null)
                throw new ErrorResponseException(404, "category " + req.targetCategory);
        }
        
        List<Task> tasks = getSelectedTasks(req, hs);
        for (Task task : tasks) {
            task.setCategory(targetCategory);
        }
        
        hs.getTransaction().commit();
        
        Translator tr = this.getTranslator(req);
        successMsg(req, tr.trp("moved_%d_tasks", tasks.size()));
    }
    
    @Override
    protected Response handleValid(TaskBulkOpRequest req, Session hs) throws Exception {
        if (req.action.equals("delete")) {
            handleDelete(req, hs);
        } else if (req.action.equals("copyToCourse")) {
            handleCopyToCourse(req, hs);
        } else if (req.action.equals("moveToCategory")) {
            handleMoveToCategory(req, hs);
        } else {
            throw new Exception("Unpossible!");
        }
        
        String redirect = req.returnTo;
        if (redirect.startsWith(req.getBasePath()))
            redirect = redirect.substring(req.getBasePath().length());
        return new RedirectResponse(redirect);
    }
    
    @Override
    public Class<TaskBulkOpRequest> getRequestType() {
        return TaskBulkOpRequest.class;
    }
}
