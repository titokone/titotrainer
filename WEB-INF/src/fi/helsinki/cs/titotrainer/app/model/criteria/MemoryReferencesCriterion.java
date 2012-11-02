package fi.helsinki.cs.titotrainer.app.model.criteria;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import fi.helsinki.cs.titotrainer.app.model.titokone.TitokoneState;

/**
 * A criterion that checks againts the number of total memory references
 * (data area + instruction fetch).
 */
@DiscriminatorValue("MEMORYREFERENCES")
@Entity
public class MemoryReferencesCriterion extends MetricCriterion {
    
    public MemoryReferencesCriterion() {
    }
    
    public MemoryReferencesCriterion(Relation rel, long rightValue) {
        super(rel, rightValue);
    }
    
    @Override
    protected long getLeftParameterValue(TitokoneState state) {
        return state.getMemoryTotalReferences();
    }
}
