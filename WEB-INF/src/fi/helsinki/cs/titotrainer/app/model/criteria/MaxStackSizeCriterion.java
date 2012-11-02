package fi.helsinki.cs.titotrainer.app.model.criteria;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import fi.helsinki.cs.titotrainer.app.model.titokone.TitokoneState;

@DiscriminatorValue("MAXSTACKSIZE")
@Entity
public class MaxStackSizeCriterion extends MetricCriterion {
    
    public MaxStackSizeCriterion() {
    }
    
    public MaxStackSizeCriterion(Relation rel, long rightValue) {
        super(rel, rightValue);
    }
    
    @Override
    @Transient
    protected long getLeftParameterValue(TitokoneState state) {
        return state.getMaxStackSize();
    }
}
