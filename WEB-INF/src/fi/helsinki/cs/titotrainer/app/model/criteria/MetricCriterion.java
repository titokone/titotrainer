package fi.helsinki.cs.titotrainer.app.model.criteria;

/**
 * Superclass for code metric criteria.
 */
public abstract class MetricCriterion extends ConstantComparisonCriterion {
    
    protected MetricCriterion() {
    }
    
    protected MetricCriterion(Relation rel, long rightValue) {
        super(rel, rightValue);
        setParameters(this.reconstructParameters());
    }
    
    @Override
    protected String getValueForMessageParPlaceholder() {
        return "" + getRightParameter();
    }
    
    /**
     * A {@link MetricCriterion}'s left parameter is always empty.
     */
    @Override
    protected String getLeftParameter() {
        return "";
    }
    
    /**
     * {@link MetricCriterion}'s left parameter is ignored.
     */
    @Override
    public void setLeftParameter(String leftParam) {
    }

}
