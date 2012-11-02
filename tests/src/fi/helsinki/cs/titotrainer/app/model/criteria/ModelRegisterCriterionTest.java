package fi.helsinki.cs.titotrainer.app.model.criteria;

import static org.junit.Assert.*;

import org.junit.Test;

import fi.helsinki.cs.titotrainer.app.model.Criterion;
import fi.helsinki.cs.titotrainer.app.model.titokone.TitokoneState;

public class ModelRegisterCriterionTest {
    
    @Test
    public void getParametersMethodShouldWorkOnDefaultConstructedObject() {
        new ModelRegisterCriterion().getParameters();
    }
    
    @Test
    public void parPlaceholderShouldBecomeRegisterName() {
        Criterion c = new ModelRegisterCriterion(3, Relation.LT);
        assertEquals("R3", c.subsMessagePlaceholders("%par"));
    }
    
    @Test
    public void shouldAcceptAnyGeneralRegisterName() {
        for (int i = 0; i < TitokoneState.getNumGeneralRegisters(); ++i) {
            
            ModelRegisterCriterion rc = new ModelRegisterCriterion();
            String params = "R" + i + " =";
            rc.setParameters(params);
            assertEquals("R" + i, rc.getLeftParameter());
            assertEquals(Relation.EQ, rc.getRelation());
            assertEquals(params, rc.getParameters());
        }
    }
    
    @Test
    public void shouldNotAcceptInvalidRegisterNumber() {
        Criterion c = new ModelRegisterCriterion();
        c.setParameters(TitokoneState.getNumGeneralRegisters() + " =");
        assertFalse(c.parametersValid());
    }

    @Test
    public void shouldBeSatisfiedIfRegisterValueMatches() {
        TitokoneState state = new TitokoneState();
        TitokoneState modelState = new TitokoneState();
        
        
        Criterion c = new ModelRegisterCriterion();
        c.setParameters("R3 >");
        
        state.getGeneralRegisters()[3] = 15;
        modelState.getGeneralRegisters()[3] = 10;
        assertTrue(c.isSatisfied(state, modelState));
        
        state.getGeneralRegisters()[3] = 5;
        assertFalse(c.isSatisfied(state, modelState));
    }
    
    @Test
    public void shouldNotBeSatisfiedIfModelStateIsNull() {
        assertFalse(new ModelRegisterCriterion().isSatisfied(new TitokoneState(), null));
    }
}
