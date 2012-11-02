package fi.helsinki.cs.titotrainer.app.model.criteria;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import fi.helsinki.cs.titotrainer.app.model.misc.ArgumentUtils;
import fi.helsinki.cs.titotrainer.app.model.titokone.TitokoneState;

/**
 * A criterion that compares the value of a symbol to a constant.
 */
@DiscriminatorValue("SYMBOL")
@Entity
public class SymbolCriterion extends ConstantComparisonCriterion {
    
    private String symbolName;
    
    /**
     * The default constructor.
     */
    public SymbolCriterion() {
    }
    
    /**
     * A convenience constructor.
     * 
     * @param sym The symbol name.
     * @param rel The relation to use.
     * @param rightValue The right hand side value.
     */
    public SymbolCriterion(String sym, Relation rel, long rightValue) {
        this.setLeftParameter(sym);
        this.setRelation(rel);
        this.setRightParameter(rightValue);
        this.setParameters(this.reconstructParameters());
    }
    
    @Override
    @Transient
    protected String getLeftParameter() {
        return ArgumentUtils.emptyOnNull(symbolName);
    }
    
    @Override
    public void setLeftParameter(String leftParam) {
        this.symbolName = leftParam;
    }
    
    @Override
    @Transient
    protected long getLeftParameterValue(TitokoneState state) {
        Integer addr = state.getSymbols().get(symbolName);
        if (addr == null)
            throw new IllegalArgumentException("No symbol '" + symbolName + "'");
        
        int[] memory = state.getMemory();
        if (addr < 0 || addr >= memory.length)
            throw new IllegalArgumentException("Value of symbol '" + symbolName + "' out of bounds");
        
        return memory[addr];
    }
    
}
