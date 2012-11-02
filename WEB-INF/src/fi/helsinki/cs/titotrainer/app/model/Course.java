package fi.helsinki.cs.titotrainer.app.model;

import static fi.helsinki.cs.titotrainer.app.model.misc.ArgumentUtils.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.validator.NotNull;

import fi.helsinki.cs.titotrainer.framework.model.Bidirectional;

/**
 * Represents one row in the table "course". A course simple has an id and a name. 
 */
@Entity
@Table(name = "course")
public class Course extends AbstractTitoEntity {

    ////////////////////
    // PRIVATE FIELDS //
    ////////////////////

    private Collection<Category> categories;
    private long                 id;
    private TString              name;
    private boolean              hidden;
    private Collection<Task>     tasks;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    /**
     * Constructs a new course with all fields unset.
     */
    public Course() {
        this.hidden = false;
    }
    
    /**
     * Constructs a new course with the name given.
     * 
     * @param name The name of the course.
     */
    public Course(TString name) {
        this();
        this.name = name;
    }
    
    /**
     * Constructs a new course with a localized name.
     * 
     * @param locale The locale for which to set the course's name.
     * @param name The localized name of the course;
     */
    public Course(Locale locale, String name) {
        this();
        this.name = new TString();
        this.name.set(locale, name);
    }

    ///////////////
    // ACCESSORS //
    ///////////////
    
    /**
     * @return
     */
    @Bidirectional
    @MapKey(name = "id")
    @OneToMany(mappedBy = "course")
    public Collection<Category> getCategories() {
        if (this.categories == null) {
            this.categories = new HashSet<Category>();
        }
        return this.categories;
    }
    
    /**
     * @param categories
     */
    public void setCategories(Collection<Category> categories) {
        this.categories = categories;
    }

    /**
     * Returns the unique id of this course.
     * 
     * @return The unique id of this course.
     */
    @Id
    @GeneratedValue
    @Override
    public long getId() {
        return this.id;
    }

    /**
     * Sets the id of this course. This function should not be called directly as the assignment of an id
     * is the responsibility of the persistence framework.
     * 
     * @param id The new id of this course.
     */
    @Override
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Returns the name of this course.
     * 
     * @return The name of this course
     */
    @Cascade( org.hibernate.annotations.CascadeType.ALL )
    @JoinColumn(name = "nameid")
    @NotNull
    @OneToOne
    public TString getName() {
        return this.name;
    }
    
    /**
     * Returns the localized name of this course. The locale to use is given as a parameter.
     * 
     * @param locale The locale in which the name is requested.
     * @return The localized name of this course.
     */
    public String getName(Locale locale) {
        validateLocaleArgument(locale, "locale", LOCALE_CONSTRAINT_NOT_NULL);
        if (this.name != null) {
            return this.name.get(locale);
        }
        return null;
    }
    
    /**
     * Sets the name of this course. The name of a course must not be null or consist only of whitespace.
     * Whitespace at the beginning or the end of the course's name will be removed. 
     * 
     * @param name The new name of this course
     */
    public void setName(TString name) {
        if (name == null) {
            throw new NullPointerException("Argument 'name' must not be null!");
        }
        this.name = name;
    }
    
    /**
     * Sets the localized name of this course.
     * 
     * @param locale The locale of the name
     * @param name The localized name of this course
     */
    public void setName(Locale locale, String name) {
        validateLocaleArgument(locale, "locale", LOCALE_CONSTRAINT_NOT_NULL);
        validateStringArgument(name, "name", STRING_CONSTRAINT_NOT_BLANK);
        
        name = name.trim();
        if (this.name == null) {
            this.name = new TString();
        }
        this.name.set(locale, name);
    }
    
    /**
     * Returns true if this course is hidden. "Hidden" means that
     * students cannot log in to this course and new users
     * may not be registered for the course.
     * 
     * @return {@code true} if this task is hidden.
     */
    public boolean getHidden() {
        return this.hidden;
    }
    
    /**
     * Sets the "hidden" status of a course. 
     * 
     * @param hidden Whether or not this course is hidden. 
     */
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
    
    @Bidirectional
    @MapKey(name = "id")
    @OneToMany(mappedBy = "course")
    public Collection<Task> getTasks() {
        if (this.tasks == null) {
            this.tasks = new HashSet<Task>();
        }
        return this.tasks;
    }
    
    public void setTasks(Collection<Task> tasks) {
        this.tasks = tasks;
    }
    
    //////////////////////
    // INSTANCE METHODS //
    //////////////////////

    /**
     * Determines if this course is completely localized for a given locale. A course is only considered
     * 'completely' localized if all categories and tasks that are part of this course are also completely
     * localized.
     * 
     * @param locale The locale for which to determine if the localization of this course is complete. Must not be {@code null}.
     * @return {@code true} - If this course is completely localized for the given locale<br />
     *         {@code false} - Otherwise
     * @throws NullPointerException - If the argument {@code locale} is {@code null}
     */
    public boolean hasCompleteTranslation(Locale locale) {
        validateLocaleArgument(locale, "locale", LOCALE_CONSTRAINT_NOT_NULL);
        /* Check if the name is localized */
        if ((this.name == null) || (this.name.get(locale) == null)) {
            return false;
        }
        /* Check all categories */
        if (this.categories != null) {
            for (Category category : this.categories) {
                if (!category.hasCompleteTranslation(locale)) {
                    return false;
                }
            }            
        }
        /* Check all tasks */
        if (this.tasks != null) {
            for (Task task : this.tasks) {
                if (!task.hasCompleteTranslation(locale)) {
                    return false;
                }
            }
        }
        return true;
    }

}