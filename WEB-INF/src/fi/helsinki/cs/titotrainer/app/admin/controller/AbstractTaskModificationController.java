package fi.helsinki.cs.titotrainer.app.admin.controller;

import static org.hamcrest.Matchers.*;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.app.admin.request.AbstractTaskModificationRequest;
import fi.helsinki.cs.titotrainer.app.controller.TitoActionController;
import fi.helsinki.cs.titotrainer.app.i18n.TitoTranslation;
import fi.helsinki.cs.titotrainer.app.model.Answer;
import fi.helsinki.cs.titotrainer.app.model.Category;
import fi.helsinki.cs.titotrainer.app.model.Course;
import fi.helsinki.cs.titotrainer.app.model.Criterion;
import fi.helsinki.cs.titotrainer.app.model.ExecStatus;
import fi.helsinki.cs.titotrainer.app.model.Input;
import fi.helsinki.cs.titotrainer.app.model.Task;
import fi.helsinki.cs.titotrainer.app.model.Validation;
import fi.helsinki.cs.titotrainer.app.model.misc.ArgumentUtils;
import fi.helsinki.cs.titotrainer.app.session.Messenger;
import fi.helsinki.cs.titotrainer.framework.i18n.Translator;
import fi.helsinki.cs.titotrainer.framework.response.ErrorResponseException;

/**
 * The common parts of {@link CreateTaskController} and {@link UpdateTaskController}.
 */
public abstract class AbstractTaskModificationController<RequestType extends AbstractTaskModificationRequest> extends TitoActionController<RequestType> {
    
    private static Package CRITERIA_PACKAGE = fi.helsinki.cs.titotrainer.app.model.criteria.Relation.class.getPackage();
    
    /**
     * Returns whether a key exists in the map and its value's {@code isEmpty()}
     * method returns false (or such a method doesn't exist).
     */
    protected boolean notEmpty(Map<?, ?> map, Object key) {
        Object v = map.get(key);
        return hasProperty("empty", is(false)).matches(v);
    }
    
    /*
     * Does the "dangerous" part of saveOrUpdateTask that invalidates
     * existing answers. This is skipped if safeUpdate is set.
     */
    private void saveOrUpdateAuxObjects(Session hs, RequestType req, Task task) throws ErrorResponseException {
        TitoTranslation tt = req.getContext().getTitoTranslation();
        
        /*
         * User answers will be preserved but
         * all validations and exec statuses will be removed.
         * The answers will be marked as obsolete so that they
         * don't count towards being solved.
         */
        for (Answer a : task.getAnswers()) {
            for (Validation val : a.getValidations()) {
                val.getCriterion().getValidations().remove(val);
                hs.delete(val);
            }
            a.getValidations().clear();
            
            for (ExecStatus es : a.getExecStatuses())
                hs.delete(es);
            a.getExecStatuses().clear();
            
            a.setObsoleted(true);
            hs.update(a);
        }
        
        for (Input i : task.getInputs()) {
            hs.delete(i);
        }
        task.getInputs().clear();
        
        for (Criterion c : task.getCriteria()) {
            hs.delete(c);
        }
        task.getCriteria().clear();
        
        Map<Object, Input> inputsByKey = new HashMap<Object, Input>();
        Map<Object, Criterion> criteriaByKey = new HashMap<Object, Criterion>();
        
        // Inputs
        for (Object inputKey : req.input.keySet()) {
            Input input = new Input(task, req.input.get(inputKey), notEmpty(req.inputSecret, inputKey));
            task.getInputs().add(input);
            inputsByKey.put(inputKey, input);
        }
        
        // If no inputs given, add an implicit empty input
        if (req.input.isEmpty()) {
            Input input = new Input(task, "", false);
            task.getInputs().add(input);
            
            // Inform the user
            Translator tr = this.getTranslator(req, AbstractTaskModificationController.class);
            req.getUserSession().getMessenger().appendMessage(Messenger.GLOBAL_WARNING_CATEGORY, tr.tr("empty_input_added"));
        }

        // Criteria
        String[] criterionKeys = req.criterionType.keySet().toArray(new String[req.criterionType.size()]);
        Arrays.sort(criterionKeys);
        for (Object criterionKey : criterionKeys) {
            String className = req.criterionType.get(criterionKey);
            Criterion criterion;
            try {
                Class<?> cls = Class.forName(CRITERIA_PACKAGE.getName() + '.' + className);
                if (!Criterion.class.isAssignableFrom(cls))
                    throw new ClassNotFoundException();
                
                criterion = (Criterion)cls.newInstance();
                criterion.setTask(task);
                task.getCriteria().add(criterion);
                criteriaByKey.put(criterionKey, criterion);
                
            } catch (Exception e) {
                ErrorResponseException e2 = new ErrorResponseException(404, "Could not instantiate criterion of type " + className, e);
                this.logger.warn(e2.getMessage(), e);
                throw e2;
            }
            
            criterion.setQualityCriterion(notEmpty(req.isQualityCriterion, criterionKey));
            
            if (notEmpty(req.acceptMsg, criterionKey)) {
                criterion.setAcceptMessage(mapToTString(req.acceptMsg.get(criterionKey), tt, false));
            }
            if (notEmpty(req.rejectMsg, criterionKey)) {
                criterion.setRejectMessage(mapToTString(req.rejectMsg.get(criterionKey), tt, false));
            }
            
            if (notEmpty(req.inputId, criterionKey)) {
                Input input = inputsByKey.get(req.inputId.get(criterionKey));
                if (input != null) {
                    criterion.setInput(input);
                    input.getCriteria().add(criterion);
                }
            }
            
            if (notEmpty(req.params, criterionKey)) {
                criterion.setParameters(req.params.get(criterionKey));
            } else {
                List<String> parts = new LinkedList<String>();
                
                if (notEmpty(req.leftParam, criterionKey)) {
                    parts.add(req.leftParam.get(criterionKey));
                }
                
                if (notEmpty(req.relation, criterionKey)) {
                    parts.add(req.relation.get(criterionKey));
                }
                
                if (notEmpty(req.rightParam, criterionKey)) {
                    parts.add(req.rightParam.get(criterionKey));
                }
                
                criterion.setParameters(StringUtils.join(parts, ' '));
            }
        }
    }
    
