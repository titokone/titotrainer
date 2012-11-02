package fi.helsinki.cs.titotrainer.app.model.titokone;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;
import org.junit.Before;
import org.junit.Test;

public class TitokoneStateTest {
    
    private int[] memory;
    
    @Before
    public void setUp() {
        memory = new int[4];
        //LOAD R1, 6(R0)
        memory[0] = 36175878;
        //STORE R2, 6(R0)
        memory[1] = 20971526;
        //NOP
        memory[2] = 524288;
        //SVC SP, =HALT
        memory[3] = 1891631115;
    }
    
    @Test
    public void shouldBeAbleToReturnUsedOpcodes() throws TitokoneCompilationException, TitokoneExecutionException {
        TitokoneState state = new TitokoneState();
        
        state.setMemory(this.memory);
        state.setMemoryCodeAreaStart(0);
        state.setMemoryDataAreaStart(4);
        
        int[] expected = new int[this.memory.length];
        for (int i = 0; i < this.memory.length; ++i) {
            expected[i] = TitokoneState.getOpcodeFromValue(this.memory[i]);
        }
        
        String[] expectedStrings = new String[expected.length];
        for (int i = 0; i < expected.length; ++i) {
            expectedStrings[i] = TitokoneState.getOpcodeNames().get(expected[i]);
        }
        
        Arrays.sort(expected);
        Arrays.sort(expectedStrings);
        
        assertArrayEquals(expected, ArrayUtils.toPrimitive(state.getUsedOpcodes().toArray(new Integer[0])));
        assertArrayEquals(expectedStrings, state.getUsedOpcodeNames().toArray(new String[0]));
    }
    
    @Test
    public void shouldTranslateMemoryValuesToStrings() {
        assertEquals("LOAD R1, 6(R0)", TitokoneState.commandToString(this.memory[0]));
        assertEquals("STORE R2, 6(R0)", TitokoneState.commandToString(this.memory[1]));
        assertEquals("NOP", TitokoneState.commandToString(this.memory[2]));
        assertEquals("SVC SP, =HALT", TitokoneState.commandToString(this.memory[3]));
    }
    
    @Test
    public void shouldReturnNullIfOpcodeTranslationDoesNotExist() {
        int i = ~0;
        assertEquals(null, TitokoneState.commandToString(i));
    }
    
}
