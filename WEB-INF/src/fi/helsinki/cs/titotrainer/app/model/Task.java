package fi.helsinki.cs.titotrainer.app.model;

import static fi.helsinki.cs.titotrainer.app.model.misc.ArgumentUtils.*;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.validator.AssertTrue;
import org.hibernate.validator.NotNull;

import fi.helsinki.cs.titotrainer.framework.model.Bidirectional;

/**
 * Represents one row in the table "task". A task is a task in the natural meaning which is to be solved by the students of a course.
 */
@Entity
@Table(name = "task")
public class Task extends AbstractTitoEntity {

    ///////////////////
    // INNER CLASSES //
    ///////////////////
    
    public static enum Type {
        PROGRAMMING, FILL_IN;
        
        public String getName(Locale locale) {
            try {
                ResourceBundle rb = ResourceBundle.getBundle(Task.class.getPackage().getName() + ".Task_Type", locale);
                return rb.getString(this.toString());
            } catch (MissingResourceException e) {
                return this.toString();
            }
        }
    }
    
    //////////////////////
    // PUBLIC CONSTANTS //
    //////////////////////

    public static final int MAX_DESCRIPTION_LENGTH = 1048576;
    public static final int DIFFICULTY_MIN = -1000000;
    public static final int DIFFICULTY_MAX = 1000000;
    public static final int DIFFICULTY_DEFAULT = 0;
    public static final int DEFAULT_MAX_STEPS = 10000;
    
    ////////////////////
    // PRIVATE FIELDS //
    ////////////////////    
    
    private Collection<Answer>    answers;
    private Category              category;
    private Course                course;
    private Date                  creationTime;
    private Date                  modificationTime;
    private User                  creator;
    private Collection<Criterion> criteria;
    private TString               description;
    private int                   difficulty;
    private boolean               hidden;
    private long                  id;
    private Collection<Input>     inputs;
    private int                   maxSteps;
    private String                modelSolution;
    private String                postCode;
    private String                preCode;
    private TString               title;
    private Type                  type;

    //////////////////
    // CONSTRUCTORS //
    //////////////////
    
    /**
     * Constructs a task with all fields unset. The only default that is set by this constructor is the
     * creation time which is set to the current time. TString and collection fields are also initialized to be empty.
     */
    public Task() {
        this.creationTime = new Date();
        this.modificationTime = new Date();
        this.maxSteps = DEFAULT_MAX_STEPS;
        this.title = new TString();
        this.type = Type.PROGRAMMING;
    }
    
    /**
     * Constructs a task with all mandatory fields set. The values for the relevant fields are given as parameters
     * to the constructor. TString and collection fields are also initialized to be empty.
     * 
     * @param category The category this task belongs to.
     * @param creator The user who created this task.
     * @param course The course this task belongs to.
     */
    public Task(Category category, User creator, Course course) {
        this();
        this.category = category;
        this.creator  = creator;
        this.course   = course;
    }
    
    ///////////////
    // ACCESSORS //
    ///////////////
    
    @Bidirectional
    @MapKey(name = "id")
    @Cascade({CascadeType.ALL})
    @OneToMany(mappedBy = "task")
    public Collection<Answer> getAnswers() {
        if (this.answers == null) {
            this.answers = new HashSet<Answer>();
        }
        return this.answers;
    }
    
    public void setAnswers(Collection<Answer> answers) {
        this.answers = answers;
    }
    
    /**
     * Returns the category this task belongs to.
     * 
     * @return The category this task belongs to.
     */
    @Bidirectional
    @ManyToOne(optional=true)
    @JoinColumn(name = "categoryid", nullable=true)
    public Category getCategory() {
        return this.category;
    }
    
    /**
     * Sets the category this task belongs to.
     * 
     * @param category The category this task belongs to.
     */
    public void setCategory(Category category) {
        this.category = category;
    }
    
    /**
     * Returns the course this task belongs to.
     * 
     * @return The course this task belongs to.
     */
    @Bidirectional
    @ManyToOne
    @NotNull
    @JoinColumn(name = "courseid")
    public Course getCourse() {
        return this.course;
    }
    
    /**
     * Sets the course this task belongs to.
     * 
     * @param course The course this task belongs to.
     */
    public void setCourse(Course course) {
        if (course == null) {
            throw new NullPointerException("Argument 'course' must not be null!");
        }
        this.course = course;
    }
    
    
    /**
     * Returns the time of creation for this task.
     * 
     * @return The time of creation for this task.
     */
    @NotNull
    public Date getCreationTime() {
        return this.creationTime;
    }

    /**
     * Sets the time this task was created. This date must not be null.
     * 
     * @param creationTime The time this task was created.
     */
    public void setCreationTime(Date creationTime) {
        if (creationTime == null) {
            throw new NullPointerException("Argument 'creationTime' must not be null!");
        }
        this.creationTime = creationTime;
    }
    
