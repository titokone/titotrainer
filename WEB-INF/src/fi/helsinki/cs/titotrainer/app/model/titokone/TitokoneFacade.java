package fi.helsinki.cs.titotrainer.app.model.titokone;

public interface TitokoneFacade {

    /**
     * Compiles and executes a TTK-91 program.
     * 
     * @param sourceCode
     * @param kbdInput
     * @return
     */
    public TitokoneState execute(String sourceCode, int[] kbdInput, int maxExecutionSteps) throws TitokoneCompilationException, TitokoneExecutionException;
    
    /**
     * <p>Returns the opcodes used in an incomplete code fragment.</p>
     * 
     * <p>The code fragment may contain jumps to non-existent labels and things like that.</p>
     * 
     * <p>The opcodes are returned in the order they appear in the code fragment.
     *    The list may thus have duplicates.</p>
     */
    public String[] extractCodeFragmentOpcodes(String codeFragment);
}
