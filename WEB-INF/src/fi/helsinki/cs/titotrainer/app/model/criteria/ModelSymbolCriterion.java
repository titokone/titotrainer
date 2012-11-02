package fi.helsinki.cs.titotrainer.app.model.criteria;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import fi.helsinki.cs.titotrainer.app.model.misc.ArgumentUtils;
import fi.helsinki.cs.titotrainer.app.model.titokone.TitokoneState;

/**
 * A criterion that compares the value of a symbol to a constant.
 */
@DiscriminatorValue("MODELSYMBOL")
@Entity
public class ModelSymbolCriterion extends ModelComparisonCriterion {
    
    private String symbolName;
    
    public ModelSymbolCriterion() {
    }
    
    public ModelSymbolCriterion(String symbolName, Relation rel) {
        this.setRelation(rel);
        this.setLeftParameter(symbolName);
        this.setParameters(this.reconstructParameters());
    }
    
    @Override
    @Transient
    public String getLeftParameter() {
        return ArgumentUtils.emptyOnNull(symbolName);
    }
    
    @Override
    public void setLeftParameter(String leftParam) {
        this.symbolName = leftParam;
    }
    
    @Override
    @Transient
    protected long getParameterValue(TitokoneState state) {
        Integer addr = state.getSymbols().get(symbolName);
        if (addr == null)
            throw new IllegalArgumentException("No symbol '" + symbolName + "'");
        
        int[] memory = state.getMemory();
        if (addr < 0 || addr >= memory.length)
            throw new IllegalArgumentException("Value of symbol '" + symbolName + "' out of bounds");
        
        return memory[addr];
    }
    
}