    @NotNull
    public Date getModificationTime() {
        return modificationTime;
    }
    
    public void setModificationTime(Date modificationTime) {
        if (modificationTime == null) {
            throw new NullPointerException("Argument 'modificationTime' must not be null!");
        }
        this.modificationTime = modificationTime;
    }
    
    /**
     * <p>Returns the user who created this task.</p>
     * 
     * <p>This may return null if the user account has been deleted.</p>
     * 
     * @return The user who created this task.
     */
    @ManyToOne
    @JoinColumn(name = "creatorId", nullable = true)
    public User getCreator() {
        return this.creator;
    }
    
    /**
     * Sets the user who created this task.
     * 
     * @param creator The user who created this task.
     */
    public void setCreator(User creator) {
        validateUserArgument(creator, "creator", USER_CONSTRAINT_NOT_STUDENT + USER_CONSTRAINT_NOT_INSTRUCTOR);
        this.creator = creator;
    }
    
    /**
     * @return
     */
    @Bidirectional
    @Cascade({CascadeType.ALL})
    @MapKey(name = "id")
    @OneToMany(mappedBy = "task")
    public Collection<Criterion> getCriteria() {
        if (this.criteria == null) {
            this.criteria = new HashSet<Criterion>();
        }
        return this.criteria;
    }
    
    /**
     * @param criteria
     */
    public void setCriteria(Collection<Criterion> criteria) {
        this.criteria = criteria;
    }
    
    /**
     * Returns the description of this task.
     * 
     * @return The description of this task.
     */
    @Cascade( { CascadeType.ALL } )
    @JoinColumn(name = "descriptionid")
    @NotNull
    @OneToOne
    public TString getDescription() {
        return this.description;
    }
    
    /**
     * Returns the localized description of this task.
     * 
     * @param locale The locale for which to return the localized description.
     * @return The localized description of this task or {@code null} if none was found.
     */
    public String getDescription(Locale locale) {
        if (this.description != null) {
            return this.description.get(locale);
        }
        return null;
    }
    
    /**
     * Sets the description of this task.
     * 
     * @param description The description of this task.
     */
    public void setDescription(TString description) {
        this.description = description;
    }
    
    /**
     * Sets the localized description of this task.
     * 
     * @param locale The locale for which to set the description.
     * @param description The localized description of this task.
     */
    public void setDescription(Locale locale, String description) {
        validateStringArgument(description, "description", STRING_CONSTRAINT_NOT_WHITESPACE_ONLY);
        if (this.description == null) {
            this.description = new TString();
        }
        /* Check if the description is too long */
        if ((description != null) && (description.length() > Task.MAX_DESCRIPTION_LENGTH)) {
            throw new IllegalArgumentException("Argument 'description' is too long! The maximum length is: " + Task.MAX_DESCRIPTION_LENGTH + ".");
        }
        this.description.set(locale, description);
    }
    
    /**
     * Returns the difficulty of this task.
     * 
     * @return The difficulty of this task.
     */
    public int getDifficulty() {
        return this.difficulty;
    }
    
    /**
     * Sets the difficulty of this task.
     * 
     * @param difficulty The difficulty of this task.
     */
    public void setDifficulty(int difficulty) {
        if ((difficulty < Task.DIFFICULTY_MIN) || (difficulty > Task.DIFFICULTY_MAX)) {
            throw new IllegalArgumentException("Argument 'difficulty' must be between " + Task.DIFFICULTY_MIN + " and " + Task.DIFFICULTY_MAX + "!");
        }
        this.difficulty = difficulty;
    }
    
    /**
     * Returns the unique id of this task.
     * 
     * @return The unique id of this task.
     */
    @Id
    @GeneratedValue
    @Override
    public long getId() {
        return this.id;
    }
    
    /**
     * Sets the unique id of this task. This method should never be called directly, assigning an
     * id to an input is taken care of by the persistence framework.
     * 
     * @param id The unique id of this task.
     */
    @Override
    public void setId(long id) {
        this.id = id;
    }
    
    @Bidirectional
    @Cascade({CascadeType.ALL})
    @MapKey(name = "id")
    @OneToMany(mappedBy = "task")
    public Collection<Input> getInputs() {
        if (this.inputs == null) {
            this.inputs = new HashSet<Input>();
        }
        return this.inputs;
    }
    
    public void setInputs(Collection<Input> inputs) {
        this.inputs = inputs;
    }
    
    /**
     * Returns true if this task is hidden. "Hidden" means that this task is only visible to editors 
     * and administrators and not yet published to the students and instructors of a course.
     * 
     * @return {@code true} if this task is hidden.
     */
    public boolean getHidden() {
        return this.hidden;
    }
    
