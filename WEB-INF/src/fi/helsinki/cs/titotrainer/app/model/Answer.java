package fi.helsinki.cs.titotrainer.app.model;

import static fi.helsinki.cs.titotrainer.app.model.misc.ArgumentUtils.*;

import java.util.Collection;
import java.util.Date;
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
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;
import org.hibernate.validator.NotNull;

import fi.helsinki.cs.titotrainer.app.model.titokone.TitokoneState.ExitStatus;
import fi.helsinki.cs.titotrainer.framework.model.Bidirectional;

/**
 * <p>An answer is a solution to a task given by a student.</p>
 * 
 * <p>
 * The answer itself contains only the solution without any information about its correctness.
 * The information about how the answer validated against correctness criteria can be found in
 * table "validation", encapsulated in {@link Validation}.
 * </p>
 * 
 * <p>Only one answer may be stored per student.</p>
 */
@Entity
@Table(name = "answer", uniqueConstraints = { @UniqueConstraint(columnNames = {"taskId", "userId"}) })
public class Answer extends AbstractTitoEntity {
    
    ////////////////////
    // PRIVATE FIELDS //
    ////////////////////
    
    private String                 code;
    private boolean                compiled;
    private long                   id;
    private boolean                obsoleted;
    private Task                   task;
    private Date                   timestamp;
    private User                   user;
    private Collection<Validation> validations;
    private Collection<ExecStatus> execStatuses;
    
    //////////////////
    // CONSTRUCTORS //
    //////////////////
    
    /**
     * Constructs an answer with all fields unset. The timestamp of this answer will be set to
     * the current time by default.
     */
    public Answer() {
       this.timestamp = new Date();
       this.obsoleted = false;
    }
    
    /**
     * Constructs a complete new answer. All relevant information is given as parameter to the constructor.
     * The timestamp of this answer is set to the current time.
     * 
     * @param user The user who submitted the answer. 
     * @param task The task this answer is a solution to.
     * @param code The actual code given as the solution to the task.
     */
    public Answer(User user, Task task, String code, boolean compiled) {
        this();
        this.user = user;
        this.task = task;
        this.code = code;
        this.compiled = compiled;
    }
    
    ///////////////
    // ACCESSORS //
    ///////////////
    
    /**
     * Returns the TTK-91 code a student has given as the solution to a task.
     * 
     * @return The TTK-91 code that the student has given as the solution to a task.
     */
    @NotNull
    @Type(type = "text")
    public String getCode() {
        return this.code;
    }

    /**
     * Sets the TTK-91 code a student has given as a solution to the task.
     * 
     * @param code The TTK-91 code a student has given as a solution to a task.
     */
    public void setCode(String code) {
        validateStringArgument(code, "code", STRING_CONSTRAINT_NOT_NULL);
        this.code = code;
    }
    
    /**
     * Whether the answer could be compiled successfully.
     */
    public boolean isCompiled() {
        return compiled;
    }
    
    /**
     * Sets whether the answer could be compiled successfully.
     * @param compiled
     */
    public void setCompiled(boolean compiled) {
        this.compiled = compiled;
    }
    
    /**
     * Returns the unique id of this answer.
     * 
     * @return The unique numerical id of this answer.
     */
    @Id
    @GeneratedValue
    @Override
    public long getId() {
        return this.id;
    }
    
    /**
     * Set the id for this answer. This method should never be called directly, assigning an
     * id to an answer is taken care of by the persistence framework. 
     * 
     * @param id The new id of this answer.
     */
    @Override
    public void setId(long id) {
        this.id = id;
    }
    
    /**
     * Tells whether the answer has been obsoleted by an invasive change to
     * the task an as such must be resubmitted to be validated again.
     */
    @NotNull
    public boolean isObsoleted() {
        return obsoleted;
    }

    /**
     * Sets the obsolecense parameter. See {@link #isObsoleted()}.
     */
    public void setObsoleted(boolean obsoleted) {
        this.obsoleted = obsoleted;
    }
    
    /**
     * Returns the task this answer is a solution to. This field has to be set for the answer to be valid.
     * 
     * @return The task this answer is a solution to. 
     */
    @Bidirectional
    @JoinColumn(name = "taskId")
    @ManyToOne
    @NotNull
    public Task getTask() {
        return this.task;
    }
    
    /**
     * Sets the task this answer is a solution for.
     * 
     * @param task The task this answer provides a solution to.
     */
    public void setTask(Task task) {
        /* Don't accept null */
        if (task == null) {
            throw new NullPointerException("Argument 'task' must not be null!");
        }
        this.task = task;
    }
    
    /**
     * Returns the time this answer was given. In most cases, this corresponds to the time
     * this object was initially created.
     * 
     * @return The time this answer was given.
     */
    @NotNull
    public Date getTimestamp() {
        return this.timestamp;
    }
    
