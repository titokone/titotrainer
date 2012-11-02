package fi.helsinki.cs.titotrainer.app.model.criteria;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import fi.helsinki.cs.titotrainer.app.model.titokone.TitokoneState;

/**
 * <p>A criterion that compares a register's value to one from the model answer.</p>
 * 
 * <p>The parameter is the register name and a relation.</p>
 */
@DiscriminatorValue("MODELREGISTER")
@Entity
public class ModelRegisterCriterion extends ModelComparisonCriterion {

    private int regNum;
    
    public ModelRegisterCriterion() {
    }
    
    public ModelRegisterCriterion(int regNum, Relation rel) {
        this.setRelation(rel);
        this.setLeftParameter("R" + regNum);
        this.setParameters(this.reconstructParameters());
    }
    
    @Override
    @Transient
    public String getLeftParameter() {
        return "R" + regNum;
    }
    
    @Override
    public void setLeftParameter(String leftParam) {
        this.regNum = RegisterCriterionCommon.parseRegNum(leftParam);
    }

    @Override
    @Transient
    protected long getParameterValue(TitokoneState state) {
        return state.getGeneralRegisters()[regNum];
    }
    
}
