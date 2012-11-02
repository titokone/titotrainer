package fi.helsinki.cs.titotrainer.app.model.criteria;

import static org.junit.Assert.*;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import fi.helsinki.cs.titotrainer.app.model.Criterion;
import fi.helsinki.cs.titotrainer.app.model.titokone.TitokoneState;

public class MetricCriteriaTest {
    
    private TitokoneState mockState;
    
    @Before
    public void setUp() {
        mockState = Mockito.mock(TitokoneState.class);
    }
    
    private void test(Criterion c, String getter) {
        c.getParameters(); // Should not throw anything
        
        c.setParameters("= 33");
        
        assertEquals("33", c.subsMessagePlaceholders("%par"));
        
        Method getterMethod;
        try {
            getterMethod = TitokoneState.class.getMethod(getter);
            
            getterMethod.invoke(Mockito.doReturn(33).when(mockState));
            assertTrue(c.isSatisfied(mockState, null));
            getterMethod.invoke(Mockito.doReturn(34).when(mockState));
            assertFalse(c.isSatisfied(mockState, null));
            
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    @Test
    public void codeSizeCriterionShouldCompareToCodeAreaSize() {
        test(new CodeSizeCriterion(), "getMemoryCodeAreaSize");
    }
    
    @Test
    public void dataSizeCriterionShouldCompareToDataAreaSize() {
        test(new DataSizeCriterion(), "getMemoryDataAreaSize");
    }
    
    @Test
    public void maxStackSizeCriterionShouldCompareToMaxStackSize() {
        test(new MaxStackSizeCriterion(), "getMaxStackSize");
    }
    
    @Test
    public void dataReferencesCriterionShouldCompareToMemoryDataReferences() {
        test(new DataReferencesCriterion(), "getMemoryDataReferences");
    }
    
    @Test
    public void memoryReferencesCriterionShouldCompareToMemoryTotalReferences() {
        test(new MemoryReferencesCriterion(), "getMemoryTotalReferences");
    }
    
    @Test
    public void executedInstructionsCriterionShouldCompareToExecutedInstructions() {
        test(new ExecutedInstructionsCriterion(), "getExecutedInstructions");
    }
    
}