    /**
     * Sets the timestamp for this answer. This method should not be called without a distinct reason as
     * the timestamp for an answer is by default set to the time the object was created. In most cases, this
     * behaviour should be the desired one. 
     * 
     * @param timestamp The new timestamp for this answer.
     */
    public void setTimestamp(Date timestamp) {
        /* Don't accept null as parameter */
        if (timestamp == null) {
            throw new NullPointerException("Argument 'timestamp' must not be null!");
        }
        this.timestamp = timestamp;
    }
    
    /**
     * Returns the {@link User} that submitted this answer. 
     * 
     * @return The user who submitted the answer. 
     */
    @NotNull
    @ManyToOne
    @OnDelete(action=OnDeleteAction.CASCADE)
    @JoinColumn(name = "userId")
    public User getUser() {
        return this.user;
    }
    
    /**
     * Sets the user who submitted this answer.
     * 
     * @param user The user who submitted this answer.
     */
    public void setUser(User user) {
        /* Don't accept null */
        if (user == null) {
            throw new NullPointerException("Argument 'user' must not be null!");
        }
        this.user = user;
    }
    
    @Bidirectional
    @MapKey(name = "id")
    @Cascade({CascadeType.ALL})
    @OneToMany(mappedBy = "answer")
    public Collection<Validation> getValidations() {
        if (this.validations == null) {
            this.validations = new HashSet<Validation>();
        }
        return this.validations;
    }
    
    public void setValidations(Collection<Validation> validations) {
        this.validations = validations;
    }
    
    @Bidirectional
    @MapKey(name = "id")
    @Cascade({CascadeType.ALL})
    @OneToMany(mappedBy = "answer")
    public Collection<ExecStatus> getExecStatuses() {
        if (this.execStatuses == null) {
            this.execStatuses = new HashSet<ExecStatus>();
        }
        return this.execStatuses;
    }
    
    public void setExecStatuses(Collection<ExecStatus> execStatuses) {
        this.execStatuses = execStatuses;
    }

    //////////////////////
    // INSTANCE METHODS //
    //////////////////////
    
    /**
     * <p>Whether the answer compiled, has a successful ExecStatus on each input,
     * all of its non-quality-criterion validations are satisfied and it is not
     * obsoleted.</p>
     */
    @Transient
    public boolean isSuccessful() {
        /*
         * TODO: we should probably cache this instead of loading all execStatuses and validations each time.
         * Loading them eagerly might speed things up as well. Probably less so, but possibly sufficiently.
         */
        
        if (!this.compiled)
            return false;
        if (this.obsoleted)
            return false;
        
        for (ExecStatus execStatus : this.getExecStatuses()) {
            if (execStatus.getExitStatus() != ExitStatus.SUCCESSFUL)
                return false;
        }
        
        for (Validation val : this.getValidations()) {
            if (!val.getCriterion().isQualityCriterion() && !val.isSatisfied())
                return false;
        }
        
        return true;
    }
    
    @Transient
    public boolean hasFailedQualityCriteria() {
        for (Validation val : this.getValidations()) {
            if (val.getCriterion().isQualityCriterion() && !val.isSatisfied())
                return true;
        }
        return false;
    }

    /**
     * Copies this answer. Bidirectional associations are not updated.
     * 
     * <h4 class = "implementation">Implementation Note</h4>
     * Internally, this method redirects to {@code deepCopy(false)}.
     * 
     * @return A true copy of this answer
     * @see #deepCopy(boolean)
     */
    public Answer deepCopy() {
        return deepCopy(false);
    }
    
    /**
     * Copies this answer. The deep-copy semantics for an answer are the following:<br /><br />
     * <ul>
     *   <li>The id is not copied at all (it's later assigned by the persistence framework)</li>
     *   <li>The reference to the user is copied</li>
     *   <li>The reference to the task is copied</li>
     *   <li>The timestamp is duplicated</li>
     *   <li>The code is copied</li>
     *   <li>Dependent validations and execStatuses are not copied at all</li>
     *   <li>If {@code updateAssociations==true}, the copy is added to the collection of answers in the "owning" task</li>
     * </ul>
     * 
     * @param updateAssociations If {@code true}, bidirectional associations are automatically updated 
     * @return A true copy of this answer
     */
    public Answer deepCopy(boolean updateAssociations) {
        Answer copy = new Answer();
        
        copy.setCode(this.code);
        copy.setCompiled(this.compiled);
        copy.setObsoleted(this.obsoleted);
        copy.setTask(this.task);
        copy.setTimestamp(new Date(this.timestamp.getTime()));
        copy.setUser(this.user);
        /* Update bidirectional associations if requested */
        if (updateAssociations) {
            copy.getTask().getAnswers().add(copy);
        }
        
        return copy;
    }

}