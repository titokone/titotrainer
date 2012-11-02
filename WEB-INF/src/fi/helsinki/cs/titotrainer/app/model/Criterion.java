package fi.helsinki.cs.titotrainer.app.model;

import static fi.helsinki.cs.titotrainer.app.model.misc.ArgumentUtils.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.log4j.Logger;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.validator.NotNull;

import fi.helsinki.cs.titotrainer.app.model.misc.ArgumentUtils;
import fi.helsinki.cs.titotrainer.app.model.titokone.TitokoneState;
import fi.helsinki.cs.titotrainer.framework.model.Bidirectional;

/**
 * Represents one row in the table "criterion".
 */
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING, name = "type")
@DiscriminatorValue("GENERAL")
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "criterion")
abstract public class Criterion extends AbstractTitoEntity {

    ////////////////////
    // PRIVATE FIELDS //
    ////////////////////
    
    private TString                acceptMessage;
    private long                   id;
    private Input                  input;
    private boolean                isQualityCriterion;
    private String                 parameters;
    private boolean                parametersValid;
    private TString                rejectMessage;
    private Task                   task;
    private String                 userCode; // transient, a bit hackish
    private Collection<Validation> validations;

    ///////////////////////
    // PROTECTED METHODS //
    ///////////////////////
    
    /**
     * <p>Validates the parameters and sets the internal state of a subclass.</p>
     * 
     * <p>Called by {@link #setParameters(String)}.</p>
     * 
     * @param parameters The parameters as passed to {@link #setParameters(String)}, except null is translated to an empty string.
     * @throws Exception if the parameters are invalid.
     */
    protected abstract void interpretParameters(String parameters) throws Exception;
    
    /**
     * <p>This is called after a successful {@link #interpretParameters(String)}
     * to return a clean parameter string with e.g. useless whitespace stripped off.</p>
     * 
     * <p>Although this method should not throw exceptions, nothing bad will happen if it does.</p>
     * 
     * <p>If this method returns null, it is interpreted as an empty string.</p>
     * 
     * @return A clean parameter string constructed from the internal state of the subclass.
     */
    protected String reconstructParameters() {
        return this.parameters;
    }
    
    /**
     * Called by {@link #isSatisfied(TitokoneState, TitokoneState)}
     * if the parameters were valid.
     * 
     * @param state The TitoKone state left by running the student's program.
     * @param modelState The TitoKone state left by running the model solution.
     *                   Null if there was no model state (which shall cause
     *                   criteria that need it to return false).
     * @return Whether the criterion was satisfied.
     */
    protected abstract boolean checkIsSatisfied(TitokoneState state, TitokoneState modelState);
    
    //////////////////
    // CONSTRUCTORS //
    //////////////////
    
    /**
     * Constructs an empty criterion with all fields unset.
     */
    public Criterion() {
        this.parameters = "";
        this.parametersValid = false;
        this.userCode = "";
    }
    
    /**
     * Constructs an otherwise unspecified criterion for a given task.
     * 
     * @param task The task this criterion shall be applied to 
     */
    public Criterion(Task task) {
        this();
        this.task = task;
    }
    
    /**
     * Constructs a complete criterion with all relevant fields set.
     * 
     * @param task The task this criterion shall be applied to
     * @param acceptMessage The feedback if the criterion is satisfied
     * @param rejectMessage The feedback if the criterion is not satisfied
     * @param parameters The data describing the details of the criterion
     */
    public Criterion(Task task, TString acceptMessage, TString rejectMessage, String parameters) {
        this(task);
        this.acceptMessage = acceptMessage;
        this.rejectMessage = rejectMessage;
        this.setParameters(parameters);
    }

    ///////////////
    // ACCESSORS //
    ///////////////
    
    
    /**
     * Returns the message to be used if the criterion was satisfied.
     * 
     * @return The message to be shown if the criterion was satisfied.
     */
    @Cascade( { CascadeType.ALL } )
    @JoinColumn(name = "acceptmessageid")
    @OneToOne
    public TString getAcceptMessage() {
        return this.acceptMessage;
    }
    
