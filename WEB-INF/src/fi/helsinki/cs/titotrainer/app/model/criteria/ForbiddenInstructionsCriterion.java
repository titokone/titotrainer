package fi.helsinki.cs.titotrainer.app.model.criteria;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import fi.helsinki.cs.titotrainer.app.model.titokone.TitokoneState;

@DiscriminatorValue("FORBIDDENINSTRUCTIONS")
@Entity
public class ForbiddenInstructionsCriterion extends InstructionSetCriterion {
    
    public ForbiddenInstructionsCriterion() {
    }
    
    public ForbiddenInstructionsCriterion(String ... opcodes) {
        super(opcodes);
    }
    
    @Override
    protected boolean checkIsSatisfied(TitokoneState state, TitokoneState modelState) {
        for (String opcode : getOpcodesFromUserCode()) {
            if (this.opcodes.contains(opcode))
                return false;
        }
        return true;
    }
}