    /**
     * Sets the "hidden" status of a task. 
     * 
     * @param hidden Whether or not this task is hidden. 
     */
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
    
    @org.hibernate.annotations.Type(type = "text")
    public String getModelSolution() {
        return modelSolution;
    }
    
    public void setModelSolution(String modelSolution) {
        this.modelSolution = modelSolution;
    }
    
    public int getMaxSteps() {
        return this.maxSteps;
    }
    
    public void setMaxSteps(int maxSteps) {
        //TODO Error checking
        this.maxSteps = maxSteps;
    }
    
    /**
     * Returns the postcode that is used in this task. Postcode is a piece of code that is appended to 
     * the students solution to a task.
     * 
     * @return The postcode to be used in this task.
     */
    @org.hibernate.annotations.Type(type = "text")
    public String getPostCode() {
        return this.postCode;
    }
    
    /**
     * Sets the postcode to be used in this task.
     * 
     * @param postCode The postcode to be used in this task.
     */
    public void setPostCode(String postCode) {
        if (postCode != null) {
            if (postCode.trim().length() == 0) {
                throw new IllegalArgumentException("Argument 'postCode' must not consist of whitespace only!");
            }
        }
        this.postCode = postCode;
    }
    
    /**
     * Returns the precode to be used in this task. Precode is a piece of code that prepends the students
     * solution to a task.
     * 
     * @return The precode for this task.
     */
    @org.hibernate.annotations.Type(type = "text")
    public String getPreCode() {
        return this.preCode;
    }
    
    /**
     * Sets the precode to be used in this task.
     * 
     * @param preCode The precode to be used in this task.
     */
    public void setPreCode(String preCode) {
        if (preCode != null) {
            if (preCode.trim().length() == 0) {
                throw new IllegalArgumentException("Argument 'preCode' must not consist of whitespace only!");
            }
        }
        this.preCode = preCode;
    }
    
    /**
     * Returns the title of this task.
     * 
     * @return The title of this task.
     */
    @Cascade( { CascadeType.ALL } )
    @JoinColumn(name = "titleid")
    @NotNull
    @OneToOne
    public TString getTitle() {
        return this.title;
    }
    
    /**
     * Returns the localized title of this task.
     * 
     * @param locale The locale for which to return the title.
     * @return The localized title of this task.
     */
    public String getTitle(Locale locale) {
        if (this.title != null) {
            return this.title.get(locale);
        }
        return null;
    }
    
    /**
     * Sets the title of this task.
     * 
     * @param title The title of this task.
     */
    public void setTitle(TString title) {
        this.title = title;
    }
    
    /**
     * Returns the localized title of the task.
     * 
     * @param locale The locale for which to get the title.
     * @param title The localized title of the task.
     */
    public void setTitle(Locale locale, String title) {
        validateStringArgument(title, "title", STRING_CONSTRAINT_NOT_WHITESPACE_ONLY);
        if (title != null) {
            title = title.trim();
        }
        if (this.title == null) {
            this.title = new TString();
        }
        this.title.set(locale, title);
    }
    
    /**
     * Returns the type of this task.
     * 
     * @return The type of this task.
     */
    @NotNull
    public Type getType() {
        return this.type;
    }
    
    /**
     * Sets the type of this task.
     * 
     * @param type The type of this task;
     */
    public void setType(Type type) {
        if (type == null) {
            throw new NullPointerException("Argument 'type' must not be null!");
        }
        this.type = type;
    }

    //////////////////////
    // INSTANCE METHODS //
    //////////////////////
    
    /**
     * Copies this task.
     * 
     * @return
     */
    public Task deepCopy() {
        return deepCopy(null, null, null, false);
    }
    
    /**
     * Copies this task.
     * 
     * @param targetCourse
     * @param targetCategory
     * @return
     */
    public Task deepCopy(Course targetCourse, Category targetCategory) {
        return deepCopy(targetCourse, targetCategory, null, false);
    }
    
