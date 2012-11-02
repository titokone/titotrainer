package fi.helsinki.cs.titotrainer.app.model.criteria;

import static org.junit.Assert.*;

import org.junit.Test;

import fi.helsinki.cs.titotrainer.app.model.Criterion;
import fi.helsinki.cs.titotrainer.app.model.titokone.TitokoneState;

public class ModelScreenOutputCriterionTest {
    
    @Test
    public void getParametersMethodShouldWorkOnDefaultConstructedObject() {
        new ModelScreenOutputCriterion().getParameters();
    }
    
    @Test
    public void parPlaceholderShouldBecomeNothing() {
        Criterion c = new ModelScreenOutputCriterion();
        assertEquals("", c.subsMessagePlaceholders("%par"));
    }
    
    @Test
    public void shouldCompareOutputsOfTitokoneStates() {
        TitokoneState state1 = new TitokoneState();
        TitokoneState state2 = new TitokoneState();
        TitokoneState state3 = new TitokoneState();
        
        state1.setOutput(new int[] {1, 2, 3});
        state2.setOutput(new int[] {1, 2, 3});
        state3.setOutput(new int[] {0, 1, 2, 3});
        
        Criterion c = new ModelScreenOutputCriterion();
        assertTrue(c.isSatisfied(state1, state2));
        assertFalse(c.isSatisfied(state2, state3));
    }
    
    @Test
    public void shouldNotBeSatisfiedIfModelStateIsNull() {
        TitokoneState state = new TitokoneState();
        state.setOutput(new int[] {-5, 7, 99});
        
        assertFalse(new ModelScreenOutputCriterion().isSatisfied(state, null));
    }
    
}
