package fi.helsinki.cs.titotrainer.app.model;

import static fi.helsinki.cs.titotrainer.app.model.misc.ArgumentUtils.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.validator.NotNull;

import fi.helsinki.cs.titotrainer.framework.model.Bidirectional;

/**
 * Represents one row in the table "category". A category simply has a name and an (automatically generated) id.
 * Furthermore, every category can only belong to one course. This is necessary to prevent problems when a new
 * course is created.
 */
@Entity
@Table(name = "category")
public class Category extends AbstractTitoEntity {

    ////////////////////
    // PRIVATE FIELDS //
    ////////////////////

    private long             id;
    private Course           course;
    private TString          name;
    private Collection<Task> tasks;

    //////////////////
    // CONSTRUCTORS //
    //////////////////
    
    /**
     * Constructs a new category with all fields unset.
     */
    public Category() {
    }
    
    /**
     * Constructs a new category for the given course with the given name.
     * 
     * @param name The name of the new category
     */
    public Category(TString name, Course course) {
        this.course = course;
        this.name = name;
    }

    ///////////////
    // ACCESSORS //
    ///////////////
    
    /**
     * Returns the unique id of this category.
     * 
     * @return The unique id of this category.
     */
    @Id
    @GeneratedValue
    public long getId() {
        return this.id;
    }
    
    /**
     * Sets a new id for this category. This method should never be called directly as assignment of an id is
     * taken care of by the persistence framework.
     * 
     * @param id The new id of this category.
     */
    public void setId(long id) {
        this.id = id;
    }
    
    /**
     * Returns the course this category belongs to.
     * 
     * @return The course this category belongs to.
     */
    @Bidirectional
    @ManyToOne
    @NotNull
    @JoinColumn(name = "courseId")
    public Course getCourse() {
        return this.course;
    }
    
    /**
     * Sets the course for this category.
     * 
     * @param course The course this category belongs to. Must not be null.
     */
    public void setCourse(Course course) {
        validateCourseArgument(course, "course", COURSE_CONSTRAINT_NOT_NULL);
        this.course = course;
    }
    
    /**
     * Returns the name of this category.
     * 
     * @return The name of this category.
     */
    @Cascade( { CascadeType.ALL } )
    @JoinColumn(name = "nameid")
    @NotNull
    @OneToOne
    public TString getName() {
        return this.name;
    }
    
    /**
     * Returns the localized name of this category.
     * 
     * @param locale The locale for which to get the name.
     * @return The localized name of this category.
     */
    public String getName(Locale locale) {
        if (this.name != null) {
            return this.name.get(locale);
        }
        return null;
    }
    
    /**
     * Sets a new name for this category. This name must not be null or consist only of whitespaces.
     * 
     * @param name The new name for this category.
     */
    public void setName(TString name) {
        this.name = name;
    }
    
    /**
     * Sets the localized name of this category.
     * 
     * @param locale The locale of this name.
     * @param name   The localized name of this category.
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
    
    @Bidirectional
    @MapKey(name = "id")
    @OneToMany(mappedBy = "category")
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
     * Copies this category within the same course. Bidirectional associations are not updated.
     * 
     * <h4 class = "implementation">Implementation Note</h4>
     * Internally, this method redirects to {@code deepCopy(null, false)}.
     * 
     * @return A true copy of this category
     * @see #deepCopy(Course, boolean)
     */
    public Category deepCopy() {
        return deepCopy(null, false);
    }

    /**
     * Copies this category and attaches it to another course. The deep-copy semantics for a category are the following:<br /><br />
     * 
     * <ul>
     *   <li>The id is not copied at all (it's later assigned by the persistence framework)</li>
     *   <li>If {@code targetCourse==null}, the old reference to course is copied, otherwise the copy of this category refers to {@code targetCourse}</li>
     *   <li>{@code name} is deep-copied</li>
     *   <li>Dependent tasks are not copied at all</li>
     *   <li>If {@code updateAssociations==true}, the copy is added to the collection of categories in the "owning" course</li>
     * </ul>
     * 
     * @param targetCourse The course the copy of this category should be attached to. If {@code null}, the course reference of the source is used
     * @param updateAssociations If {@code true}, bidirectional associations are automatically updated
     * @return A true copy of this category
     */
    public Category deepCopy(Course targetCourse, boolean updateAssociations) {
        Category copy = new Category();
        
        /* Deep-copy name */
        copy.setName(copyOrNull(this.name));
        /* Use target-course if provided, otherwise copy the old reference */
        if (targetCourse != null) {
            copy.setCourse(targetCourse);
        } else {
            copy.setCourse(this.course);
        }
        /* Update bidirectional associations if requested */
        if (updateAssociations) {
            copy.getCourse().getCategories().add(copy);
        }
        
        return copy;
    }
    
    /**
     * Determines if this category is completely localized for a given locale.
     * 
     * @param locale The locale for which to determine if the localization of this category is complete. Must not be {@code null}.
     * @return {@code true} - If this category is completely localized for the given locale<br />
     *         {@code false} - Otherwise
     * @throws NullPointerException - If the argument {@code locale} is {@code null}
     */
    @Override
    public boolean hasCompleteTranslation(Locale locale) {
        validateLocaleArgument(locale, "locale", LOCALE_CONSTRAINT_NOT_NULL);
        if (this.name == null) {
            return false;
        }
        return (this.name.get(locale) != null);
    }
    
}