package fi.helsinki.cs.titotrainer.app.model;

import static org.junit.Assert.*;

import java.util.Locale;

import org.hibernate.PropertyValueException;
import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;

import fi.helsinki.cs.titotrainer.testsupport.TitoTestCase;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleCategories;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleCourses;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleTasks;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleUsers;

public class CategoryTest extends TitoTestCase {

    private Session session;
    
    private SampleCourses courses;
    private SampleUsers   users;
    
    @Before
    public void setUp() {
        this.session = this.openAutoclosedSession();
        this.courses = new SampleCourses(this.session);
        this.users = new SampleUsers(this.session);
    }
    
    /* General Tests */
            
    @Test
    public void shouldAcceptNormalCategoryName() {
        Category category = new Category();
        category.setName(Locale.ENGLISH, "A Category Name");
        category.setCourse(this.courses.autumnCourse);
        category.getCourse().getCategories().add(category);
        this.session.save(category);
        this.session.flush();
    }
    
    @Test(expected = PropertyValueException.class)
    public void shouldNotAcceptCategoryWithoutName() {
        Category category = new Category();
        category.setCourse(this.courses.selfStudyCourse);
        this.courses.selfStudyCourse.getCategories().add(category);
        this.session.save(category);
        this.session.flush();
    }
    
    /* Test Category(TString, Course) */
    
    @Test
    public void shouldSaveValidCategoryFromComplexConstructor() {
        Category category;
        TString name = new TString(Locale.ENGLISH, "A Normal Category Name!");
        category = new Category(name, this.courses.selfStudyCourse);
        this.session.save(category);
        this.session.flush();
    }
    
    /* Test setCourse(Course) */
    
    @Test
    public void shouldAcceptValidCourse() {
        Category category = new Category();
        category.setCourse(this.courses.autumnCourse);
        assertEquals(this.courses.autumnCourse, category.getCourse());
    }
    
    @Test(expected = NullPointerException.class)
    public void shouldNotAcceptNullCourse() {
        Category category = new Category();
        category.setCourse(null);
    }
    
    /* Test getName(Locale) */
    
    @Test
    public void shouldReturnNullForNullName() {
        Category category = new Category();
        String name = category.getName(Locale.ENGLISH);
        assertNull(name);
    }

    /* Test setName(TString) */
    
    @Test
    public void shouldAcceptValidTStringAsName() {
        Category category = new Category();
        TString name = new TString(Locale.ENGLISH, "A Proper Name");
        category.setName(name);
        this.session.save(name);
        this.session.flush();
        assertEquals(name, category.getName());
    }
    
    @Test
    public void setNameShouldAcceptNullNameTString() {
        Category category = new Category();
        category.setName(null);
    }
    
    /* Test setName(Locale, String) */
    
    @Test
    public void shouldAcceptValidName() {
        Category category = new Category();
        category.setName(Locale.ENGLISH, "Difficult Tasks");
        assertEquals("Difficult Tasks", category.getName(Locale.ENGLISH));
    }
    