    /**
     * Returns a localized accept message.
     * 
     * @param locale The locale to return an accept message for. 
     * @return The localized accept message
     */
    public String getAcceptMessage(Locale locale) {
        if (this.acceptMessage != null) {
            return this.acceptMessage.get(locale);
        }
        return null;
    }
    
    /**
     * Returns the reject message as it is to be presented to the user i.e.
     * with placeholders filled in.
     */
    public String getAcceptMessageForUser(Locale locale) {
        return subsMessagePlaceholders(ArgumentUtils.emptyOnNull(getAcceptMessage(locale)));
    }
    
    /**
     * Sets the message to be used if the criterion was satisfied.
     * 
     * @param acceptMessage The message to be used if the criterion was satisfied. 
     */
    public void setAcceptMessage(TString acceptMessage) {
        this.acceptMessage = acceptMessage;
    }
    
    /**
     * @param locale
     * @param acceptMessage
     */
    public void setAcceptMessage(Locale locale, String acceptMessage) {
        validateLocaleArgument(locale, "locale", LOCALE_CONSTRAINT_NOT_NULL);
        acceptMessage = nullifyOnEmpty(acceptMessage);
        if (this.acceptMessage == null) {
            this.acceptMessage = new TString();
        }
        this.acceptMessage.set(locale, acceptMessage);
    }

    /**
     * Returns the unique id of this criterion.
     * 
     * @return The unique numerical id of this criterion.
     */    
    @Id
    @GeneratedValue
    @Override
    public long getId() {
        return this.id;
    }

    /**
     * Set the id for this criterion. This method should never be invoked directly, assigning an
     * id to an answer is taken care of by the persistence framework. 
     * 
     * @param id The new id of this criterion.
     */
    @Override
    public void setId(long id) {
        this.id = id;
    }
    
    /**
     * Returns the input, if any, this criterion is meant for.
     */
    @ManyToOne
    @JoinColumn(name = "inputId")
    @Bidirectional
    public Input getInput() {
        return this.input;
    }
    
    public void setInput(Input input) {
        this.input = input;
    }
    
    /**
     * Returns the criterion's parameter string as it was set using {@link #setParameters(String)}. Not null.
     */
    @NotNull
    public final String getParameters() {
        return this.parameters;
    }
    
    /**
     * Sets the criterion's parameters. Even invalid parameters may be set.
     * 
     * @param parameters Any parameter string. Null is translated to an empty string.
     * @see #parametersValid()
     */
    public final void setParameters(String parameters) {
        if (parameters == null)
            parameters = "";
        
        this.parameters = parameters;
        this.parametersValid = true;
        try {
            this.interpretParameters(parameters);
        } catch (Exception e) {
            this.parametersValid = false;
            return;
        }
        
        try {
            this.parameters = this.reconstructParameters();
            if (this.parameters == null)
                this.parameters = "";
        } catch (RuntimeException e) {
            Logger.getLogger(this.getClass()).warn("reconstructParameters failed in setParameters(\"" + parameters + "\").", e);
        }
    }
    
    /**
     * Returns whether this is a quality criterion i.e.
     * whether an answer can be accepted even though this criterion
     * is not satisfied.
     */
    public boolean isQualityCriterion() {
        return isQualityCriterion;
    }
    
    public void setQualityCriterion(boolean isQualityCriterion) {
        this.isQualityCriterion = isQualityCriterion;
    }
    
    /**
     * Returns the message to be used if the criterion was not satisfied.
     * 
     * @return The message to be shown if the criterion was not satisfied.
     */    
    @Cascade( { CascadeType.ALL } )
    @JoinColumn(name = "rejectmessageid")
    @OneToOne
    public TString getRejectMessage() {
        return this.rejectMessage;
    }
    
