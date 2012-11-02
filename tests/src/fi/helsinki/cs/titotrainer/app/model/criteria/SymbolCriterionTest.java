package fi.helsinki.cs.titotrainer.app.model.criteria;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import fi.helsinki.cs.titotrainer.app.model.Criterion;
import fi.helsinki.cs.titotrainer.app.model.titokone.TitokoneState;

public class SymbolCriterionTest {
    
    private Map<String, Integer> testSyms;
    private int[] testMemory;
    private TitokoneState mockState;
    
    @Before
    public void setUp() {
        this.testSyms = new HashMap<String, Integer>();
        this.testMemory = new int[1024];
        
        mockState = Mockito.mock(TitokoneState.class);
        Mockito.doReturn(testSyms).when(mockState).getSymbols();
        Mockito.doReturn(testMemory).when(mockState).getMemory();
    }
    
    @Test
    public void getParametersMethodShouldWorkOnDefaultConstructedObject() {
        new SymbolCriterion().getParameters();
    }
    
    @Test
    public void parPlaceholderShouldBecomeSymbolName() {
        Criterion c = new SymbolCriterion("foo", Relation.LT, 10);
        assertEquals("foo", c.subsMessagePlaceholders("%par"));
    }
    
    @Test
    public void shouldAcceptParametersString() {
        SymbolCriterion sc = new SymbolCriterion();
        String params = "foo >= 33";
        sc.setParameters(params);
        
        assertEquals("foo", sc.getLeftParameter());
        assertEquals(Relation.GTE, sc.getRelation());
        assertEquals(33l, sc.getRightParameter());
        assertEquals(params, sc.getParameters());
    }
    
    @Test
    public void shouldRejectBadlyFormatedParameters() {
        SymbolCriterion sc = new SymbolCriterion();
        sc.setParameters("foo == 33"); // the real relation is '='
        assertFalse(sc.parametersValid());
    }
    
    @Test
    public void shouldBeSatisfiedIfSymbolExistsAndValueInMemoryMatches() {
        SymbolCriterion sc = new SymbolCriterion("foo", Relation.EQ, 456l);
        
        assertFalse(sc.isSatisfied(mockState, null));
        
        this.testSyms.put("foo", -123);
        assertFalse(sc.isSatisfied(mockState, null));
        
        this.testSyms.put("foo", 123);
        this.testMemory[123] = 42;
        assertFalse(sc.isSatisfied(mockState, null));
        
        this.testMemory[123] = 456;
        assertTrue(sc.isSatisfied(mockState, null));
    }
    
}
