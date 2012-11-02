package fi.helsinki.cs.titotrainer.app.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.validator.NotNull;

import fi.helsinki.cs.titotrainer.framework.model.Bidirectional;

/**
 * <p>Indicates whether an answer satisfied a criterion for an input.</p>
 */
@Entity
@Table(name = "validation")
public class Validation extends AbstractTitoEntity {

    ////////////////////
    // PRIVATE FIELDS //
    ////////////////////
    
    private Answer    answer;
    private Criterion criterion;
    private long      id;
    private Input     input;
    private boolean   satisfied;

    //////////////////
    // CONSTRUCTORS //
    //////////////////
    
    /**
     * Creates a new validation with all fields unset. 
     */
    public Validation() {
    }

    ///////////////
    // ACCESSORS //
    ///////////////
    
    /**
     * Returns the answer this validation applies to.
     * 
     * @return The answer this validation applies to.
     */
    @Bidirectional
    @JoinColumn(name = "answerId")
    @ManyToOne
    @NotNull
    public Answer getAnswer() {
        return this.answer;
    }
    
    /**
     * Sets the answer this validation applies to.
     * 
     * @param answer The answer this validation applies to.
     */
    public void setAnswer(Answer answer) {
        /* Test for null answer */
        if (answer == null) {
            throw new NullPointerException("Argument 'answer' must not be null!");
        }
        /* Test if answer collides with criterion i.e. if they have different tasks */
        if (this.criterion != null) {
            if (this.criterion.getTask() != answer.getTask()) {
                throw new IllegalArgumentException("The answer you're trying to set has a task different from the one in criterion!");
            }
        }
        /* Test if answer collides with input, i.e. if they have different tasks */
        if (this.input != null) {
            if (this.input.getTask() != answer.getTask()) {
                throw new IllegalArgumentException("The answer you're trying to set has a task different from the one in input!");
            }
        }
        this.answer = answer;
    }
    
    /**
     * Returns the criterion the students solution was tested against.
     * 
     * @return The criterion the students solution was tested against.
     */
    @Bidirectional
    @JoinColumn(name = "criterionId")
    @ManyToOne
    @NotNull
    public Criterion getCriterion() {
        return this.criterion;
    }
    
    /**
     * Sets the criterion that was used in this validation.
     * 
     * @param criterion The criterion that was used for this validation.
     */
    public void setCriterion(Criterion criterion) {
        if (criterion == null) {
            throw new NullPointerException("Argument 'criterion' must not be null!");
        }
        /* Test if criterion collides with answer, i.e. if they apply to different tasks. */
        if (this.answer != null) {
            if (this.answer.getTask() != criterion.getTask()) {
                throw new IllegalArgumentException("The criterion you're trying to set has a task different from the one in answer!");
            }
        }
        /* Test if criterion collides with input, i.e. if they apply to different tasks. */
        if (this.input != null) {
            if (this.input.getTask() != criterion.getTask()) {
                throw new IllegalArgumentException("The criterion you're trying to set has a task different from the one in input!");
            }
        }
        this.criterion = criterion;
    }
    
    /**
     * Returns the unique id of this validation.
     * 
     * @return The unique id of this validation.
     */
    @Id
    @GeneratedValue
    @Override
    public long getId() {
        return this.id;
    }
    
    /**
     * Sets the id of this validation. This method should never be called directly, assigning an
     * id to a validation is taken care of by the persistence framework.
     * 
     * @param id The new id of this validation.
     */
    @Override
    public void setId(long id) {
        this.id = id;
    }
    
    /**
     * Returns the input that was used in this validation.
     * 
     * @return The input that was used in this validation.
     */
    @Bidirectional
    @JoinColumn(name = "inputId")
    @ManyToOne
    public Input getInput() {
        return this.input;
    }
    
    /**
     * Sets the input used in this validation.
     * 
     * @param input The input used in this validation.
     */
    public void setInput(Input input) {
        /* Test if input collides with answer, i.e. if they have different tasks */
        if (this.answer != null && input != null) {
            if (this.answer.getTask() != input.getTask()) {
                throw new IllegalArgumentException("The input you're trying to set has a task different from the one in answer!");
            }
        }
        /* Test if input collides with criterion, i.e. if they have different tasks */
        if (this.criterion != null && input != null) {
            if (this.criterion.getTask() != input.getTask()) {
                throw new IllegalArgumentException("The input you're trying to set has a task different from the one in criterion!");                
            }
        }
        this.input = input;
    }
    
    /**
     * Returns true if the criterion was satisfied by the answer using the given input.
     * 
     * @return {@code true} if the criterion was satisfied by the answer. 
     */
    public boolean isSatisfied() {
        return this.satisfied;
    }
    
    /**
     * Sets the satisfied flag for this validation.
     * 
     * @param satisfied Whether or not the criterion was satisfied. 
     */
    public void setSatisfied(boolean satisfied) {
        this.satisfied = satisfied;
    }

}