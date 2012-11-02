package fi.helsinki.cs.titotrainer.app.model.criteria;

import java.util.Arrays;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import fi.helsinki.cs.titotrainer.app.model.Criterion;
import fi.helsinki.cs.titotrainer.app.model.titokone.TitokoneState;

/**
 * <p>A criterion to check that the screen output matches that of the model answer.</p>
 * 
 * <p>This criterion doesn't need parameters and ignores them.</p> 
 */
@DiscriminatorValue("MODELSCREENOUTPUT")
@Entity
public class ModelScreenOutputCriterion extends Criterion {

    public ModelScreenOutputCriterion() {
        this.setParameters("");
    }
    
    @Override
    @Transient
    protected String reconstructParameters() {
        return "";
    }

    @Override
    protected boolean checkIsSatisfied(TitokoneState state, TitokoneState modelState) {
        if (modelState == null)
            return false;
        return Arrays.equals(state.getOutput(), (modelState.getOutput()));
    }

    @Override
    protected void interpretParameters(String parameters) throws Exception {
        // Ignore parameters
    }
}
