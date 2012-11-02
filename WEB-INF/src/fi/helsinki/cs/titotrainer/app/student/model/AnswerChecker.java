package fi.helsinki.cs.titotrainer.app.student.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.app.misc.SortUtils;
import fi.helsinki.cs.titotrainer.app.model.Answer;
import fi.helsinki.cs.titotrainer.app.model.Criterion;
import fi.helsinki.cs.titotrainer.app.model.ExecStatus;
import fi.helsinki.cs.titotrainer.app.model.Input;
import fi.helsinki.cs.titotrainer.app.model.Task;
import fi.helsinki.cs.titotrainer.app.model.User;
import fi.helsinki.cs.titotrainer.app.model.Validation;
import fi.helsinki.cs.titotrainer.app.model.misc.PartialCriterionCmp;
import fi.helsinki.cs.titotrainer.app.model.titokone.TitokoneCompilationException;
import fi.helsinki.cs.titotrainer.app.model.titokone.TitokoneExecutionException;
import fi.helsinki.cs.titotrainer.app.model.titokone.TitokoneFacade;
import fi.helsinki.cs.titotrainer.app.model.titokone.TitokoneState;
import fi.helsinki.cs.titotrainer.app.model.titokone.TitokoneState.ExitStatus;
import fi.helsinki.cs.titotrainer.app.session.TitoUserSession;

public class AnswerChecker {
    
    public static class AnswerCheckException extends Exception {
        private AnswerCheckException(String msg, Exception cause) {
            super(msg, cause);
        }
        
        @Override
        public Exception getCause() {
            return (Exception)super.getCause();
        }
    }
    
    private Logger logger;
    
    private Session hs;
    private TitoUserSession userSession;
    private Task task;
    private TitokoneFacade titokone;
    private int[] customInput;
    private String codeWrittenByUser;
    
    public AnswerChecker() {
        logger = Logger.getLogger(AnswerChecker.class);
    }
    
    public void setHibernateSession(Session hs) {
        this.hs = hs;
    }
    
    public void setUserSession(TitoUserSession userSession) {
        this.userSession = userSession;
    }
    
    public void setTask(Task task) {
        this.task = task;
    }
    
    public void setTitokoneFacade(TitokoneFacade titokone) {
        this.titokone = titokone;
    }
    
    public void setCustomInput(int[] customInput) {
        this.customInput = customInput;
    }
    
    public void setCodeWrittenByUser(String codeWrittenByUser) {
        this.codeWrittenByUser = codeWrittenByUser;
    }
    
    
    public void checkAnswer() throws AnswerCheckException {
        User user = userSession.getAuthenticatedUser();
        
        boolean compiledSuccessfully = true;
        
        try {
            checkCustomInput();
        } catch (TitokoneCompilationException e) {
            compiledSuccessfully = false;
        }
        
        deleteOldAnswer(user);
        
        Answer newAnswer = createAndSaveNewAnswer(user, compiledSuccessfully);
        
        if (compiledSuccessfully) {
            for (Input input : task.getInputs()) {
                checkPredefinedInput(input, newAnswer);
            }
        }
        
        finalizeNewAnswer(newAnswer);
    }

    
    private void checkCustomInput() throws AnswerCheckException, TitokoneCompilationException {
        TitokoneState modelSolutionState = null;
        try {
            modelSolutionState = executeModelSolution(customInput);
        } catch (Exception e) {
            String msg = "Error running model solution for task " + task.getId() + " with custom input " + ArrayUtils.toString(customInput) + ": " + e.getMessage();
            throw new AnswerCheckException(msg, e);
        }
        
        try {
            
            userSession.setAttribute("customInput", customInput);
            
            TitokoneState customInputTitokoneState = executeUserCode(customInput);
            
            userSession.setAttribute("customInputTitokoneState", customInputTitokoneState);
            userSession.setAttribute("customInputModelSolutionState", modelSolutionState);
            
            Collection<Long> customInputSatisfiedCriteria = getSatisfiedCriteria(customInput, customInputTitokoneState, modelSolutionState);
            userSession.setAttribute("customInputSatisfiedCriteria", customInputSatisfiedCriteria.toArray());

        } catch (TitokoneCompilationException e) {
            userSession.setAttribute("customInputCompilationError", e.getMessage());
            throw e;
        } catch (TitokoneExecutionException e) {
            String msg = "User's answer caused a TitokoneExecutionException for task " + task.getId() + " with input " + ArrayUtils.toString(customInput);
            throw new AnswerCheckException(msg, e);
        } catch (Exception e) {
            String msg = "User's answer caused an Exception for task " + task.getId() + " with input " + ArrayUtils.toString(customInput);
            throw new AnswerCheckException(msg, e);
        }
    }
    