    /**
     * Handles the {@link AbstractTaskModificationRequest} part of a request
     * and invokes {@link Session#saveOrUpdate(Object)} on a task.
     */
    protected void saveOrUpdateTask(Session hs, RequestType req, Course course, Task task) throws ErrorResponseException {
        boolean safeUpdate = (req.safeUpdate != null && req.safeUpdate == true);
        
        TitoTranslation tt = req.getContext().getTitoTranslation();
        
        Category newCategory = null;
        if (req.categoryId != null) {
            newCategory = (Category)hs.createQuery("FROM Category WHERE course.id = ? AND id = ?")
                                   .setLong(0, course.getId())
                                   .setLong(1, req.categoryId)
                                   .uniqueResult();
            
            if (newCategory == null) {
                throw new ErrorResponseException(404, "Category " + req.categoryId + " not found in course " + course.getId());
            }
        }
        
        if (task.getCourse() == null) {
            course.getTasks().add(task);
            task.setCourse(course);
        } else {
            // The subclasses should not be changing the course of a task.
            assert(task.getCourse().equals(course));
        }
        
        task.setTitle(mapToTString(req.title, tt, true));
        task.setDescription(mapToTString(req.description, tt, true));
        
        if (req.type != null)
            task.setType(Task.Type.valueOf(req.type));
        
        task.setHidden(req.hidden);
        task.setDifficulty(req.difficulty);
        task.setMaxSteps(req.maxSteps);
        
        if (!ObjectUtils.equals(task.getCategory(), newCategory)) {
            task.setCategory(newCategory);
            if (newCategory != null) {
                newCategory.getTasks().add(task);
            }
        }
        
        if (!safeUpdate) {
            task.setPreCode(ArgumentUtils.nullifyOnEmpty(req.preCode));
            task.setPostCode(ArgumentUtils.nullifyOnEmpty(req.postCode));
            task.setModelSolution(ArgumentUtils.nullifyOnEmpty(req.modelSolution));
            
            saveOrUpdateAuxObjects(hs, req, task);
        }
        
        req.getUserSession().setAttribute("task", task);
        
        task.setModificationTime(new Date());
        
        hs.saveOrUpdate(task);
        if (!safeUpdate) {
            for (Input i : task.getInputs()) {
                hs.save(i);
            }
            for (Criterion c : task.getCriteria()) {
                hs.save(c);
            }
        }
        hs.flush();
    }
    
}
