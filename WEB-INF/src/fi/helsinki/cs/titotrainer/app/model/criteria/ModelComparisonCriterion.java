package fi.helsinki.cs.titotrainer.app.model.criteria;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Transient;

import fi.helsinki.cs.titotrainer.app.model.Criterion;
import fi.helsinki.cs.titotrainer.app.model.titokone.TitokoneState;

/**
 * <p>A superclass to criteria that compare a variable's value
 * to one from the model solution using a {@link Relation}.</p>
 * 
 * <p>The parameters must consist of
 * <ul>
 * <li>possibly a subclass-defined value (e.g. a register name)</li>
 * <li>a relation</li>
 * </ul>
 * 
 * The parameters must be separated by spaces.
 * </p>
 * 
 * <p>
 * Examples:
 * <pre>
 * x =
 * R0 &lt;
 * &lt;=
 * </pre>
 * </p>
 */
public abstract class ModelComparisonCriterion extends Criterion {
    
    private static final Pattern paramPattern = Pattern.compile("^(?:([^\\s]+)\\s+)?(<|<=|>|>=|=|!=)$");
    
    /**
     * The relation.
     */
    protected Relation relation;
    
    @Override
    protected String reconstructParameters() {
        assert(this.getLeftParameter() != null);
        if (relation != null)
            return this.getLeftParameter() + " " + relation.toSymbol();
        else
            return this.getLeftParameter();
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
    }
    
    public Relation getRelation() {
        return relation;
    }
    
    public void setRelation(Relation relation) {
        this.relation = relation;
    }
    
    /**
     * Returns the left parameter as a string.
     * 
     * @return A non-null string form of the left parameter.
     */
    public abstract String getLeftParameter();
    
    /**
     * Parses and sets the left parameter.
     * 
     * @param leftParam The parameter that will be in the left side of the relation. Never null -- empty if none set.
     * @throws IllegalArgumentException if the parameters were in an incorrect format.
     */
    public abstract void setLeftParameter(String leftParam);
    
    @Override
    @Transient
    protected String getValueForMessageParPlaceholder() {
        return "" + getLeftParameter();
    }
    
    @Override
    protected boolean checkIsSatisfied(TitokoneState state, TitokoneState modelState) {
        if (modelState == null)
            return false;
        
        long stateValue;
        long modelValue;
        try {
            stateValue = getParameterValue(state);
            modelValue = getParameterValue(modelState);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return this.relation.test(stateValue, modelValue);
    }
    
    /**
     * Returns the value of the parameter as it is to be used
     * in the comparison.
     * 
     * @param state The titokone state.
     * @return The value of the parameter.
     * @throws IllegalArgumentException If the value could not be determined. This causes the criterion to not be satisfied.
     */
    protected abstract long getParameterValue(TitokoneState state);
}
