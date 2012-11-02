package fi.helsinki.cs.titotrainer.app.model.criteria;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import fi.helsinki.cs.titotrainer.app.model.Criterion;
import fi.helsinki.cs.titotrainer.app.model.titokone.StaticSynchronizedTitokoneFacade;
import fi.helsinki.cs.titotrainer.app.model.titokone.TitokoneState;

/**
 * <p>A superclass for criteria that verify the presence of instructions.</p>
 * 
 * <p>Parses the parameters as a comma-separated list of upper-case opcode names.
 * Any invalid opcodes in the list are rejected during {@link #interpretParameters(String)}.</p>
 */
public abstract class InstructionSetCriterion extends Criterion {
    
    /**
     * The opcode names given as parameters.
     */
    protected List<String> opcodes = new LinkedList<String>();
    
    protected InstructionSetCriterion() {
    }
    
    protected InstructionSetCriterion(String... opcodes) {
        this.opcodes.addAll(Arrays.asList(opcodes));
        this.setParameters(this.reconstructParameters());
    }
    
    @Override
    @Transient
    protected String reconstructParameters() {
        return StringUtils.join(opcodes, ',');
    }
    
    /*
     * We can't look at opcodes from the end state's memory, because that includes
     * codes from the task's precode and postcode.
     * Instead we reparse the user's code here.
     */
    protected String[] getOpcodesFromUserCode() {
        // Breaking module barriers badly here, but that's caused by the potential non-reentrancy of Titokone.
        return StaticSynchronizedTitokoneFacade.extractCodeFragmentOpcodesStatic(this.getUserCode());
    }
    
    @Override
    protected void interpretParameters(String parameters) throws Exception {
        
        List<String> opcodes = new LinkedList<String>();
        
        String[] parts = StringUtils.split(parameters, ',');
        for (String part : parts) {
            String opcode = part.trim();
            
            if (!TitokoneState.getOpcodeNumbers().containsKey(opcode))
                throw new IllegalArgumentException(opcode + " is not a valid opcode");
            opcodes.add(opcode);
        }
        
        this.opcodes = opcodes;
    }
    
}
