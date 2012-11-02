package fi.helsinki.cs.titotrainer.app.model.criteria;

import static org.junit.Assert.*;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.mockito.Mockito;

import fi.helsinki.cs.titotrainer.app.model.titokone.TitokoneState;

public class ForbiddenInstructionsCriterionTest extends AbstractInstructionSetCriterionTest {
    
    @Override
    protected ForbiddenInstructionsCriterion createTestObject() {
        return new ForbiddenInstructionsCriterion();
    }
    
    @Test
    public void getParametersMethodShouldWorkOnDefaultConstructedObject() {
        new ForbiddenInstructionsCriterion().getParameters();
    }
    
    @Test
    public void shouldBeSatisfiedIfNoOpcodesAreFoundInProgram() {
        String[] forbidden = {"STORE", "DIV"};
        String givenCode =
            "LOAD R1,=3\n" +
            "\n" +
            ";asd \n" +
            "ADD R1,R1\n" +
            "MUL R1,R1\n" +
            "sub R1,R1\n" + 
            "sVc SP,=HALT";
        
        TitokoneState mockState = Mockito.mock(TitokoneState.class);
        
        ForbiddenInstructionsCriterion c = createTestObject();
        c.setParameters(StringUtils.join(forbidden, ','));
        c.setUserCode(givenCode);
        assertTrue(c.isSatisfied(mockState, null));
        
        givenCode += "\nDIV R1,R1";
        c.setUserCode(givenCode);
        assertFalse(c.isSatisfied(mockState, null));
    }
    
}
