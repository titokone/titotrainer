package fi.helsinki.cs.titotrainer.app.model.criteria;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import fi.helsinki.cs.titotrainer.app.model.titokone.TitokoneState;

@DiscriminatorValue("CODESIZE")
@Entity
public class CodeSizeCriterion extends MetricCriterion {
    
    public CodeSizeCriterion() {
    }
    
    public CodeSizeCriterion(Relation rel, long rightValue) {
        super(rel, rightValue);
    }

    @Override
    @Transient
    protected long getLeftParameterValue(TitokoneState state) {
        return state.getMemoryCodeAreaSize();
    }
}
