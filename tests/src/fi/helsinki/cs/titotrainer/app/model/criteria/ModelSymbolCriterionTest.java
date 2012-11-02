package fi.helsinki.cs.titotrainer.app.model.criteria;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import fi.helsinki.cs.titotrainer.app.model.Criterion;
import fi.helsinki.cs.titotrainer.app.model.titokone.TitokoneState;

public class ModelSymbolCriterionTest {
    private Map<String, Integer> testSyms1;
    private Map<String, Integer> testSyms2;
    private int[] testMemory1;
    private int[] testMemory2;
    private TitokoneState mockState1;
    private TitokoneState mockState2;
    
    @Before
    public void setUp() {
        this.testSyms1 = new HashMap<String, Integer>();
        this.testMemory1 = new int[1024];
        
        this.testSyms2 = new HashMap<String, Integer>();
        this.testMemory2 = new int[1024];
        
        mockState1 = Mockito.mock(TitokoneState.class);
        Mockito.doReturn(testSyms1).when(mockState1).getSymbols();
        Mockito.doReturn(testMemory1).when(mockState1).getMemory();
        
        mockState2 = Mockito.mock(TitokoneState.class);
        Mockito.doReturn(testSyms2).when(mockState2).getSymbols();
        Mockito.doReturn(testMemory2).when(mockState2).getMemory();
    }
    
    @Test
    public void getParametersMethodShouldWorkOnDefaultConstructedObject() {
        new ModelSymbolCriterion().getParameters();
    }
    
    @Test
    public void parPlaceholderShouldBecomeSymbolName() {
        Criterion c = new ModelSymbolCriterion("foo", Relation.LT);
        assertEquals("foo", c.subsMessagePlaceholders("%par"));
    }
    
    @Test
    public void shouldAcceptParametersString() {
        ModelSymbolCriterion sc = new ModelSymbolCriterion();
        String params = "foo >=";
        sc.setParameters(params);
        
        assertEquals("foo", sc.getLeftParameter());
        assertEquals(Relation.GTE, sc.getRelation());
        assertEquals(params, sc.getParameters());
    }
    
    @Test
    public void shouldRejectBadlyFormatedParameters() {
        ModelSymbolCriterion sc = new ModelSymbolCriterion();
        sc.setParameters("foo =="); // the real relation is '='
        assertFalse(sc.parametersValid());
    }
    
    @Test
    public void shouldBeSatisfiedIfSymbolExistsAndValueInMemoryMatches() {
        ModelSymbolCriterion sc = new ModelSymbolCriterion();
        sc.setParameters("foo =");
        
        assertFalse(sc.isSatisfied(mockState1, mockState2));
        
        this.testSyms1.put("foo", -123);
        this.testSyms2.put("foo", -123);
        assertFalse(sc.isSatisfied(mockState1, mockState2));
        
        this.testSyms1.put("foo", 123);
        this.testSyms2.put("foo", 123);
        this.testMemory1[123] = 456;
        this.testMemory2[123] = 999;
        assertFalse(sc.isSatisfied(mockState1, mockState2));
        
        this.testMemory2[123] = 456;
        assertTrue(sc.isSatisfied(mockState1, mockState2));
    }
    
    @Test
    public void shouldNotBeSatisfiedIfModelStateIsNull() {
        assertFalse(new ModelSymbolCriterion().isSatisfied(new TitokoneState(), null));
    }
}