    /**
     * @param locale
     * @return
     */
    public String getRejectMessage(Locale locale) {
        if (this.rejectMessage != null) {
            return this.rejectMessage.get(locale);
        }
        return null;
    }

    /**
     * Returns the reject message as it is to be presented to the user i.e.
     * with placeholders filled in.
     */
    public String getRejectMessageForUser(Locale locale) {
        return subsMessagePlaceholders(ArgumentUtils.emptyOnNull(getRejectMessage(locale)));
    }

    /**
     * Sets the message to be used if the criterion was satisfied.
     * 
     * @param rejectMessage The message to be used if the criterion was satisfied. 
     */
    public void setRejectMessage(TString rejectMessage) {
        this.rejectMessage = rejectMessage;
    }
    
    /**
     * @param locale
     * @param rejectMessage
     */
    public void setRejectMessage(Locale locale, String rejectMessage) {
        validateLocaleArgument(locale, "locale", LOCALE_CONSTRAINT_NOT_NULL);
        rejectMessage = nullifyOnEmpty(rejectMessage);
        if (this.rejectMessage == null) {
            this.rejectMessage = new TString();
        }
        this.rejectMessage.set(locale, rejectMessage);        
    }
    
    /**
     * Returns the task this criterion shall be applied to.
     * 
     * @return The task this criterion shall be applied to.
     */
    @Bidirectional
    @ManyToOne
    @NotNull
    @JoinColumn(name = "taskid")
    public Task getTask() {
        return this.task;
    }
    
    /**
     * Sets the task this criterion shall be applied to.
     * 
     * @param task The task this criterion shall be applied to.
     */
    public void setTask(Task task) {
        if (task == null) {
            throw new NullPointerException("Argument 'task' must not be null!");
        }
        this.task = task;
    }
    
    /**
     * Returns the user code that was set for some criterion subclasses to analyze during validation.
     * Never null, but possibly empty.
     */
    @Transient
    public String getUserCode() {
        return userCode;
    }
    
    /**
     * Sets the user code for some criterion subclasses to analyze during validation.
     */
    public void setUserCode(String userCode) {
        this.userCode = ArgumentUtils.emptyOnNull(userCode);
    }
    
    @Bidirectional
    @MapKey(name = "id")
    @OneToMany(mappedBy = "criterion")
    @Cascade({CascadeType.DELETE})
    public Collection<Validation> getValidations() {
        if (this.validations == null) {
            this.validations = new HashSet<Validation>();
        }
        return this.validations;
    }
    
    public void setValidations(Collection<Validation> validations) {
        this.validations = validations;
    }

    //////////////////////
    // INSTANCE METHODS //
    //////////////////////

    /**
     * Copies this criterion for the same task.
     * 
     * <h4 class = "implementation">Implementation Note</h4>
     * Internally, this method redirects to {@code deepCopy(null, null, false)}.
     * 
     * @return A true copy of this criterion
     * @see #deepCopy(Task, Input, boolean)
     */
    public Criterion deepCopy() {
        return this.deepCopy(null, null, false);
    }
    
