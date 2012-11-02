package fi.helsinki.cs.titotrainer.app.model.criteria;

import static org.junit.Assert.*;

import org.junit.Test;
import org.mockito.Mockito;

import fi.helsinki.cs.titotrainer.app.model.Criterion;
import fi.helsinki.cs.titotrainer.app.model.titokone.TitokoneState;

public class ScreenOutputCriterionTest {
    
    @Test
    public void getParametersMethodShouldWorkOnDefaultConstructedObject() {
        new ScreenOutputCriterion().getParameters();
    }
    
    @Test
    public void parPlaceholderShouldBecomeParameterList() {
        Criterion c = new ScreenOutputCriterion(1, 2, 3);
        assertEquals("1,2,3", c.subsMessagePlaceholders("%par"));
    }
    
    @Test
    public void shouldReadParametersAsCommaSeparatedList() {
        String params = " 1,-7,  0  , 55";
        int[] expected = {1, -7, 0, 55};
        
        ScreenOutputCriterion criterion = new ScreenOutputCriterion();
        criterion.setParameters(params);
        
        assertArrayEquals(expected, criterion.getExpected());
        assertEquals(params.replace(" ", ""), criterion.getParameters().replace(" ", ""));
    }
    
    @Test
    public void shouldRejectIfParametersContainNonInteger() {
        ScreenOutputCriterion criterion = new ScreenOutputCriterion();
        criterion.setParameters("1, 2, x, 4");
        assertFalse(criterion.parametersValid());
    }
    
    @Test
    public void shouldBeSatisfiedIfAllValuesMatch() {
        int[] values = {363, 775, -55};
        int[] matchingValues = values.clone();
        int[] mismatchingValues = {363, 775, -56};
        
        ScreenOutputCriterion criterion = new ScreenOutputCriterion(values);
        
        TitokoneState mockState = Mockito.mock(TitokoneState.class);
        
        Mockito.doReturn(matchingValues).when(mockState).getOutput();
        assertTrue(criterion.isSatisfied(mockState, null));
        Mockito.doReturn(mismatchingValues).when(mockState).getOutput();
        assertFalse(criterion.isSatisfied(mockState, null));
    }
    
}
