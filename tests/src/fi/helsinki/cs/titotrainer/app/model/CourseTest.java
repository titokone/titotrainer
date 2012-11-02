package fi.helsinki.cs.titotrainer.app.model;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Locale;

import org.hibernate.PropertyAccessException;
import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;

import fi.helsinki.cs.titotrainer.testsupport.TitoTestCase;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleCategories;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleTasks;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleUsers;


public class CourseTest extends TitoTestCase {
    
    private Session session;
    
    private SampleUsers users;
    
    @Before
    public void setUp() {
        this.session = this.openAutoclosedSession();
        this.users = new SampleUsers(this.session);
    }    
    
    /* General Tests */
    
    @Test
    public void shouldSaveValidCourseFromComplexConstructor() {
        TString name = new TString(Locale.ENGLISH, "Some Course");
        Course course = new Course(name);
        this.session.save(course);
        this.session.flush();
    }
    
    @Test
    public void shouldSaveValidCourseFromSimpleConstructor() {
        Course course = new Course();
        course.setName(Locale.ENGLISH, "Some Course");
        this.session.save(course);
        this.session.flush();
    }
    
    @Test(expected = PropertyAccessException.class)
    public void shouldNotSaveCourseWithoutName() {
        Course course = new Course();
        this.session.save(course);
        this.session.flush();
    }
    
    /* Test getCategories */
    
    @Test
    public void getCategoriesShouldReturnEmptyCollectionBeforePersistingTheCourse() {
        Course course = new Course();
        course.setName(Locale.ENGLISH, "Test Course");
        assertEquals(0, course.getCategories().size());
    }
    
    @Test
    public void getCategoriesShouldReturnEmptyCollectionAfterPersistingTheCourse() {
        Course courseIn = new Course();
        courseIn.setName(Locale.ENGLISH, "Test Course");
        this.session.save(courseIn);
        this.session.flush();
        Course courseOut = (Course)this.session.get(Course.class, courseIn.getId());
        Collection<Category> categories = courseOut.getCategories();
        assertEquals(0, categories.size());
    }
    
    @Test
    public void getCategoriesShouldReturnTheCorrectCollection() {
        Course courseA = new Course();
        courseA.setName(Locale.ENGLISH, "Test Course");
        this.session.save(courseA);
        Course courseB = new Course();
        courseB.setName(Locale.ENGLISH, "Wrong Course");
        this.session.save(courseB);
        Category categoryA = SampleCategories.createBeginnersTasks(courseA);
        courseA.getCategories().add(categoryA);
        Category categoryB = SampleCategories.createIntermediateTasks(courseB);
        courseB.getCategories().add(categoryB);
        Category categoryC = SampleCategories.createTrickyTasks(courseA);
        courseA.getCategories().add(categoryC);
        this.session.save(courseA);
        this.session.save(courseB);
        this.session.save(categoryA);
        this.session.save(categoryB);
        this.session.save(categoryC);
        this.session.flush();
        Category categoryAOut = (Category)this.session.get(Category.class, categoryA.getId());
        Category categoryBOut = (Category)this.session.get(Category.class, categoryB.getId());
        Category categoryCOut = (Category)this.session.get(Category.class, categoryC.getId());
        Course courseOut = (Course)this.session.get(Course.class, courseA.getId());
        assertEquals("Test Course", courseOut.getName(Locale.ENGLISH));
        assertNotNull(courseOut);
        Collection<Category> categories = courseOut.getCategories();
        assertNotNull(categories);
        assertTrue(categories.contains(categoryAOut));
        assertFalse(categories.contains(categoryBOut));
        assertTrue(categories.contains(categoryCOut));
    }

    /* Test setName(...) */
    
    @Test
    public void shouldAcceptValidName() {
        Course course = new Course();
        course.setName(Locale.ENGLISH, "Computer Systems Organization - Spring 2009");
        assertEquals("Computer Systems Organization - Spring 2009", course.getName(Locale.ENGLISH));
    }
    
    @Test
    public void shouldStripWhitespacesFromName() {
        Course course = new Course();
        course.setName(Locale.ENGLISH, "     A Course     \n  ");
        assertEquals("A Course", course.getName(Locale.ENGLISH));
    }
    
    @Test(expected = NullPointerException.class)
    public void shouldNotAcceptNullName() {
        Course course = new Course();
        course.setName(null);
    }

    @Test(expected = IllegalArgumentException.class)    
    public void shouldNotAcceptEmptyName() {
        Course course = new Course();
        course.setName(Locale.ENGLISH, "  \t   \n       \n    \n  ");
    }
    
    /* Test hasCompleteTranslation(Locale) */
    
    @Test(expected = NullPointerException.class)
    public void hasCompleteTranslationShouldNotAcceptNullLocale() {
        Course course = new Course();
        course.hasCompleteTranslation(null);
    }
    
    @Test
    public void hasCompleteTranslationShouldOnlyReturnTrueIfTheNameIsTranslated() {
        Course course = new Course();
        assertFalse(course.hasCompleteTranslation(ENGLISH));
        course.setName(FINNISH, "Kurssi");
        assertFalse(course.hasCompleteTranslation(ENGLISH));
        course.setName(ENGLISH, "Course");
        assertTrue(course.hasCompleteTranslation(ENGLISH));
    }
    
    @Test
    public void hasCompleteTranslationShouldOnlyReturnTrueIfAllDependentCategoriesAreLocalized() {
        Course course = new Course(ENGLISH, "Some Course");
        this.session.save(course);
        assertTrue(course.hasCompleteTranslation(ENGLISH));
        Category categoryA = new Category();
        categoryA.setName(new TString());
        categoryA.setCourse(course);
        course.getCategories().add(categoryA);
        this.session.save(categoryA);
        Category categoryB = new Category();
        categoryB.setName(new TString());
        categoryB.setCourse(course);
        course.getCategories().add(categoryB);
        this.session.save(categoryB);
        assertFalse(course.hasCompleteTranslation(ENGLISH));
        categoryA.setName(ENGLISH, "Category A");
        assertFalse(course.hasCompleteTranslation(ENGLISH));
        categoryB.setName(GERMAN, "Kategorie B");
        assertFalse(course.hasCompleteTranslation(ENGLISH));
        categoryB.setName(ENGLISH, "Category B");
        assertTrue(course.hasCompleteTranslation(ENGLISH));
    }
    
    @Test
    public void hasCompleteTranslationShouldOnlyReturnTrueIfAllDependentTasksAreLocalized() {
        Course course = new Course(ENGLISH, "Some Course");
        this.session.save(course);
        assertTrue(course.hasCompleteTranslation(ENGLISH));
        Category category = SampleCategories.createBeginnersTasks(course);
        this.session.save(category);
        Task taskA = SampleTasks.createMinimalTask(category, this.users.editor, course, Task.Type.PROGRAMMING);
        this.session.save(taskA);
        taskA.setTitle(ENGLISH, null);
        Task taskB = SampleTasks.createStandardTask(category, this.users.admin, course, Task.Type.FILL_IN);
        taskB.setDescription(ENGLISH, null);
        this.session.save(taskB);
        assertFalse(course.hasCompleteTranslation(ENGLISH));
        taskA.setTitle(ENGLISH, "Some Title");
        assertFalse(course.hasCompleteTranslation(ENGLISH));
        taskB.setDescription(ENGLISH, "Some Description");
        assertTrue(course.hasCompleteTranslation(ENGLISH));
    }

}