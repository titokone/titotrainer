package fi.helsinki.cs.titotrainer.app.model.criteria;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Transient;

import fi.helsinki.cs.titotrainer.app.model.Criterion;
import fi.helsinki.cs.titotrainer.app.model.titokone.TitokoneState;

/**
 * <p>A superclass to criteria that compare a variable to a constant
 * using a {@link Relation}.</p>
 * 
 * <p>The parameters must consist of
 * <ul>
 * <li>possibly a subclass-defined value (e.g. a register name)</li>
 * <li>a relation</li>
 * <li>a constant integer</li>
 * </ul>
 * 
 * The parameters must be separated by spaces.
 * </p>
 * 
 * <p>
 * Examples:
 * <pre>
 * x = 33
 * R0 &lt; 8
 * &lt;= 99
 * </pre>
 * </p>
 */
public abstract class ConstantComparisonCriterion extends Criterion {
    
    private static final Pattern paramPattern = Pattern.compile("^(?:([^\\s]+)\\s+)?\\s*(<|<=|>|>=|=|!=)\\s+(-?\\d*)$");
    
    /**
     * The relation.
     */
    protected Relation relation;
    
    /**
     * The constant to compare with.
     */
    protected long rightParameter;
    
    protected ConstantComparisonCriterion() {
    }
    
    protected ConstantComparisonCriterion(Relation relation, long rightParameter) {
        this.relation = relation;
        this.rightParameter = rightParameter;
    }
    
    @Override
    protected String reconstructParameters() {
        assert(this.getLeftParameter() != null);
        if (relation != null)
            return this.getLeftParameter() + " " + relation.toSymbol() + " " + rightParameter;
        else
            return this.getLeftParameter() + " " + rightParameter;
    }
    
    @Override
    protected void interpretParameters(String parameters) throws Exception {
        Matcher matcher = paramPattern.matcher(parameters);
        if (!matcher.matches())
            throw new IllegalArgumentException("Parameters format not valid: '" + parameters + "'.");
        
        String leftParameter = matcher.group(1);
        if (leftParameter == null)
            leftParameter = "";
        this.setLeftParameter(leftParameter);
        this.relation = Relation.fromSymbol(matcher.group(2));
        this.rightParameter = Long.parseLong(matcher.group(3));
    }
    
    public Relation getRelation() {
        return relation;
    }
    
    public void setRelation(Relation relation) {
        this.relation = relation;
    }
    
    public long getRightParameter() {
        return rightParameter;
    }
    
    public void setRightParameter(long rightParameter) {
        this.rightParameter = rightParameter;
    }
    
    @Override
    @Transient
    protected String getValueForMessageParPlaceholder() {
        return "" + getLeftParameter();
    }
    
    /**
     * Returns the left parameter as a string.
     * 
     * @return A non-null string form of the left parameter.
     */
    protected abstract String getLeftParameter();
    
    /**
     * Parses and sets the left parameter.
     * 
     * @param leftParam The parameter that will be in the left side of the relation. Never null -- empty if none set.
     * @throws IllegalArgumentException if the parameter was in an incorrect format.
     */
    public abstract void setLeftParameter(String leftParam);
    
    @Override
    protected boolean checkIsSatisfied(TitokoneState state, TitokoneState modelState) {
        long leftParameter;
        try {
            leftParameter = getLeftParameterValue(state);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return this.relation.test(leftParameter, rightParameter);
    }
    
    /**
     * Returns the value of the left parameter as it is to be used
     * in the comparison.
     * 
     * @param state The titokone state.
     * @return The value of the left parameter.
     * @throws IllegalArgumentException If the value could not be determined. This causes the criterion to not be satisfied.
     */
    protected abstract long getLeftParameterValue(TitokoneState state);
}