    /**
     * Copies this criterion and attaches it to another task. The deep-copy semantics for a criterion are the following:<br /><br />
     * 
     * <ul>
     *   <li>The id is not copied at all (it's later assigned by the persistence framework)</li>
     *   <li>If {@code targetTask==null}, the old reference to task is copied, otherwise the copy of this criterion refers to {@code targetTask}</li>
     *   <li>{@code acceptMessage} and {@code rejectMessage} are deep-copied</li>
     *   <li>The criterion parameters are copied</li>
     *   <li>If {@code targetTask==null} and {@code targetInput==null}, input reference is copied</li>
     *   <li>If {@code targetTask!=null} and {@code targetInput==null}, the input is deep-copied into {@code targetTask}</li>
     *   <li>Dependent validations are not copied at all</li>
     *   <li>If {@code updateAssociations==true}, the copy is added to the collection of criteria in the "owning" task</li>
     * </ul>
     * 
     * @param targetTask The task the copy of this criterion should be attached to. If {@code null}, the task reference of the source is used.
     * @param targetInput The input to be used with the copy of the criterion. If {@code null}, either the old input reference is used or the input is duplicated into the target task.
     * @param updateAssociations If {@code true}, bidirectional associations are automatically updated 
     * @return A true copy of this criterion
     */
    public Criterion deepCopy(Task targetTask, Input targetInput, boolean updateAssociations) {
        Criterion copy = null;
        
        try {
            copy = this.getClass().newInstance();
        } catch (Exception exception) {
            throw new IllegalStateException("Deep-Copying a criterion failed due to an exception on initialization of the copy!", exception);
        }
        
        copy.setParameters(this.getParameters());
        /* Deep-Copy dependent TString's */
        copy.setAcceptMessage(copyOrNull(this.acceptMessage));
        copy.setRejectMessage(copyOrNull(this.rejectMessage));
        
        /* Determine targetTask & targetInput */
        if ((targetTask == null) || targetTask.equals(this.task)) {
            targetTask = this.task;
            /* Don't copy the input */
            if (targetInput == null) {
                targetInput = this.input;
            }
        } else {
            /* If targetInput is null, just use the existing input */
            if ((targetInput == null) && (this.input != null)) {
                targetInput = this.input.deepCopy(targetTask, true);
            }
        }
        
        /* Check for clashes in Task & Input */
        if ((targetInput != null) && (!targetInput.getTask().equals(targetTask))) {
            throw new IllegalArgumentException("The argument 'targetInput' clashes with 'targetTask'");
        }

        /* Set Task & Input */
        copy.setTask(targetTask);
        copy.setInput(targetInput);
        
        /* Set quality criterion status */
        copy.setQualityCriterion(this.isQualityCriterion());
        
        /* Update bidirectional associations if requested */
        if (updateAssociations) {
            copy.getTask().getCriteria().add(copy);
            if (copy.getInput() != null) {
                copy.getTask().getInputs().add(copy.getInput());
            }
        }
        
        return copy;
    }
    
    /**
     * Determines if this criterion is completely localized for a given locale.
     * 
     * @param locale The locale for which to determine if the localization of this criterion is complete. Must not be {@code null}.
     * @return {@code true} - If this criterion is completely localized for the given locale<br />
     *         {@code false} - Otherwise
     * @throws NullPointerException - If the argument {@code locale} is {@code null} 
     */
    @Override
    public boolean hasCompleteTranslation(Locale locale) {
        validateLocaleArgument(locale, "locale", LOCALE_CONSTRAINT_NOT_NULL);
        if ((this.acceptMessage == null) || (this.acceptMessage.get(locale) == null)) {
            return false;
        }
        if ((this.rejectMessage == null) || (this.rejectMessage.get(locale) == null)) {
            return false;
        }
        return true;
    }
    
    @Transient
    protected String getValueForMessageParPlaceholder() {
        return this.getParameters();
    }
    
    /**
     * Substitutes the '%par' placeholder in a message.
     */
    public String subsMessagePlaceholders(String msgTemplate) {
        String msg = msgTemplate;
        msg = msg.replace("%par", this.getValueForMessageParPlaceholder());
        return msg;
    }

    /**
     * Returns whether the parameters last set using {@link #setParameters(String)} were valid.
     */
    public final boolean parametersValid() {
        return this.parametersValid;
    }
    
   /**
    * Checks whether the criterion is satisfied by the given TitoKone state.
    * 
    * @param state The TitoKone state left by running the student's program.
    * @param modelState The TitoKone state left by running the model solution.
    *                   Null if there was no model state (which shall cause
    *                   criteria that need it to return false).
    * @return Whether the criterion was satisfied.
    *         Returns false if the parameters have not been set or were invalid.
    */
   @Transient
   public final boolean isSatisfied(TitokoneState state, TitokoneState modelState) {
       if (!parametersValid())
           return false;
       else
           return checkIsSatisfied(state, modelState);
   }

}