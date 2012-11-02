package fi.helsinki.cs.titotrainer.testsupport.app.model;

import fi.helsinki.cs.titotrainer.app.model.Criterion;
import fi.helsinki.cs.titotrainer.app.model.Task;
import fi.helsinki.cs.titotrainer.app.model.titokone.TitokoneState;

public class NonCooperativeCriterion extends Criterion {
    
    public NonCooperativeCriterion(Task task) {
        super(task);
    }
    
    @Override
    protected boolean checkIsSatisfied(TitokoneState state, TitokoneState modelState) {
        return false;
    }
    
    @Override
    protected String reconstructParameters() {
        return null;
    }
    
    @Override
    protected void interpretParameters(String parameters) throws Exception {
    }
    
}
