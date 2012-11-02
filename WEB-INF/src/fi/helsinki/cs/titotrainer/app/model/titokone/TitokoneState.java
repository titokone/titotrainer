package fi.helsinki.cs.titotrainer.app.model.titokone;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import fi.hu.cs.titokone.BinaryInterpreter;
import fi.hu.cs.titokone.Interpreter;

public class TitokoneState implements Serializable {
    
    private static final int GENERAL_REGISTERS = 8;
    
    private static final SortedMap<Integer, String> OPCODE_NAMES;
    
    private static final SortedMap<String, Integer> OPCODE_NUMBERS;
    
    // OPCODE_NAMES and OPCODE_NUMBERS initialization
    static {
        final TreeMap<Integer, String> validOpcodes = new TreeMap<Integer, String>();
        
        // Titokone's Interpreter class has what we need, but it's protected.
        final class OpcodeThief extends Interpreter {
            public void lootOpcodes() {
                for (Object[] row : commandData) {
                    validOpcodes.put((Integer)row[1], row[0].toString());
                }
            }
        };
        new OpcodeThief().lootOpcodes();
        
        OPCODE_NAMES = Collections.unmodifiableSortedMap(validOpcodes);
        
        // Alas, MapUtils has no invertSortedMap()
        OPCODE_NUMBERS = new TreeMap<String, Integer>();
        for (Entry<Integer, String> e : OPCODE_NAMES.entrySet()) {
            OPCODE_NUMBERS.put(e.getValue(), e.getKey());
        }
    }
    
    /**
     * Returns the total number of general registers.
     * Some of them overlap with special purpose registers.
     * This is a constant.
     */
    public static int getNumGeneralRegisters() {
        return GENERAL_REGISTERS;
    }

    /**
     * Returns a map of opcode code number to upper-case name.
     */
    public static SortedMap<Integer, String> getOpcodeNames() {
        return OPCODE_NAMES;
    }
    
    /**
     * Returns a map of upper-case opcode name to its code number.
     */
    public static SortedMap<String, Integer> getOpcodeNumbers() {
        return OPCODE_NUMBERS;
    }

    public static int getOpcodeFromValue(int memoryValue) {
        BinaryInterpreter interp = new BinaryInterpreter();
        return Integer.parseInt(interp.getOpCodeFromBinary(memoryValue));
    }
    
    /**
     * Returns a string representation of a binary TTK-91 command.
     * 
     * @param memoryValue The binary command.
     * @return A string representation, or null if it could not be formed.
     */
    public static String commandToString(int memoryValue) {
        BinaryInterpreter interp = new BinaryInterpreter();
        String value = interp.binaryToString(memoryValue);
        if (!value.equals(BinaryInterpreter.GARBLE))
            return value;
        else
            return null;
    }
    
    /**
     * The exit status of Titokone.
     */
    public static enum ExitStatus {
        SUCCESSFUL,
        MAX_STEPS_EXCEEDED,
        ADDRESS_OUT_OF_BOUNDS,
        BAD_ACCESS_MODE,
        DIVISION_BY_ZERO,
        FAILED_WRITE,
        INTEGER_OVERFLOW,
        INVALID_DEVICE,
        INVALID_OP_CODE,
        NOT_ENOUGH_INPUT,
        NO_STDIN_DATA,
        OTHER_ERROR
    }
    
    private ExitStatus exitStatus;
    
    private Map<String, Integer> symbols;
    
    private int[] generalRegisters = new int[getNumGeneralRegisters()];
    
    // Special registers:
    private int regPC;
    private int regSP;
    private int regFP;
    
    private int[] memory;
    
    private int memoryCodeAreaStart;
    private int memoryDataAreaStart;
    private int memoryDataAreaSize;
    private int maxStackSize;
    private int executedInstructions;
    private int memoryDataReferences;
    
    private int[] output;
    
    public ExitStatus getExitStatus() {
        return exitStatus;
    }
    
    public void setExitStatus(ExitStatus execStatus) {
        this.exitStatus = execStatus;
    }
    
    public int getExecutedInstructions() {
        return executedInstructions;
    }
    
    public void setExecutedInstructions(int executedInstructions) {
        this.executedInstructions = executedInstructions;
    }
    