    @Test(expected = NullPointerException.class)
    public void shouldNotAcceptNullLocale() {
        Category category = new Category();
        category.setName(null, "A Normal Course Name");
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAcceptNullName() {
        Category category = new Category();
        category.setName(Locale.ENGLISH, null);
    }
    
    @Test
    public void shouldStripWhitespacesFromName() {
        Category category = new Category();
        category.setName(Locale.ENGLISH, "    A Category       ");
        assertEquals("A Category", category.getName(Locale.ENGLISH));
    }
        
    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptEmptyName() {
        Category category = new Category();
        category.setName(Locale.ENGLISH, "\n    \t         \n     ");
    }
    
    /* Test deepCopy() */
    
    @Test(expected = NullPointerException.class)
    public void deepCopyShouldThrowNullPointerExceptionIfCourseIsNotSet() {
        Category source = new Category();
        source.deepCopy();
    }
    
    @Test
    public void deepCopyShouldReturnTrueCopy() {
        Category source = SampleCategories.createIntermediateTasks(this.courses.autumnCourse);
        this.session.save(source);
        Category copy = source.deepCopy();
        assertSame(source.getCourse(), copy.getCourse());
        assertNotSame(source, copy);
        assertFalse(source.equals(copy));
        assertFalse(copy.equals(source));
        assertNotSame(source.getName(), copy.getName());
        assertEquals(source.getName().getTranslations(), copy.getName().getTranslations());
    }
    
    @Test
    public void deepCopyShouldNotCopyCourse() {
        Category source = new Category();
        source.setName(ENGLISH, "Category A");
        source.setCourse(this.courses.autumnCourse);
        Category copy = source.deepCopy();
        assertSame(source.getCourse(), copy.getCourse());
    }
    
    @Test
    public void deepCopyShouldNotUpdateCollectionInCourse() {
        Category source = new Category();
        source.setName(ENGLISH, "Category A");
        source.setCourse(this.courses.autumnCourse);
        Category copy = source.deepCopy();
        assertFalse(source.getCourse().getCategories().contains(copy));
    }
    
    @Test
    public void deepCopyShouldNotCopyOrDuplicateTasks() {
        Category source = SampleCategories.createBeginnersTasks(this.courses.autumnCourse);
        Task task = SampleTasks.createMinimalTask(source, this.users.editor, this.courses.autumnCourse, Task.Type.PROGRAMMING);
        this.session.save(source);
        Category copy = source.deepCopy();
        assertEquals(1, source.getTasks().size());
        assertSame(task, source.getTasks().iterator().next());
        assertEquals(0, copy.getTasks().size());
    }
    
    /* Test deepCopy(Course, boolean) */
    
    @Test
    public void deepCopyShouldAttachCopyToTargetCourse() {
        Category source = SampleCategories.createBeginnersTasks(this.courses.autumnCourse);
        Category copy = source.deepCopy(this.courses.selfStudyCourse, true);
        assertSame(this.courses.selfStudyCourse, copy.getCourse());
    }
    
    @Test
    public void deepCopyShouldUpdateCollectionInCourse() {
        Category source = SampleCategories.createBeginnersTasks(this.courses.autumnCourse);
        Category copy = source.deepCopy(this.courses.selfStudyCourse, true);
        assertTrue(this.courses.autumnCourse.getCategories().contains(source));
        assertTrue(this.courses.selfStudyCourse.getCategories().contains(copy));
        this.session.save(source);
        this.session.save(copy);
    }

    /* Test hasCompleteTranslation(Locale) */

    @Test(expected = NullPointerException.class)
    public void hasCompleteTranslationShouldNotAcceptNullLocale() {
        Category category = new Category();
        category.hasCompleteTranslation(null);
    }

    @Test
    public void hasCompleteTranslationShouldReturnFalseOnNullName() {
        Category category = new Category();
        assertFalse(category.hasCompleteTranslation(Locale.ENGLISH));
    }

    @Test
    public void hasCompleteTranslationShouldReturnTrueOnlyForTheTranslatedLocale() {
        Category category = new Category();
        category.setName(Locale.ENGLISH, "A Category!");
        assertTrue(category.hasCompleteTranslation(Locale.ENGLISH));
        assertFalse(category.hasCompleteTranslation(Locale.GERMAN));
        assertFalse(category.hasCompleteTranslation(Locale.FRENCH));
    }

    @Test
    public void hasCompleteTranslationShouldReturnFalseAfterTranslationHasBeenRemoved() {
        Category category = new Category();
        category.setName(Locale.ENGLISH, "Simple Tasks");
        assertTrue(category.hasCompleteTranslation(Locale.ENGLISH));
        category.getName().unset(Locale.ENGLISH);
        assertFalse(category.hasCompleteTranslation(Locale.ENGLISH));
    }

}