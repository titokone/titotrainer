package fi.helsinki.cs.titotrainer.app.model.criteria;

import java.util.Arrays;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.apache.commons.collections.iterators.ArrayIterator;
import org.apache.commons.lang.StringUtils;

import fi.helsinki.cs.titotrainer.app.model.Criterion;
import fi.helsinki.cs.titotrainer.app.model.titokone.TitokoneState;

/**
 * Compares screen output to a predefined (comma-separated) list of integers.
 */
@DiscriminatorValue("SCREENOUTPUT")
@Entity
public class ScreenOutputCriterion extends Criterion {
    
    private int[] expected;

    public ScreenOutputCriterion() {
    }
    
    public ScreenOutputCriterion(int ... expected) {
        this.setExpected(expected);
        this.setParameters(this.reconstructParameters());
    }
    
    @Override
    @Transient
    protected String reconstructParameters() {
        if (expected != null)
            return StringUtils.join(new ArrayIterator(expected), ',');
        else
            return null;
    }
    
    @Override
    protected void interpretParameters(String parameters) throws Exception {
        if (parameters == null)
            parameters = "";
        
        String[] parts = StringUtils.split(parameters, ',');
        int[] values = new int[parts.length];
        
        for (int i = 0; i < parts.length; ++i) {
            try {
                values[i] = Integer.parseInt(parts[i].trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(e);
            }
        }
        
        this.expected = values;
    }
    
    @Transient
    public int[] getExpected() {
        return expected;
    }
    
    public void setExpected(int[] expected) {
        this.expected = expected;
    }

    @Override
    protected boolean checkIsSatisfied(TitokoneState state, TitokoneState modelState) {
        int[] output = state.getOutput();
        return Arrays.equals(expected, output);
    }
    
}
