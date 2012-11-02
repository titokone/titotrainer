package fi.helsinki.cs.titotrainer.app.admin.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.app.admin.request.TaskRequest;
import fi.helsinki.cs.titotrainer.app.misc.SortUtils;
import fi.helsinki.cs.titotrainer.app.model.Category;
import fi.helsinki.cs.titotrainer.app.model.Course;
import fi.helsinki.cs.titotrainer.app.model.Criterion;
import fi.helsinki.cs.titotrainer.app.model.CriterionType;
import fi.helsinki.cs.titotrainer.app.model.Task;
import fi.helsinki.cs.titotrainer.app.model.misc.PartialCriterionCmp;
import fi.helsinki.cs.titotrainer.app.model.misc.PartialInputCmp;
import fi.helsinki.cs.titotrainer.app.model.titokone.TitokoneState;
import fi.helsinki.cs.titotrainer.app.session.Messenger;
import fi.helsinki.cs.titotrainer.app.view.TitoPageView;
import fi.helsinki.cs.titotrainer.framework.response.ErrorResponseException;
import fi.helsinki.cs.titotrainer.framework.response.ViewResponse;
import fi.helsinki.cs.titotrainer.framework.view.template.TemplateRenderer;

public class TaskView extends TitoPageView<TaskRequest> {
    @Override
    public Class<TaskRequest> getRequestType() {
        return TaskRequest.class;
    }
    
    @SuppressWarnings("unchecked")
    private List<Category> getAvailableCategories(Session hs, final Locale locale, Course course) {
        assert(locale != null);
        List<Category> cats = hs.createQuery("FROM Category WHERE course.id = ?")
                                .setLong(0, course.getId())
                                .list();
        Collections.sort(cats, new Comparator<Category>() {
            @Override
            public int compare(Category o1, Category o2) {
                String name1 = o1.getName(locale);
                if (name1 == null)
                    return -1;
                String name2 = o2.getName(locale);
                if (name2 == null)
                    return 1;
                return name1.compareTo(name2);
            }
        });
        return cats;
    }
    
    @SuppressWarnings("unchecked")
    private Map<Class, CriterionType> getCriterionTypeMap(Session hs) {
        Map<Class, CriterionType> ret = new HashMap<Class, CriterionType>();
        
        List<CriterionType> types = hs.createQuery("FROM CriterionType").list();
        for (CriterionType ct : types) {
            ret.put(ct.getCriterionClass(), ct);
        }
        
        return ret;
    }
    
    @Override
    protected void handle(TaskRequest req, Session hs, TemplateRenderer tr, ViewResponse resp) throws ErrorResponseException {
        
        Locale viewLocale = this.getTranslator(req).getLocale();
        
        Course course = null;
        Category category = null;
        
        boolean newTask = (req.id == null);
        tr.put("newTask", newTask);
        
        Task task = req.getUserSession().consumeAttribute("task", Task.class);
        if (task != null) {
            /*
             * We need to load a persistent instance to be able to read
             * lazy-loaded values. We'll evict this instance at the end
             * of this method to avoid saving the merged values.
             */
            task = (Task)hs.merge(task);
        }
        
        if (newTask) {
            // The user wants to create a new task
            assert(req.courseId != null || req.categoryId != null); // Checked by req.validate()
            
            if (task == null)
                task = new Task();
            
            if (req.categoryId != null) {
                category = (Category)hs.get(Category.class, req.categoryId);
                if (category == null) {
                    throw new ErrorResponseException(404, "Category " + req.categoryId);
                }
                
                task.setCategory(category);
                
                course = category.getCourse();
                
                if (req.courseId != null && course.getId() != req.courseId)
                    throw new ErrorResponseException(404);
                
            } else {
                course = (Course)hs.get(Course.class, req.courseId);
                if (course == null) {
                    throw new ErrorResponseException(404, "Course " + req.courseId);
                }
            }
            
            assert(course != null);
            assert(req.courseId == null || req.courseId == course.getId());
            
        } else {
            // The user wants to update an existing task
            if (task == null) {
                task = (Task)hs.createQuery("FROM Task AS t LEFT JOIN FETCH t.criteria WHERE t.id = ?")
                               .setLong(0, req.id)
                               .uniqueResult();
                if (task == null)
                    throw new ErrorResponseException(404, "Task " + req.id);
            }
            
            tr.put("taskId", task.getId());
            
            course = task.getCourse();
        }
        
        // Notify the user if the parameters of some criteria are not valid
        for (Criterion c : task.getCriteria()) {
            if (!c.parametersValid()) {
                req.getUserSession().getMessenger().appendMessage(Messenger.GLOBAL_WARNING_CATEGORY, this.getTranslator(req).tr("warning_criteria_parameters_not_valid"));
                break;
            }
        }
        
        task.getCategory(); // Force eager load
        
        tr.put("task", task);
        
        tr.put("numAnswers", hs.createQuery("SELECT COUNT(*) FROM Answer WHERE task.id = ?").setLong(0, task.getId()).uniqueResult());
        
        tr.put("printMode", (req.printMode != null && req.printMode != 0l));
        
        tr.put("sortedInputs", SortUtils.getSortedArray(task.getInputs(), new PartialInputCmp()));
        
        tr.put("sortedCriteria", SortUtils.getSortedArray(task.getCriteria(), new PartialCriterionCmp()));
        
        tr.put("criterionTypeMap", getCriterionTypeMap(hs));
        
        tr.put("course", course);
        tr.put("categoryId", req.categoryId);
        
        tr.put("taskName", task.getTitle().getByPreference(true, viewLocale));
        
        tr.put("availableCategories", getAvailableCategories(hs, this.getTranslator(req).getLocale(), course));
        
        Set<Locale> supportedLocales = req.getContext().getTitoTranslation().getSupportedLocales();
        List<Locale> supportedLocaleList = new ArrayList<Locale>(supportedLocales);
        Collections.sort(supportedLocaleList, new Comparator<Locale>() {
            @Override
            public int compare(Locale o1, Locale o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });
        
        tr.put("supportedLocales", supportedLocaleList);
        
        Set<Locale> completeTranslations = new HashSet<Locale>();
        for (Locale locale : supportedLocales) {
            if (task.hasCompleteTranslation(locale))
                completeTranslations.add(locale);
        }
        tr.put("completeTranslations", completeTranslations);
        
        tr.put("TaskType", Task.Type.class);
        
        tr.put("TitokoneState", TitokoneState.class);
        
        
        hs.evict(task); // To make sure no changes from the merge in the beginning will be saved
    }
    
    @Override
    protected String getTemplateName() {
        return "admin/task.vm";
    }
}
