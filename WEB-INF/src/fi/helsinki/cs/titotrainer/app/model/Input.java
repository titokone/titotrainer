package fi.helsinki.cs.titotrainer.app.model;

import static fi.helsinki.cs.titotrainer.app.model.misc.ArgumentUtils.*;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.validator.NotNull;

import fi.helsinki.cs.titotrainer.framework.model.Bidirectional;

/**
 * <p>Represents one row in the table "input". Input is basically a string given to the solution of a task to
 * test it for correctness.</p>
 * 
 * <p>{@link Comparable} comparisons are done on the input string.</p>
 */
@Entity
@Table(name = "input")
public class Input extends AbstractTitoEntity {

    ////////////////////
    // PRIVATE FIELDS //
    ////////////////////
    
    private long                   id;
    private String                 input;
    private boolean                secret;
    private Task                   task;
    private Collection<Criterion>  criteria;

    //////////////////
    // CONSTRUCTORS //
    //////////////////
    
    /**
     * Constructs an input with all fields unset.
     */
    public Input() {
    }
    
    /**
     * Constructs an input with all relevant fields set. The values of the fields are given as parameters to
     * the constructor.
     * 
     * @param task The task this input should be used for.
     * @param input The input to be used to test the solution to the task. 
     * @param secret A flag denoting if the input is visible to the user.
     */
    public Input(Task task, String input, boolean secret) {
        this.task = task;
        this.input = input;
        this.secret = secret;
    }

    ///////////////
    // ACCESSORS //
    ///////////////

    /**
     * Returns the unique id of this input.
     * 
     * @return The unique numerical id of this input.
     */    
    @Id
    @GeneratedValue
    @Override
    public long getId() {
        return this.id;
    }
    
    /**
     * Set the id for this input. This method should never be called directly, assigning an
     * id to an input is taken care of by the persistence framework. 
     * 
     * @param id The new id of this input.
     */
    @Override
    public void setId(long id) {
        this.id = id;
    }
        
    /**
     * Returns the input to a task.
     * 
     * @return The input to a task.
     */
    @NotNull
    public String getInput() {
        return this.input;
    }
    
    @Transient
    public int[] getInputNumbers() {
        String[] parts = StringUtils.split(this.getInput(), ',');
        int[] result = new int[parts.length];
        for (int i = 0; i < parts.length; ++i) {
            result[i] = Integer.parseInt(parts[i].trim());
        }
        return result;
    }
    
    /**
     * Sets the input to a task. The input must not be null.
     *  
     * @param input The input to test the solution to a task with.
     */
    public void setInput(String input) {
        validateStringArgument(input, "input", STRING_CONSTRAINT_NOT_NULL);
        input = input.trim();
        this.input = input;
    }
    
    /**
     * Returns true if the input is considered "secret". A secret input must not be shown to a student
     * solving the task.
     * 
     * @return {@code true} if the input is "secret".
     */
    public boolean isSecret() {
        return this.secret;
    }
    
    /**
     * Sets the secrecy status of the input.
     * 
     * @param secret {@code true} if the input should be secret. 
     */
    public void setSecret(boolean secret) {
        this.secret = secret;
    }
    
    /**
     * Returns the task this input applies to.
     * 
     * @return The task this input shall be used with.
     */
    @Bidirectional
    @ManyToOne
    @JoinColumn(name = "taskId", nullable = false)
    public Task getTask() {
        return this.task;
    }
    
    /**
     * Sets the task this input shall be used with.
     * 
     * @param task The task this input shall be used with.
     */
    public void setTask(Task task) {
        if (task == null) {
            throw new NullPointerException("Argument 'task' must not be null!");
        }
        this.task = task;
    }
    
    /**
     * Returns the criteria that are specific to this input.
     */
    @Bidirectional
    @MapKey(name = "id")
    @OneToMany(mappedBy = "input")
    @Cascade({CascadeType.DELETE})
    public Collection<Criterion> getCriteria() {
        if (this.criteria == null) {
            this.criteria = new HashSet<Criterion>();
        }
        return this.criteria;
    }
    
    public void setCriteria(Collection<Criterion> criteria) {
        this.criteria = criteria;
    }
    
    //////////////////////
    // INSTANCE METHODS //
    //////////////////////

    /**
     * Copies this input for the same task.
     * 
     * <h4 class = "implementation">Implementation Note</h4>
     * Internally, this method redirects to {@code deepCopy(null, false)}.
     * 
     * @return A true copy of this input
     * @see #deepCopy(Task, boolean)
     */
    public Input deepCopy() {
        return this.deepCopy(null, false);
    }
    
    /**
     * Copies this input and attaches it to another task. The deep-copy semantics for an input are the following:<br /><br />
     * 
     * <ul>
     *   <li>The id is not copied at all (it's later assigned by the persistence framework)</li>
     *   <li>If {@code targetTask == null}, the old reference to task is copied, otherwise the copy of this input refers to {@code targetTask}</li>
     *   <li>The secrecy status is copied</li>
     *   <li>The input is copied</li>
     *   <li>Dependent validations are not copied at all</li>
     *   <li>If {@code updateAssociations==true}, the copy is added to the collection of inputs in the "owning" task</li>
     * </ul>
     * 
     * @param targetTask The task the copy of this input should be attached to. If {@code null}, the task reference of the source is used
     * @param updateAssociations If {@code true}, bidirectional associations are automatically updated 
     * @return A true copy of this input
     */
    public Input deepCopy(Task targetTask, boolean updateAssociations) {
        Input copy = new Input();
        
        copy.setInput(this.input);
        copy.setSecret(this.secret);
        /* Use target-task if provided, otherwise copy the old reference */
        if (targetTask == null) {
            copy.setTask(this.task);
        } else {
            copy.setTask(targetTask);
        }
        /* Update bidirectional associations if requested */
        if (updateAssociations) {
            copy.getTask().getInputs().add(copy);            
        }
        
        return copy;
    }
    
}