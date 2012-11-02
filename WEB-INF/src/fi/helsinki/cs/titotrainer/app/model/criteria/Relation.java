/**
 * 
 */
package fi.helsinki.cs.titotrainer.app.model.criteria;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;

@SuppressWarnings("unchecked")
public enum Relation {
    LT, LTE, GT, GTE, EQ, INEQ;
    
    private static final Map<Relation, String> symbolMap;
    private static final Map<String, Relation> reverseSymbolMap;
    
    static {
        symbolMap = new HashMap<Relation, String>();
        symbolMap.put(LT, "<");
        symbolMap.put(LTE, "<=");
        symbolMap.put(GT, ">");
        symbolMap.put(GTE, ">=");
        symbolMap.put(EQ, "=");
        symbolMap.put(INEQ, "!=");
        
        reverseSymbolMap = MapUtils.invertMap(symbolMap);
    }
    
    /**
     * Returns a symbolic notation of the enumeration value.
     * E.g. <code>LT</code> gives <code>&lt;</code>.
     */
    public String toSymbol() {
        return symbolMap.get(this);
    }
    
    /**
     * Returns the relation from a symbolic representation.
     * 
     * @param symbol The symbol as a string.
     * @return The relation (or null if symbol is null or not recognized).
     */
    public static Relation fromSymbol(String symbol) {
        if (symbol == null)
            return null;
        return reverseSymbolMap.get(symbol);
    }
    
    /**
     * Evaluates <code>left REL right</code>.
     * 
     * @param left The left parameter.
     * @param right The right parameter.
     * @return Wether the relation holds for the parameters.
     */
    public boolean test(long left, long right) {
        switch (this) {
        case LT:    return left < right;
        case LTE:   return left <= right;
        case GT:    return left > right;
        case GTE:   return left >= right;
        case EQ:    return left == right;
        case INEQ:  return left != right;
        }
        throw new IllegalStateException("test(left, right) not up to date");
    }
    
}