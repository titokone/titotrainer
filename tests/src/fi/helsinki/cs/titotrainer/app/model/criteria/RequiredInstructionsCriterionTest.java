package fi.helsinki.cs.titotrainer.app.model.criteria;

import static org.junit.Assert.*;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.mockito.Mockito;

import fi.helsinki.cs.titotrainer.app.model.titokone.TitokoneState;

public class RequiredInstructionsCriterionTest extends AbstractInstructionSetCriterionTest {

    
    @Override
    protected RequiredInstructionsCriterion createTestObject() {
        return new RequiredInstructionsCriterion();
    }
    
    @Test
    public void getParametersMethodShouldWorkOnDefaultConstructedObject() {
        new RequiredInstructionsCriterion().getParameters();
    }
    
    @Test
    public void shouldBeSatisfiedIfAllOpcodesAreFoundInProgram() {
        String[] required = {"ADD", "SUB"};
        String givenCode =
            "LOAD R1,=3\n" +
            "\n" +
            ";asd \n" +
            "ADD R1,R1\n" +
            "MUL R1,R1\n" +
            "sub R1,R1\n" + 
            "sVc SP,=HALT";
        
        TitokoneState mockState = Mockito.mock(TitokoneState.class);
        
        RequiredInstructionsCriterion c = createTestObject();
        c.setParameters(StringUtils.join(required, ','));
        c.setUserCode(givenCode);
        assertTrue(c.isSatisfied(mockState, null));
        
        givenCode = givenCode.replace("ADD R1,R1", "");
        c.setUserCode(givenCode);
        assertFalse(c.isSatisfied(mockState, null));
    }
    
}