    /**
     * Copies this task. The deep-copy semantics for an answer are the following:<br /><br />
     * <ul>
     *   <li>If {@code targetCourse==null}, the task will be copied within the same course.</li>
     *   <li>If {@code targetCourse==null} and {@code targetCategory==null}, the task will be copied within the same course and within the same category</li>
     *   <li>If {@code targetCourse!=null} and {@code targetCategory==null}, the category of this task is duplicated in the targetCourse automatically</li>
     *   <li>If {@code copyEditor!=null} the copy editor is set as the creator of the copied task. If {@code copyEditor==null}, the original creator is preserved</li>
     *   <li>{@code title} and {@code description} are deep-copied.</li>
     * </ul>
     * 
     * @param targetCourse
     * @param targetCategory
     * @param copyEditor
     * @param updateAssociations
     * @return
     */
    public Task deepCopy(Course targetCourse, Category targetCategory, User copyEditor, boolean updateAssociations) {
        validateUserArgument(copyEditor, "copyEditor", USER_CONSTRAINT_NOT_STUDENT + USER_CONSTRAINT_NOT_INSTRUCTOR);
        
        Task copy = new Task();

        /* This map is used to assign the copies of the criteria to the correct inputs */
        Map<Input, Input> inputMap = new HashMap<Input, Input>();
        
        
        /* Determine the correct target categories to use. */
        if ((targetCourse == null) || (targetCourse.equals(this.course))) {
            targetCourse = this.course;
            if (targetCategory == null) {
                targetCategory = this.category;
            }
        } else {
            if (targetCategory == null) {
                /* Duplicate the category of this task in the target course */
                if (this.category != null) {
                    targetCategory = this.category.deepCopy(targetCourse, true);
                }
            }
        }
        /* Check for mismatch in Course & Category */
        if (targetCategory != null && !targetCourse.equals(targetCategory.getCourse())) {
            throw new IllegalArgumentException("The argument 'targetCategory' has to be one of the categories in 'targetCourse'!");
        }
        /* Set Course & Category */
        copy.setCourse(targetCourse);
        copy.setCategory(targetCategory);

        /* Copy all dependent inputs */
        for (Input sourceInput : this.getInputs()) {
            Input copyInput = sourceInput.deepCopy(copy, true);
            inputMap.put(sourceInput, copyInput);
        }
        
        /* Copy all dependent criteria */
        for (Criterion sourceCriterion : this.getCriteria()) {
            sourceCriterion.deepCopy(copy, inputMap.get(sourceCriterion.getInput()), true);
        }
        
        /* Deep-Copy dependent TStrings */
        copy.setTitle(copyOrNull(this.title));
        copy.setDescription(copyOrNull(this.description));
        
        /* Copy creation time */
        copy.setCreationTime(new Date(this.creationTime.getTime()));
        /* Set creator of copy */
        if (copyEditor == null) {
            /* Use the given creator */
            copy.setCreator(this.creator);
        } else {
            copy.setCreator(copyEditor);
        }
        /* Copy difficulty */
        copy.setDifficulty(this.difficulty);
        /* Copy hidden status */
        copy.setHidden(this.hidden);
        /* Copy Pre/Post-Code */
        if (this.postCode != null) {
            copy.setPostCode(this.postCode);            
        }
        if (this.preCode != null) {
            copy.setPreCode(this.preCode);            
        }
        /* Copy model solution */
        if (this.modelSolution != null) {
            copy.setModelSolution(this.modelSolution);
        }
        /* Copy Type */
        copy.setType(this.type);
        return copy;
    }

    /**
     * Determines if this task is completely localized for a given locale. A task is only considered 'completely'
     * localized if all dependent entities, like criteria are also localized for the given locale.
     * 
     * @param locale The locale for which to determine if the localization of this task is complete. Must not be {@code null}.
     * @return {@code true} - If this task is completely localized for the given locale<br />
     *         {@code false} - Otherwise
     * @throws NullPointerException - If the argument {@code locale} is {@code null}
     */
    public boolean hasCompleteTranslation(Locale locale) {
        validateLocaleArgument(locale, "locale", LOCALE_CONSTRAINT_NOT_NULL);
        /* Check dependent TString's */
        if ((this.title == null) || (this.description == null)) {
            return false;
        }
        /* Check the title of the task */
        if (this.title.get(locale) == null) {
            return false;
        }
        /* Check the description of the task */
        if (this.description.get(locale) == null) {
            return false;
        }
        return true;
    }
    
    /**
     * Determines if this task is a fill-in task.
     * 
     * @return {@code true} - If this task is a fill-in task<br />
     *         {@code false} - Otherwise
     */
    @Transient
    public boolean isFillIn() {
        if (this.type == Type.FILL_IN) {
            return true;
        }
        return false;
    }
    
    /**
     * Determines if this task is a programming task.
     * 
     * @return {@code true} - If this task is a programming task<br />
     *         {@code false} - Otherwise
     */
    @Transient
    public boolean isProgramming() {
        if (this.type == Type.PROGRAMMING) {
            return true;
        }
        return false;        
    }
    
    /**
     * <p>An internal validation method.</p>
     * 
     * <p>Checks that the category is in the correct course.</p>
     */
    @AssertTrue
    @Transient
    public boolean isCategoryInCorrectCourse() {
        if (this.category == null || this.course == null) {
            return true; // Nothing to check
        }
        return this.category.getCourse().equals(this.course);
    }
    
}
