package fi.helsinki.cs.titotrainer.app.model.titokone;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;

import fi.helsinki.cs.titotrainer.testsupport.TitoTestCase;

public class StaticSynchronizedTitokoneFacadeTest extends TitoTestCase {
    
    private StaticSynchronizedTitokoneFacade facade;
    
    @Before
    public void setUp() {
        this.facade = new StaticSynchronizedTitokoneFacade();
    }
    
    @Test
    public void shouldRunValidTTK91Program() throws TitokoneCompilationException, TitokoneExecutionException {
        String program = "NOP\nSVC SP, =HALT";
        int maxSteps = 1000;
        
        TitokoneState state = this.facade.execute(program, new int[0], maxSteps);
        assertNotNull(state);
        //TODO: assert something about state
        assertEquals(2, state.getExecutedInstructions());
    }
    
    @Test
    public void shouldReturnMaxStepsExceededStatusIfMaxStepsExceeded() throws TitokoneCompilationException, TitokoneExecutionException {
        String program = "NOP\nSVC SP, =HALT";
        int maxSteps = 1;
        
        assertEquals(TitokoneState.ExitStatus.MAX_STEPS_EXCEEDED, this.facade.execute(program, new int[0], maxSteps).getExitStatus());
    }
    
    @Test
    public void shouldReportInsufficientInput() throws TitokoneCompilationException, TitokoneExecutionException {
        String program = "IN R1, =KBD\n";
        program += "SVC SP, =HALT";
        
        TitokoneState state = this.facade.execute(program, new int[0], 10);
        
        assertEquals(TitokoneState.ExitStatus.NOT_ENOUGH_INPUT, state.getExitStatus());
    }

    @Test
    public void shouldReturnProgramsSymbols() throws TitokoneCompilationException, TitokoneExecutionException {
        String program = "x DC 3\ny DC 42\nSVC SP, =HALT";
        
        TitokoneState state = this.facade.execute(program, new int[0], 10);
        Map<String, Integer> symbols = state.getSymbols();
        assertEquals(new Integer(1), symbols.get("x"));
        assertEquals(new Integer(2), symbols.get("y"));
    }
    
    @Test (expected = TitokoneCompilationException.class)
    public void shouldThrowTitokoneCompilationExceptionException() throws TitokoneCompilationException, TitokoneExecutionException {
        String program = "sopderwåhg";
        
        this.facade.execute(program, new int[0], 10);
        
    }
    
    @Test
    public void shouldReturnValuesOfAllGeneralRegisters() throws TitokoneCompilationException, TitokoneExecutionException {
        String program = "";
        for (int i = 0; i <= 5; ++i) { // R6 and R7 have a special meaning
            program += "LOAD R" + i + ", =" + i + "0\n";
        }
        program += "SVC SP, =HALT";
        
        TitokoneState state = this.facade.execute(program, new int[0], TitokoneState.getNumGeneralRegisters() + 2);
        
        for (int i = 0; i <= 5; ++i) {
            assertEquals(i * 10, state.getGeneralRegisters()[i]);
        }
    }
    
    @Test
    public void shouldReturnValuesOfAllSpecialRegisters() throws TitokoneCompilationException, TitokoneExecutionException {
        String program = "";
        program += "start PUSH SP, =42\n";
        program += "CALL SP, subr\n";
        program += "subr PUSH SP, =55\n";
        program += "SVC SP, =HALT";
        
        TitokoneState state = this.facade.execute(program, new int[0], TitokoneState.getNumGeneralRegisters() + 2);
        
        // TODO: verify that these are correct and explain why
        assertEquals(-1, state.getRegPC());
        assertEquals(9, state.getRegSP());
        assertEquals(9, state.getRegFP());
    }
    
    @Test
    public void shouldReturnCorrectMemoryData() throws TitokoneCompilationException, TitokoneExecutionException {
        String program = "x DC 3\nLOAD R1, =30\nSTORE R1, x\nSVC SP, =HALT";
        
        TitokoneState state = this.facade.execute(program, new int[0], 10);
        
        assertEquals(1, state.getMemoryDataAreaSize());
        assertEquals(3, state.getMemoryCodeAreaSize());
        
        //35651614 = LOAD R1, =30 in TTK91
        assertEquals(35651614, state.getMemory()[state.getMemoryCodeAreaStart()]);
        
        assertEquals(30, state.getMemory()[state.getSymbols().get("x")]);
        
        assertEquals(30, state.getMemory()[state.getMemoryDataAreaStart()]);
    }
    
