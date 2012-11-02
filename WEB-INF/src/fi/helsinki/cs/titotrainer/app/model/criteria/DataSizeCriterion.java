package fi.helsinki.cs.titotrainer.app.model.criteria;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import fi.helsinki.cs.titotrainer.app.model.titokone.TitokoneState;

@DiscriminatorValue("DATASIZE")
@Entity
public class DataSizeCriterion extends MetricCriterion {
    public DataSizeCriterion() {
    }
    
    public DataSizeCriterion(Relation rel, long rightValue) {
        super(rel, rightValue);
    }

    @Override
    protected long getLeftParameterValue(TitokoneState state) {
        return state.getMemoryDataAreaSize();
    }
    
}
