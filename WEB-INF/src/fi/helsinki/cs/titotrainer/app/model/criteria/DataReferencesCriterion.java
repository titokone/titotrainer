package fi.helsinki.cs.titotrainer.app.model.criteria;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import fi.helsinki.cs.titotrainer.app.model.titokone.TitokoneState;

/**
 * A criterion that checks againts the number of data area memory references.
 */
@DiscriminatorValue("DATAREFERENCES")
@Entity
public class DataReferencesCriterion extends MetricCriterion {
    
    public DataReferencesCriterion() {
    }
    
    public DataReferencesCriterion(Relation rel, long rightValue) {
        super(rel, rightValue);
    }

    @Override
    protected long getLeftParameterValue(TitokoneState state) {
        return state.getMemoryDataReferences();
    }
}
