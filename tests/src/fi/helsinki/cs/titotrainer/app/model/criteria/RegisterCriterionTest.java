package fi.helsinki.cs.titotrainer.app.model.criteria;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import fi.helsinki.cs.titotrainer.app.model.Criterion;
import fi.helsinki.cs.titotrainer.app.model.titokone.TitokoneState;

public class RegisterCriterionTest {
    
    private int[] testRegs;
    private TitokoneState mockState;
    
    @Before
    public void setUp() {
        this.testRegs = new int[TitokoneState.getNumGeneralRegisters()];
        for (int i = 0; i < testRegs.length; ++i) {
            testRegs[i] = i*i;
        }
        
        mockState = Mockito.mock(TitokoneState.class);
        Mockito.doReturn(testRegs).when(mockState).getGeneralRegisters();
    }
    
    @Test
    public void getParametersMethodShouldWorkOnDefaultConstructedObject() {
        new RegisterCriterion().getParameters();
    }
    
    @Test
    public void parPlaceholderShouldBecomeRegisterName() {
        Criterion c = new RegisterCriterion(3, Relation.LT, 10);
        assertEquals("R3", c.subsMessagePlaceholders("%par"));
    }
    
    @Test
    public void shouldAcceptAnyGeneralRegisterName() {
        for (int i = 0; i < TitokoneState.getNumGeneralRegisters(); ++i) {
            new RegisterCriterion(i, Relation.EQ, 42);
            
            RegisterCriterion rc = new RegisterCriterion();
            String params = "R" + i + " = 42";
            rc.setParameters(params);
            assertEquals("R" + i, rc.getLeftParameter());
            assertEquals(Relation.EQ, rc.getRelation());
            assertEquals(42l, rc.getRightParameter());
            assertEquals(params, rc.getParameters());
        }
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptInvalidRegisterNumber() {
        new RegisterCriterion(TitokoneState.getNumGeneralRegisters(), Relation.EQ, 42);
    }

    @Test
    public void shouldBeSatisfiedIfRegisterValueMatches() {
        Criterion c = new RegisterCriterion(3, Relation.GT, 42);
        testRegs[3] = 43;
        assertTrue(c.isSatisfied(mockState, null));
        testRegs[3] = 42;
        assertFalse(c.isSatisfied(mockState, null));
    }
    
}
