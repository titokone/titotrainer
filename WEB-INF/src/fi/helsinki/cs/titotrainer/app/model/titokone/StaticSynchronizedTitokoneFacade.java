package fi.helsinki.cs.titotrainer.app.model.titokone;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import fi.helsinki.cs.titotrainer.app.model.titokone.TitokoneState.ExitStatus;
import fi.hu.cs.titokone.Application;
import fi.hu.cs.titokone.Compiler;
import fi.hu.cs.titokone.Control;
import fi.hu.cs.titokone.Processor;
import fi.hu.cs.titokone.RandomAccessMemory;
import fi.hu.cs.ttk91.TTK91AddressOutOfBounds;
import fi.hu.cs.ttk91.TTK91BadAccessMode;
import fi.hu.cs.ttk91.TTK91CompileException;
import fi.hu.cs.ttk91.TTK91CompileSource;
import fi.hu.cs.ttk91.TTK91Cpu;
import fi.hu.cs.ttk91.TTK91DivisionByZero;
import fi.hu.cs.ttk91.TTK91Exception;
import fi.hu.cs.ttk91.TTK91ExecutionOverrun;
import fi.hu.cs.ttk91.TTK91FailedWrite;
import fi.hu.cs.ttk91.TTK91IntegerOverflow;
import fi.hu.cs.ttk91.TTK91InvalidDevice;
import fi.hu.cs.ttk91.TTK91InvalidOpCode;
import fi.hu.cs.ttk91.TTK91NoKbdData;
import fi.hu.cs.ttk91.TTK91NoStdInData;

/**
 * Implements {@link TitokoneFacade} by using a singleton
 * instance of the (non-thread-safe) Titokone system.
 */
public class StaticSynchronizedTitokoneFacade implements TitokoneFacade {
    
    private static Logger logger = Logger.getLogger(StaticSynchronizedTitokoneFacade.class);
    
    private static Object titotrainerLock = new Object();
    
    private static class TitoSourceWrapper implements TTK91CompileSource {
        public String sourceCode;
        
        public TitoSourceWrapper(String sourceCode) {
            this.sourceCode = sourceCode;
        }
        
        @Override
        public String getSource() {
            return this.sourceCode;
        }
        
    }
    
    private static void silenceTitokoneLogger() {
        java.util.logging.Logger titokoneLogger = java.util.logging.Logger.getLogger(Control.class.getPackage().getName());
        titokoneLogger.setLevel(Level.OFF);
    }
    