    @Test
    public void shouldReturnCorrectAmountOfExecutedInstructions() throws TitokoneCompilationException, TitokoneExecutionException {
        String program = "x DC 0\n";
        program += "LOAD R1, =20\n";
        program += "STORE R1, x\n";
        program += "SVC SP, =HALT\n";
        
        TitokoneState state = this.facade.execute(program, new int[0], 10);
        
        assertEquals(3, state.getExecutedInstructions());
    }
    
    @Test
    public void shouldReturnCorrectMaxStackSize() throws TitokoneCompilationException, TitokoneExecutionException {
        String program = "PUSH SP, =30\n";
        program += "PUSH SP, =20\n";
        program += "PUSH SP, =200\n";
        program += "POP SP, R1\n";
        program += "PUSH SP, =15\n";
        program += "POP SP, R1\n";
        program += "SVC SP, =HALT";

        TitokoneState state = this.facade.execute(program, new int[0], 10);
        
        assertEquals(4, state.getMaxStackSize());
        
    }
    
    @Test
    public void shouldReturnCorrectAmountOfMemoryReferences() throws TitokoneCompilationException, TitokoneExecutionException {
        String program = "x DC 64\n";
        program += "LOAD R1, =20\n";
        program += "STORE R1, x\n";
        program += "LOAD R2, =40\n";
        program += "STORE R1, x\n";
        program += "SVC SP, =HALT"; 
        
        TitokoneState state = this.facade.execute(program, new int[0], 10);
        
        assertEquals(4, state.getMemoryDataReferences());
        assertEquals(4+5, state.getMemoryTotalReferences());
    }
    
    @Test
    public void shouldReturnCorrectOutput() throws TitokoneCompilationException, TitokoneExecutionException {
        String program = "LOAD R1, =20\n";
        program += "LOAD R2, =30\n";
        program += "OUT R1, =CRT\n";
        program += "OUT R2, =CRT\n";
        program += "SVC SP, =HALT"; 
        
        TitokoneState state = this.facade.execute(program, new int[0], 10);
        
        int[] expected = {20, 30};
        assertArrayEquals(expected, state.getOutput());
        
        program = "NOP\nSVC SP, =HALT";
        
        state = this.facade.execute(program, new int[0], 4);
        assertEquals(0, state.getOutput().length);
    }
    
    @Test
    public void shouldReturnUsedOpcodes() throws TitokoneCompilationException, TitokoneExecutionException {
        String program = "LOAD R1, =20\n";
        program += "LOAD R2, =30\n";
        program += "OUT R1, =CRT\n";
        program += "OUT R2, =CRT\n";
        program += "SVC SP, =HALT"; 
        
        TitokoneState state = this.facade.execute(program, new int[0], 10);
        
        Set<String> expectedNames = new TreeSet<String>();
        expectedNames.add("LOAD");
        expectedNames.add("OUT");
        expectedNames.add("SVC");
        
        Set<Integer> expectedNumbers = new TreeSet<Integer>();
        for (String name : expectedNames) {
            expectedNumbers.add(TitokoneState.getOpcodeNumbers().get(name));
        }
        
        assertEquals(expectedNumbers, state.getUsedOpcodes());
    }
    
    
    @Test
    public void shouldBeAbleToReturnOpcodesFromCodeFragment() {
        String fragment = "LOAD R1,=3\n" +
                          "\n" +
                          "add R1,R1\n" +
                          "OOPS OOPS OOPS\n" +
                          "; a comment, too\n" +
                          "SVC SP,=HALT";
        
        String[] expected = {
            "LOAD",
            "ADD",
            "SVC"
        };
        
        String[] opcodes = this.facade.extractCodeFragmentOpcodes(fragment);
        assertArrayEquals(expected, opcodes);
    }
    
}