    private Collection<Long> getSatisfiedCriteria(int[] input, TitokoneState titokoneState, TitokoneState modelTitokoneState) {
        Collection<Long> satisfiedCriteria = new LinkedList<Long>();
        for (Criterion criterion : getSortedCriteria()) {
            criterion.setUserCode(codeWrittenByUser); // We won't save this change
            if (criterion.getInput() == null || Arrays.equals(input, criterion.getInput().getInputNumbers())) {       
                if (criterion.isSatisfied(titokoneState, modelTitokoneState))
                    satisfiedCriteria.add(criterion.getId());
            }
        }
        return satisfiedCriteria;
    }

    private void deleteOldAnswer(User user) {
        // (fixme: this doesn't work with the unit test database for some reason)
        Answer oldAnswer = (Answer)hs.createQuery("FROM Answer WHERE task.id = ? AND user.id = ?")
                                     .setLong(0, task.getId())
                                     .setLong(1, user.getId())
                                     .uniqueResult();
        if (oldAnswer != null) {
            for (Validation v : oldAnswer.getValidations())
                hs.delete(v);
            oldAnswer.getValidations().clear();
            for (ExecStatus es : oldAnswer.getExecStatuses())
                hs.delete(es);
            oldAnswer.getExecStatuses().clear();
            hs.delete(oldAnswer);
            hs.flush();
        }
    }
    
    private Answer createAndSaveNewAnswer(User user, boolean compiledSuccessfully) {
        Answer newAnswer = new Answer(user, task, codeWrittenByUser, compiledSuccessfully);
        task.getAnswers().add(newAnswer);
        hs.save(newAnswer);

        // We save the new answer even if it will fail.
        hs.getTransaction().commit();
        hs.beginTransaction();
        
        return newAnswer;
    }
    
    private void checkPredefinedInput(Input input, Answer newAnswer) throws AnswerCheckException {
        try {
            TitokoneState state = executeUserCode(input.getInputNumbers());
            
            addExecStatusToAnswer(newAnswer, input, state.getExitStatus());
            
            if (state.getExitStatus() != TitokoneState.ExitStatus.SUCCESSFUL) {
                return; // No need to check criteria
            }
            
            TitokoneState modelState;
            try {
                modelState = executeModelSolution(input.getInputNumbers());
                if (modelState != null) {
                    if (modelState.getExitStatus() != TitokoneState.ExitStatus.SUCCESSFUL) {
                        throw new Exception("Model solution returned " + modelState.getExitStatus());
                    }
                }
            } catch (Exception e) {
                logger.warn("Failed to run model solution for task " + task.getId() + " on input " + input.getId(), e);
                throw e;
            }
            
            
            for (Criterion criterion : task.getCriteria()) {
                criterion.setUserCode(codeWrittenByUser); // Transient
                if (criterion.getInput() != null && !criterion.getInput().equals(input))
                    continue;
                
                Validation validation = new Validation();
                validation.setSatisfied(criterion.isSatisfied(state, modelState));
                validation.setAnswer(newAnswer);
                validation.setInput(input);
                validation.setCriterion(criterion);
                
                criterion.getValidations().add(validation);
                hs.save(validation);
                hs.update(criterion);
                hs.update(validation);
            }
        } catch (Exception e) {
            String inputStr = input.isSecret() ? " a secret input" : " input '" + input.getInput() + "'";
            String msg =
                "Caught exception while running TitoKone for task " + task.getId() +
                " with " + inputStr + ": " + e.getMessage();
            throw new AnswerCheckException(msg, e);
        }
    }


    private void addExecStatusToAnswer(Answer newAnswer, Input input, ExitStatus exitStatus) {
        ExecStatus execStatus = new ExecStatus(newAnswer, input, exitStatus);
        newAnswer.getExecStatuses().add(execStatus);
        hs.save(execStatus);
    }


    private TitokoneState executeUserCode(int[] input) throws Exception {
        return titokone.execute(getFullUserCode(), input, task.getMaxSteps());
    }
    
    private TitokoneState executeModelSolution(int[] input) throws Exception {
        String modelCode = getFullModelCode();
        if (modelCode != null) {
            return titokone.execute(modelCode, input, task.getMaxSteps());
        } else {
            return null;
        }
    }
    
    private List<Criterion> getSortedCriteria() {
        return SortUtils.getSortedList(task.getCriteria(), new PartialCriterionCmp());
    }
    
    private String getFullUserCode() {
        return preprocessCode(codeWrittenByUser);
    }
    
    private String getFullModelCode() {
        if (task.getModelSolution() != null) {
            return preprocessCode(task.getModelSolution());
        } else {
            return null;
        }
    }
    
    private String preprocessCode(String code) {
        String preCode = task.getPreCode() != null ? task.getPreCode() + "\n" : "";
        String postCode = task.getPostCode() != null ? "\n" + task.getPostCode() : "";
        return preCode + code + postCode;
    }
    
    private void finalizeNewAnswer(Answer newAnswer) {
        hs.update(task);
        hs.update(newAnswer);
        hs.flush();
    }
    
}