    private static TitokoneState executeImpl(StaticSynchronizedTitokoneFacade self, String sourceCode, int[] kbdInput, int maxExecutionSteps)
        throws TitokoneCompilationException, TitokoneExecutionException {
        
        silenceTitokoneLogger();
        
        Control control = new Control(null, null);
        Application app;
        
        TTK91CompileSource src = new TitoSourceWrapper(sourceCode);
        //try to compile the given source code
        try {
            app = (Application)control.compile(src);
        } catch (TTK91CompileException e) {
            throw new TitokoneCompilationException(e.getMessage(), e);
        } catch (TTK91Exception e) {
            //does not happen in practice
            throw new TitokoneCompilationException(e);
        }
        
        //try to run the previously compiled code
        TitokoneState state = new TitokoneState();
        state.setExitStatus(ExitStatus.SUCCESSFUL);
        
        try {
            app.setKbd(StringUtils.join(ArrayUtils.toObject(kbdInput), ',') + ","); // The final comma is required by TitoKone for some reason
            
            control.run(app, maxExecutionSteps);
            
        } catch (TTK91ExecutionOverrun e) {
            state.setExitStatus(ExitStatus.MAX_STEPS_EXCEEDED);
        } catch (TTK91AddressOutOfBounds e) {
            state.setExitStatus(ExitStatus.ADDRESS_OUT_OF_BOUNDS);
        } catch (TTK91BadAccessMode e) {
            state.setExitStatus(ExitStatus.BAD_ACCESS_MODE);
        } catch (TTK91DivisionByZero e) {
            state.setExitStatus(ExitStatus.DIVISION_BY_ZERO);
        } catch (TTK91FailedWrite e) {
            state.setExitStatus(ExitStatus.FAILED_WRITE);
        } catch (TTK91IntegerOverflow e) {
            state.setExitStatus(ExitStatus.INTEGER_OVERFLOW);
        } catch (TTK91InvalidDevice e) {
            state.setExitStatus(ExitStatus.INVALID_DEVICE);
        } catch (TTK91InvalidOpCode e) {
            state.setExitStatus(ExitStatus.INVALID_OP_CODE);
        } catch (TTK91NoKbdData e) {
            state.setExitStatus(ExitStatus.NOT_ENOUGH_INPUT);
        } catch (TTK91NoStdInData e) {
            state.setExitStatus(ExitStatus.NO_STDIN_DATA);
        } catch (TTK91Exception e){
            logger.warn("Unexpected TTK91Exception: ", e);
            state.setExitStatus(ExitStatus.OTHER_ERROR);
        }
        
        
        state.setSymbols(self.getApplicationSymbolTable(app));
        
        /* for explanation of index usage while calling registers from titokone,
         * see TTK91Cpu interface.
         */
        for(int i = 0; i < TitokoneState.getNumGeneralRegisters(); i++) {
            state.setGeneralRegister(i, control.getCpu().getValueOf(i + 401));
        }
        
        state.setRegPC(control.getCpu().getValueOf(TTK91Cpu.CU_PC));
        state.setRegSP(control.getCpu().getValueOf(TTK91Cpu.REG_SP));
        state.setRegFP(control.getCpu().getValueOf(TTK91Cpu.REG_FP));
             
        RandomAccessMemory memory = (RandomAccessMemory)control.getMemory();
        
        state.setMemory(memory.getMemory());
        state.setMemoryCodeAreaStart(0);
        state.setMemoryDataAreaStart(memory.getCodeAreaSize());
        state.setMemoryDataAreaSize(memory.getDataAreaSize());
        
        Processor processor = (Processor)control.getCpu();
        state.setMaxStackSize(processor.giveStackMaxSize());
        state.setExecutedInstructions(processor.giveCommAmount());
        
        state.setMemoryDataReferences(memory.getMemoryReferences());
        
        String[] outputLines = StringUtils.split(app.readCrt(), System.getProperty("line.separator", "\n"));
        int[] output = new int[outputLines.length];
        for (int i = 0; i < output.length; ++i) {
            output[i] = Integer.parseInt(outputLines[i].trim());
        }
        state.setOutput(output);
        
        return state;
    }
    
    @Override
    public TitokoneState execute(String sourceCode, int[] kbdInput, int maxExecutionSteps)
        throws TitokoneCompilationException, TitokoneExecutionException {
        
        synchronized (titotrainerLock) {
            return executeImpl(this, sourceCode, kbdInput, maxExecutionSteps);
        }
    }
    
    private static String[] extractCodeFragmentOpcodesImpl(String codeFragment) {
        silenceTitokoneLogger();
        
        Compiler compiler = new Compiler();
        
        List<String> res = new LinkedList<String>();
        for (String line : StringUtils.split(codeFragment, '\n')) {
            try {
                String[] stuff = compiler.parseLine(line);
                String opcode = stuff[1].toUpperCase();
                if (TitokoneState.getOpcodeNumbers().containsKey(opcode)) // The compiler may let through funnies
                    res.add(opcode);
            } catch (Exception e) {
            }
        }
        return res.toArray(new String[res.size()]);
    }
    
    @Override
    public String[] extractCodeFragmentOpcodes(String codeFragment) {
        return extractCodeFragmentOpcodesStatic(codeFragment);
    }
    
    public static String[] extractCodeFragmentOpcodesStatic(String codeFragment) {
        synchronized (titotrainerLock) {
            return extractCodeFragmentOpcodesImpl(codeFragment);
        }
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, Integer> getApplicationSymbolTable(Application app) {
        return app.getSymbolTable().toHashMap();
    }
    
}
