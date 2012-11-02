package fi.helsinki.cs.titotrainer.app.model.criteria;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import fi.helsinki.cs.titotrainer.app.model.titokone.TitokoneState;

/**
 * A criterion that compares a register (R0-R7) value to a constant.
 */
@DiscriminatorValue("REGISTER")
@Entity
public class RegisterCriterion extends ConstantComparisonCriterion {
    
    private int regNum;
    
    /**
     * The default constructor that initializes nothing.
     */
    public RegisterCriterion() {
    }
    
    /**
     * A convenience constructor.
     * 
     * @param regNum The general-purpose register's number.
     * @param rel The relation to use.
     * @param rightValue The right hand side value.
     */
    public RegisterCriterion(int regNum, Relation rel, long rightValue) {
        this.setLeftParameter("R" + regNum);
        this.setRelation(rel);
        this.setRightParameter(rightValue);
        this.setParameters(this.reconstructParameters());
    }
    
    @Override
    @Transient
    protected String getLeftParameter() {
        return "R" + regNum;
    }
    
    @Override
    public void setLeftParameter(String leftParam) {
        this.regNum = RegisterCriterionCommon.parseRegNum(leftParam);
    }

    @Override
    @Transient
    protected long getLeftParameterValue(TitokoneState state) {
        return state.getGeneralRegisters()[regNum];
    }

}
