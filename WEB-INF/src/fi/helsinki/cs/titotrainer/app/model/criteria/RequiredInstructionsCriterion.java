package fi.helsinki.cs.titotrainer.app.model.criteria;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.apache.commons.lang.ArrayUtils;

import fi.helsinki.cs.titotrainer.app.model.titokone.TitokoneState;

@DiscriminatorValue("REQUIREDINSTRUCTIONS")
@Entity
public class RequiredInstructionsCriterion extends InstructionSetCriterion {
    
    public RequiredInstructionsCriterion() {
    }
    
    public RequiredInstructionsCriterion(String ... opcodes) {
        super(opcodes);
    }
    
    @Override
    protected boolean checkIsSatisfied(TitokoneState state, TitokoneState modelState) {
        String[] usedOpcodes = getOpcodesFromUserCode();
        for (String opcode : this.opcodes) {
            if (!ArrayUtils.contains(usedOpcodes, opcode))
                return false;
        }
        return true;
    }
    
}
