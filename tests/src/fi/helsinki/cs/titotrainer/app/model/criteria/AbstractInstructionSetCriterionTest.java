package fi.helsinki.cs.titotrainer.app.model.criteria;

import static org.junit.Assert.*;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

/**
 * Common tests for {@link InstructionSetCriterion} subclasses.
 */
public abstract class AbstractInstructionSetCriterionTest {
    
    protected abstract InstructionSetCriterion createTestObject();
    
    @Test
    public void parPlaceholderShouldBecomeInstructionList() {
        InstructionSetCriterion c = createTestObject();
        c.setParameters("ADD, DIV, SVC");
        assertEquals(c.getParameters(), c.subsMessagePlaceholders("%par"));
    }
    
    @Test
    public void shouldParseParametersAsOpcodeNames() {
        String testParams = "LOAD,SVC, STORE , SUB  ,MUL";
        String[] expectedTestParams = {"LOAD", "SVC", "STORE", "SUB", "MUL"};
        
        InstructionSetCriterion c = createTestObject();
        c.setParameters(testParams);
        assertTrue(c.parametersValid());
        
        String[] getterResults = StringUtils.split(c.getParameters().replace(" ", ""), ',');
        assertEquals(expectedTestParams.length, getterResults.length);
        for (String expected : expectedTestParams) {
            assertTrue(ArrayUtils.contains(getterResults, expected));
        }
    }
    
    @Test
    public void shouldNotAcceptInvalidOpcodes() {
        InstructionSetCriterion criterion = createTestObject();
        criterion.setParameters("ADD,FOO,SUB");
        assertFalse(criterion.parametersValid());
    }
    
    @Test
    public void shouldNotChangeOrderOfParameters() {
        InstructionSetCriterion criterion = createTestObject();
        criterion.setParameters("LOAD , MUL");
        assertEquals("LOAD,MUL", criterion.getParameters());
        criterion.setParameters("MUL , LOAD");
        assertEquals("MUL,LOAD", criterion.getParameters());
    }
    
}
