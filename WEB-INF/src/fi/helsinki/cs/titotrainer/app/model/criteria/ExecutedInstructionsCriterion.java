package fi.helsinki.cs.titotrainer.app.model.criteria;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import fi.helsinki.cs.titotrainer.app.model.titokone.TitokoneState;

@DiscriminatorValue("EXECUTEDINSTRUCTIONS")
@Entity
public class ExecutedInstructionsCriterion extends MetricCriterion {
    
    public ExecutedInstructionsCriterion() {
    }
    
    public ExecutedInstructionsCriterion(Relation rel, long rightValue) {
        super(rel, rightValue);
    }
    
    @Override
    protected long getLeftParameterValue(TitokoneState state) {
        return state.getExecutedInstructions();
    }
}