    public void setMaxStackSize(int maxStackSize) {
        this.maxStackSize = maxStackSize;
    }
    
    public int getMaxStackSize() {
        return maxStackSize;
    }
    
    /**
     * <p>Returns a mapping of symbol name to symbol address.</p>
     * 
     * @return A map.
     */
    public Map<String, Integer> getSymbols() {
        return Collections.unmodifiableMap(symbols);
    }
    
    public void setSymbols(Map<String, Integer> symbols) {
        this.symbols = symbols;
    }
    
    public int[] getGeneralRegisters() {
        return generalRegisters;
    }
    
    public void setGeneralRegister(int ind, int value) {
        generalRegisters[ind] = value;
    }
    
    /**
     * Returns the Program Counter pseudo-register.
     */
    public int getRegPC() {
        return regPC;
    }
    
    public void setRegPC(int regPC) {
        this.regPC = regPC;
    }
    
    /**
     * Returns the Stack Pointer register.
     */
    public int getRegSP() {
        return regSP;
    }
    
    public void setRegSP(int regSP) {
        this.regSP = regSP;
    }
    
    /**
     * Returns the Frame Pointer register.
     */
    public int getRegFP() {
        return regFP;
    }
    
    public void setRegFP(int regFP) {
        this.regFP = regFP;
    }
    
    public int[] getMemory() {
        return memory;
    }
    
    public void setMemory(int[] memory) {
        this.memory = memory;
    }
    
    public int getMemoryCodeAreaStart() {
        return memoryCodeAreaStart;
    }
    
    public void setMemoryCodeAreaStart(int memoryCodeAreaStart) {
        this.memoryCodeAreaStart = memoryCodeAreaStart;
    }
    
    public int getMemoryCodeAreaSize() {
        assert(this.memoryDataAreaStart >= this.memoryCodeAreaStart);
        return this.memoryDataAreaStart - this.memoryCodeAreaStart;
    }
    
    public int getMemoryDataAreaStart() {
        return memoryDataAreaStart;
    }
    
    public void setMemoryDataAreaStart(int memoryDataAreaStart) {
        this.memoryDataAreaStart = memoryDataAreaStart;
    }
    
    public int getMemoryDataAreaSize() {
        return memoryDataAreaSize;
    }
    
    public void setMemoryDataAreaSize(int memoryDataAreaSize) {
        this.memoryDataAreaSize = memoryDataAreaSize;
    }
    
    /**
     * Returns the number of memory references the program made to its data area.
     * @return The number of data references.
     */
    public int getMemoryDataReferences() {
        return memoryDataReferences;
    }
    
    public void setMemoryDataReferences(int memoryDataReferences) {
        this.memoryDataReferences = memoryDataReferences;
    }
    
    /**
     * Returns the total number of memory references i.e. data references + instruction fetches.
     * @return The number of memory data references plus the number of executed instructions.
     */
    public int getMemoryTotalReferences() {
        return getMemoryDataReferences() + getExecutedInstructions();
    }
    
    public int[] getOutput() {
        return output;
    }
    
    public void setOutput(int[] output) {
        this.output = output;
    }
    
    /**
     * Returns the numbers of the opcodes written in the program's code area.
     * 
     * @return A modifiable non-null set of non-null uppercase opcode numbers.
     * @see #getUsedOpcodeNames()
     */
    public SortedSet<Integer> getUsedOpcodes() {
        SortedSet<Integer> opcodes = new TreeSet<Integer>();

        for (int i = this.getMemoryCodeAreaStart(); i < this.getMemoryCodeAreaStart() + this.getMemoryCodeAreaSize(); ++i) {
            opcodes.add(getOpcodeFromValue(this.memory[i]));
        }
        return opcodes;
    }
    
    /**
     * Returns the upper-case names of the opcodes written in the program's code area.
     * 
     * @return An unmodifiable non-null set of non-null opcode names.
     */
    public SortedSet<String> getUsedOpcodeNames() {
        SortedSet<Integer> opcodes = getUsedOpcodes();
        SortedSet<String> names = new TreeSet<String>();
        for (Integer op : opcodes) {
            names.add(OPCODE_NAMES.get(op));
        }
        return Collections.unmodifiableSortedSet(names);
    }
    
}
