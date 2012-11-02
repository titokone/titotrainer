package fi.helsinki.cs.titotrainer.app.student.view;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.app.misc.SortUtils;
import fi.helsinki.cs.titotrainer.app.model.Answer;
import fi.helsinki.cs.titotrainer.app.model.Criterion;
import fi.helsinki.cs.titotrainer.app.model.ExecStatus;
import fi.helsinki.cs.titotrainer.app.model.Input;
import fi.helsinki.cs.titotrainer.app.model.Task;
import fi.helsinki.cs.titotrainer.app.model.Validation;
import fi.helsinki.cs.titotrainer.app.model.misc.PartialCriterionCmp;
import fi.helsinki.cs.titotrainer.app.model.misc.PartialInputCmp;
import fi.helsinki.cs.titotrainer.app.model.titokone.TitokoneState;
import fi.helsinki.cs.titotrainer.app.model.titokone.TitokoneState.ExitStatus;
import fi.helsinki.cs.titotrainer.app.student.request.TaskListRequest;
import fi.helsinki.cs.titotrainer.app.view.TitoPageView;
import fi.helsinki.cs.titotrainer.framework.response.ErrorResponseException;
import fi.helsinki.cs.titotrainer.framework.response.ViewResponse;
import fi.helsinki.cs.titotrainer.framework.view.template.TemplateRenderer;


public class TaskView extends TitoPageView<TaskListRequest> {
    
    /**
     * The data class given to the template for each executed input.
     */
    public static final class InputData {
        public TitokoneState state;
        public int[] input;
    }
    
    public Class<TaskListRequest> getRequestType() {
        return TaskListRequest.class;
    }
    
    @Override
    protected void handle(TaskListRequest req, Session hs, TemplateRenderer tr, ViewResponse resp) throws ErrorResponseException {

        Task task = (Task)hs.get(Task.class, req.id);
        
        if (task == null)
            throw new ErrorResponseException(404, "Task not found");
        
        tr.put("task", task);
        
        tr.put("sortedInputs", SortUtils.getSortedArray(task.getInputs(), new PartialInputCmp()));
        
        tr.put("sortedCriteria", SortUtils.getSortedArray(task.getCriteria(), new PartialCriterionCmp()));
        
        Answer latestAnswer;
        
        // Load latest answer, if there is one
        latestAnswer = (Answer)hs.createQuery("FROM Answer WHERE task.id = ? AND user.id = ? ORDER BY timestamp DESC")
                                 .setLong(0, req.id)
                                 .setLong(1, req.getUserSession().getAuthenticatedUser().getId())
                                 .setMaxResults(1)
                                 .uniqueResult();
        
        tr.put("latestAnswer", latestAnswer);
        
        if (latestAnswer != null && latestAnswer.isCompiled() && !latestAnswer.isObsoleted()) {
            TreeMap<Input, String> inputValidation = new TreeMap<Input, String>(new PartialInputCmp()); // Maps input to validation status
            
            for (Input input : task.getInputs()) {
                inputValidation.put(input, "accepted");
            }
            
            for (ExecStatus execStatus : latestAnswer.getExecStatuses()) {
                if (execStatus.getExitStatus() != ExitStatus.SUCCESSFUL)
                    inputValidation.put(execStatus.getInput(), "failed");
            }
            
            for (Validation val : latestAnswer.getValidations()) {
                if (!val.isSatisfied()) {
                    if (val.getCriterion().isQualityCriterion() && inputValidation.get(val.getInput()).equals("accepted")) {
                        inputValidation.put(val.getInput(), "lacksQuality");
                    } else {
                        inputValidation.put(val.getInput(), "rejected");
                    }
                }
            }
            tr.put("inputValidation", inputValidation);
        }
        
        // Custom input and related stuff
        {
            // all of these attributes may be null
            TitokoneState state = (TitokoneState)req.getUserSession().consumeAttribute("customInputTitokoneState");
            int[] inputVals = (int[])req.getUserSession().consumeAttribute("customInput");
            
            if (inputVals == null) {
                tr.put("customInputWasTried", false);
                
                // Default to the first public input if there is one
                for (Input input : SortUtils.getSortedList(task.getInputs(), new PartialInputCmp())) {
                    if (!input.isSecret()) {
                        inputVals = input.getInputNumbers();
                        break;
                    }
                }
                
            } else {
                tr.put("customInputWasTried", true);
            }
            
            List<Criterion> relevantCriteria = new LinkedList<Criterion>(); // Criteria relevant to the current input
            for (Criterion criterion : SortUtils.getSortedList(task.getCriteria(), new PartialCriterionCmp())) {
                if (criterion.getInput() == null || Arrays.equals(criterion.getInput().getInputNumbers(), inputVals))
                    relevantCriteria.add(criterion);
            }
            
            tr.put("customInputTitokoneState", state);
            tr.put("customInput", inputVals);
            tr.put("customInputModelSolutionState", req.getUserSession().consumeAttribute("customInputModelSolutionState"));
            tr.put("customInputRelevantCriteria", relevantCriteria);
            tr.put("customInputSatisfiedCriteria", req.getUserSession().consumeAttribute("customInputSatisfiedCriteria"));
            tr.put("customInputCompilationError", req.getUserSession().consumeAttribute("customInputCompilationError"));
        }
        
        
    }

    @Override
    protected String getTemplateName() {
        return "student/task.vm";
    }
    
}
