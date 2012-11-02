package fi.helsinki.cs.titotrainer.testsupport.app.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import fi.helsinki.cs.titotrainer.app.model.Criterion;
import fi.helsinki.cs.titotrainer.app.model.Task;
import fi.helsinki.cs.titotrainer.app.model.titokone.TitokoneState;

@Entity
@DiscriminatorValue("SIMPLE_TEST")
public class SimpleCriterion extends Criterion {

    private String parameters;
    
    public SimpleCriterion() {
        super();
    }
    
    public SimpleCriterion(Task task) {
        super(task);
    }
    
    @Override
    @Transient
    protected String reconstructParameters() {
        return this.parameters;
    }
    
    @Override
    protected void interpretParameters(String parameters) throws Exception {
        this.parameters = parameters;
    }
    
    @Override
    protected boolean checkIsSatisfied(TitokoneState state, TitokoneState modelState) {
        return true;
    }
    
}
