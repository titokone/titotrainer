package fi.helsinki.cs.titotrainer.app.model;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.validator.InvalidStateException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import fi.helsinki.cs.titotrainer.testsupport.TitoTestCase;
import fi.helsinki.cs.titotrainer.testsupport.app.model.SimpleCriterion;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleCategories;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleCourses;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleTasks;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleUsers;
import fi.helsinki.cs.titotrainer.testsupport.framework.model.TestDb;

public class TaskTest extends TitoTestCase {
    
    private Session session;
    
    private SampleCategories categories;
    private SampleCourses    courses;
    private SampleTasks      tasks;
    private SampleUsers      users;

    @Override
    protected SessionFactory getNewSessionFactory() {
        AnnotationConfiguration conf = TestDb.createHibernateConfig();
        conf.addAnnotatedClass(SimpleCriterion.class);
        this.sessionFactory = TestDb.createSessionFactory(conf);
        return this.sessionFactory;
    }
    
    @Before
    public void setUp() {
        this.session = this.openAutoclosedSession();
        this.tasks = new SampleTasks(this.session);
        this.categories = this.tasks.categories;
        this.courses = this.tasks.courses;
        this.users = this.tasks.users;
    }

    /* General Tests */

    @Test
    public void shouldSaveValidTask() {
        this.session.save(this.tasks.minimalTask);
        this.session.flush();
    }
    
    @Test
    public void collectionsShouldNotAllowDuplicateMembers() {
        Task task = new Task();
        Criterion criterion = new SimpleCriterion();
        Input input = new Input(task, "1,2,3", false);
        Answer answer = new Answer(this.users.nykanen, task, "; Just a comment!", true);
        assertEquals(0, task.getAnswers().size());
        assertEquals(0, task.getCriteria().size());
        assertEquals(0, task.getInputs().size());
        task.getCriteria().add(criterion);
        task.getAnswers().add(answer);
        task.getInputs().add(input);
        assertEquals(1, task.getAnswers().size());
        assertEquals(1, task.getCriteria().size());
        assertEquals(1, task.getInputs().size());
        task.getCriteria().add(criterion);
        task.getAnswers().add(answer);
        task.getInputs().add(input);
        assertEquals(1, task.getAnswers().size());
        assertEquals(1, task.getCriteria().size());
        assertEquals(1, task.getInputs().size());        
    }
    
    /* Test setCategory(...) */
    
    @Test
    public void shouldAcceptValidCategory() {
        Task task = new Task();
        task.setCategory(this.categories.intermediateTasks);
        assertEquals(this.categories.intermediateTasks, task.getCategory());
    }
    
    public void shouldAcceptNullCategory() {
        Task task = new Task();
        task.setCategory(null);
    }
    
    /* Test setCategory(...) */
    
    @Test
    public void shouldAcceptValidCourse() {
        Task task = new Task();
        task.setCourse(this.courses.selfStudyCourse);
        assertEquals(this.courses.selfStudyCourse, task.getCourse());
    }
    
    @Test(expected = NullPointerException.class)
    public void shouldNotAcceptNullCourse() {
        Task task = new Task();
        task.setCourse(null);
    }
    
    /* Test setCreationTime(...) */
    
    @Test
    public void shouldAcceptValidCreationTime() {
        Task task = new Task();
        Date now = new Date();
        task.setCreationTime(now);
        assertEquals(now, task.getCreationTime());
    }
    
    @Test(expected = NullPointerException.class)
    public void shouldNotAcceptNullCreationTime() {
        Task task = new Task();
        task.setCreationTime(null);
    }
    
    /* Test setCreator(...) */
    
    @Test(expected = IllegalArgumentException.class)
    public void setCreatorShouldNotAcceptStudentCreator() {
        Task task = new Task();
        task.setCreator(this.users.pullman);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void setCreatorShouldNotAcceptInstructorCreator() {
        Task task = new Task();
        task.setCreator(this.users.assistant);
    }
    
    @Test
    public void setCreatorShouldAcceptEditorCreator() {
        Task task = new Task();
        task.setCreator(this.users.editor);
        assertEquals(this.users.editor, task.getCreator());
    }
    
    @Test
    public void setCreatorShouldAcceptAdminCreator() {
        Task task = new Task();
        task.setCreator(this.users.admin);
        assertEquals(this.users.admin, task.getCreator());
    }
    
    @Test
    public void setCreatorShouldAcceptNullCreator() {
        Task task = new Task();
        task.setCreator(null);
    }
    
    /* Test setDescription(...) */
    
    @Test
    public void shouldAcceptValidDescription() {
        Task task = new Task();
        task.setDescription(Locale.ENGLISH, "Write a program that prints out the number 12");
        assertEquals("Write a program that prints out the number 12", task.getDescription(Locale.ENGLISH));
    }
    
    @Test
    public void shouldAcceptNullDescription() {
        Task task = new Task();
        task.setDescription(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptEmptyDescription() {
        Task task = new Task();
        task.setDescription(Locale.ENGLISH, "");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptOnlyWhitespaceDescription() {
        Task task = new Task();
        task.setDescription(Locale.ENGLISH, "\n\n     \t      \t   \n   ");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptTooLongDescription() {
        Task task = new Task();
        String badDescription = StringUtils.repeat("D", Task.MAX_DESCRIPTION_LENGTH + 1);
        task.setDescription(Locale.ENGLISH, badDescription);
    }
    
    /* Test setDifficulty(...) */
    
    @Test
    public void shouldAcceptMinMaxDefaultDifficulty() {
        Task task = new Task();
        task.setDifficulty(Task.DIFFICULTY_MIN);
        assertEquals(Task.DIFFICULTY_MIN, task.getDifficulty());
        task.setDifficulty(Task.DIFFICULTY_DEFAULT);
        assertEquals(Task.DIFFICULTY_DEFAULT, task.getDifficulty());
        task.setDifficulty(Task.DIFFICULTY_MAX);
        assertEquals(Task.DIFFICULTY_MAX, task.getDifficulty());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptTooLowDifficulty() {
        Task task = new Task();
        task.setDifficulty(Task.DIFFICULTY_MIN - 1);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptTooHighDifficulty() {
        Task task = new Task();
        task.setDifficulty(Task.DIFFICULTY_MAX + 1);
    }
    
    /* Test setHidden(...) */
    
    @Test
    public void setHiddenShouldBehaveAsExpected() {
        Task task = new Task();
        task.setHidden(true);
        assertTrue(task.getHidden());
        task.setHidden(false);
        assertFalse(task.getHidden());
    }
    
    /* Test setPostcode(...) */
    
    @Test
    public void shouldAcceptValidPostCode() {
        Task task = new Task();
        task.setPostCode("; Some comments as postcode");
        assertEquals("; Some comments as postcode", task.getPostCode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptWhitespaceOnlyPostCode() {
        Task task = new Task();
        task.setPostCode("  \n   \t    \n  ");
    }

    @Test
    public void shouldAcceptNullPostcode() {
        Task task = new Task();
        task.setPostCode(null);
        assertNull(task.getPostCode());
    }
    
    /* Test setPrecode(...) */
    
    @Test
    public void shouldAcceptValidPreCode() {
        Task task = new Task();
        task.setPreCode("; Some comments as precode");
        assertEquals("; Some comments as precode", task.getPreCode());        
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptWhitespaceOnlyPreCode() {
        Task task = new Task();
        task.setPreCode("  \n   \t    \n  ");        
    }

    @Test
    public void shouldAcceptNullPrecode() {
        Task task = new Task();
        task.setPreCode(null);
        assertNull(task.getPreCode());
    }
    
    /* Test setTitle(...) */

    @Test
    public void shouldAcceptValidTitle() {
        Task task = new Task();
        task.setTitle(Locale.ENGLISH, "My first Task");
        assertEquals("My first Task", task.getTitle(Locale.ENGLISH));
    }

    @Test
    public void shouldAcceptNullTitle() {
        Task task = new Task();
        task.setTitle(null);
    }
    
    @Test
    public void shouldStripWhitespaceFromTitle() {
        Task task = new Task();
        task.setTitle(Locale.ENGLISH, "     My first task   \n  \t  ");
        assertEquals("My first task", task.getTitle(Locale.ENGLISH));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptWhitespaceOnlyTitle() {
        Task task = new Task();
        task.setTitle(Locale.ENGLISH, "  \n\t     \n\n   ");
    }
    
    /* Test setType(...) */
    
    @Test
    public void shouldAcceptValidType() {
        Task task = new Task();
        task.setType(Task.Type.PROGRAMMING);
        assertEquals(Task.Type.PROGRAMMING, task.getType());
        task.setType(Task.Type.FILL_IN);
        assertEquals(Task.Type.FILL_IN, task.getType());
    }
    
    @Test(expected = NullPointerException.class)
    public void shouldNotAcceptNullType() {
        Task task = new Task();
        task.setType(null);
    }
    
    /* Test deepCopy() */

    @Test(expected = IllegalArgumentException.class)
    public void deepCopyShouldThrowExceptionOnCourseAndCategoryMismatch() {
        Task sourceTask = SampleTasks.createStandardTask(this.categories.beginnersTasks,
                                                         this.tasks.standardTask.getCreator(),
                                                         this.courses.emptyCourse,
                                                         Task.Type.PROGRAMMING);
        sourceTask.deepCopy();
    }

    @Test
    public void deepCopyShouldCopyAllSimpleFieldsCorrectly() {
        Task sourceTask = SampleTasks.createMinimalTask(this.tasks.minimalTask.getCategory(),
                                                        this.tasks.minimalTask.getCreator(),
                                                        this.tasks.minimalTask.getCourse(),
                                                        Task.Type.PROGRAMMING);
        
        sourceTask.setCreationTime(new GregorianCalendar(2008, 11, 19, 20, 41, 18).getTime());
        sourceTask.setDifficulty(1234);
        sourceTask.setHidden(true);
        sourceTask.setPreCode("pre");
        sourceTask.setPostCode("post");
        sourceTask.setModelSolution("model");
        Task targetTask = sourceTask.deepCopy();
        assertEquals(sourceTask.getCreationTime(), targetTask.getCreationTime());
        assertEquals(sourceTask.getDifficulty(), targetTask.getDifficulty());
        assertEquals(sourceTask.getHidden(), targetTask.getHidden());
        assertEquals(sourceTask.getType(), targetTask.getType());
        assertEquals(sourceTask.getPreCode(), targetTask.getPreCode());
        assertEquals(sourceTask.getPostCode(), targetTask.getPostCode());
        assertEquals(sourceTask.getModelSolution(), targetTask.getModelSolution());
    }
    
    @Test
    public void deepCopyShouldPreserveCourseAndCategory() {
        Task source = SampleTasks.createStandardTask(this.categories.intermediateTasks, this.users.editor, this.courses.selfStudyCourse, Task.Type.PROGRAMMING);
        Task copy = source.deepCopy();
        assertSame(source.getCourse(), copy.getCourse());
        assertSame(source.getCategory(), copy.getCategory());
    }
    
    @Test
    public void deepCopyShouldNotDuplicateAnyAnswers() {
        Task source = SampleTasks.createStandardTask(this.categories.intermediateTasks, this.users.editor, this.courses.selfStudyCourse, Task.Type.PROGRAMMING);
        Answer answer = new Answer(this.users.pullman, source, "; Just a comment!", true);
        source.getAnswers().add(answer);
        this.session.save(source);
        Task copy = source.deepCopy();
        assertTrue(source.getAnswers().contains(answer));
        assertSame(source, answer.getTask());
        assertEquals(0, copy.getAnswers().size());
        assertFalse(copy.getAnswers().contains(answer));
    }
    
    @Test
    public void deepCopyShouldDuplicateAllDependentCriteria() {
        Task source = SampleTasks.createStandardTask(this.categories.intermediateTasks, this.users.editor, this.courses.selfStudyCourse, Task.Type.PROGRAMMING);
        this.session.save(source);
        Criterion criterionA = new SimpleCriterion();
        criterionA.setTask(source);
        Criterion criterionB = new SimpleCriterion();
        criterionB.setTask(source);
        source.getCriteria().add(criterionA);
        source.getCriteria().add(criterionB);
        this.session.save(criterionA);
        this.session.save(criterionB);
        assertEquals(2, source.getCriteria().size());
        Task copy = source.deepCopy();
        assertEquals(2, copy.getCriteria().size());
    }
    
    @Test
    public void deepCopyShouldDuplicateAllDependentInputs() {
        Task source = SampleTasks.createStandardTask(this.categories.intermediateTasks, this.users.admin, this.courses.selfStudyCourse, Task.Type.FILL_IN);
        this.session.save(source);
        Input inputA = new Input(source, "1,2,3,4", false);
        Input inputB = new Input(source, "1,2,3,4", false);
        source.getInputs().add(inputA);
        source.getInputs().add(inputB);
        this.session.save(inputA);
        this.session.save(inputB);
        assertEquals(2, source.getInputs().size());
        this.session.saveOrUpdate(source);
        Task copy = source.deepCopy();
        assertEquals(2, copy.getInputs().size());
    }
    
    @Test
    @Ignore
    public void deepCopyShouldPreserveCriterionInputMappings() {
        Task source = SampleTasks.createStandardTask(this.categories.trickyTasks, this.users.editor, this.courses.selfStudyCourse, Task.Type.PROGRAMMING);
        
        fail();
    }
    
    /* Test deepCopy(Course, Category) */

    @Test(expected = IllegalArgumentException.class)
    public void deepCopyShouldThrowExceptionOnCourseAndCategoryClashB() {
        Task sourceTask = SampleTasks.createStandardTask(this.categories.beginnersTasks, this.users.editor, this.courses.autumnCourse, Task.Type.PROGRAMMING);
        sourceTask.deepCopy(this.courses.emptyCourse, this.categories.intermediateTasks);
    }
    
    @Test
    public void deepCopyShouldDuplicateCategoryIfRequired() {
        Task source = SampleTasks.createStandardTask(this.categories.intermediateTasks, this.users.admin, this.courses.selfStudyCourse, Task.Type.PROGRAMMING);
        Task copy = source.deepCopy(this.courses.emptyCourse, null);
        assertNotSame(source.getCategory(), copy.getCategory());
        assertTrue(this.courses.emptyCourse.getCategories().contains(copy.getCategory()));
    }
    
    @Test
    public void deepCopyShouldAttachCopyToCorrectCategory() {
        Task source = SampleTasks.createMinimalTask(this.categories.intermediateTasks, this.users.editor, this.courses.selfStudyCourse, Task.Type.PROGRAMMING);
        Task copy = source.deepCopy(null, this.categories.intermediateTasks);
        assertSame(this.categories.intermediateTasks, copy.getCategory());
    }
    
    @Test
    public void deepCopyShouldNotUpdateCollectionInCategory() {
        Task source = SampleTasks.createStandardTask(this.categories.intermediateTasks, this.users.editor, this.courses.selfStudyCourse, Task.Type.PROGRAMMING);
        this.session.save(source);
        Task copy = source.deepCopy(null, this.categories.intermediateTasks);
        assertSame(this.categories.intermediateTasks, copy.getCategory());
        assertFalse(source.getCategory().getTasks().contains(copy));
    }
    
    @Test
    public void deepCopyShouldAttachCopyToTheCorrectCourse() {
        Task source = SampleTasks.createMinimalTask(this.categories.intermediateTasks, this.users.editor, this.courses.selfStudyCourse, Task.Type.PROGRAMMING);
        Task targetTask = source.deepCopy(this.courses.selfStudyCourse, null, null, false);
        assertEquals(this.courses.selfStudyCourse, targetTask.getCourse());
    }
    
    @Test
    public void deepCopyShouldDuplicateAllDependentTStrings() {
        Task source = SampleTasks.createMinimalTask(this.categories.intermediateTasks, this.users.editor, this.courses.selfStudyCourse, Task.Type.PROGRAMMING);
        Task copy = source.deepCopy();
        assertNotSame(source.getTitle(), copy.getTitle());
        assertNotSame(source.getDescription(), copy.getDescription());
        assertEquals(source.getTitle().getTranslations(), copy.getTitle().getTranslations());
        assertEquals(source.getDescription().getTranslations(), copy.getDescription().getTranslations());
    }

    
    /* Test deepCopy(Course, Category, User, boolean) */

    @Test(expected = IllegalArgumentException.class)
    public void deepCopyShouldNotAcceptStudentAsCopyEditor() {
        Task task = new Task();
        task.deepCopy(this.courses.autumnCourse, null, this.users.pullman, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deepCopyShouldNotAcceptInstructorAsCopyEditor() {
        Task task = new Task();
        task.deepCopy(this.courses.autumnCourse, null, this.users.assistant, false);
    }
    
    @Test
    public void deepCopyShouldSetTheCopyEditorAsCreator() {
        Task sourceTask = SampleTasks.createMinimalTask(this.categories.intermediateTasks, this.users.editor, this.courses.selfStudyCourse, Task.Type.PROGRAMMING);
        Task targetTask = sourceTask.deepCopy(null, null, this.users.admin, false);
        assertSame(this.users.admin, targetTask.getCreator());
    }
    
    @Test
    public void deepCopyShouldKeepCreatorIfNoCopyEditorWasSpecified() {
        Task sourceTask = SampleTasks.createMinimalTask(this.categories.intermediateTasks, this.users.editor, this.courses.selfStudyCourse, Task.Type.PROGRAMMING);
        Task targetTask = sourceTask.deepCopy(null, null, null, false);
        assertSame(sourceTask.getCreator(), targetTask.getCreator());
    }
    
    @Test
    public void deepCopyShouldCopyCreationTimeCorrectly() {
        Task sourceTask = SampleTasks.createMinimalTask(this.categories.intermediateTasks, this.users.editor, this.courses.selfStudyCourse, Task.Type.PROGRAMMING);
        sourceTask.setCreationTime(new GregorianCalendar(2008, 11, 19, 20, 41, 18).getTime());
        Task targetTask = sourceTask.deepCopy(this.courses.selfStudyCourse, null, null, false);
        assertNotSame(sourceTask.getCreationTime(), targetTask.getCreationTime());
        assertEquals(sourceTask.getCreationTime(), targetTask.getCreationTime());
        targetTask.setCreationTime(new GregorianCalendar(2008, 11, 19, 20, 41, 19).getTime());
        assertFalse(sourceTask.getCreationTime().equals(targetTask.getCreationTime()));
        assertFalse(targetTask.getCreationTime().equals(sourceTask.getCreationTime()));
    }
    
    @Test
    public void deepCopyShouldDuplicateAllDependentStrings() {
        Task sourceTask = SampleTasks.createMinimalTask(this.categories.beginnersTasks, this.users.editor, this.courses.autumnCourse, Task.Type.PROGRAMMING);
        sourceTask.setPreCode("Some Precode");
        sourceTask.setPostCode("Some Postcode");
        Task targetTask = sourceTask.deepCopy(this.courses.selfStudyCourse, null, null, false);
        assertSame(sourceTask.getPostCode(), targetTask.getPostCode());
        assertSame(sourceTask.getPreCode(), targetTask.getPreCode());
        sourceTask.setPreCode("Precode");
        sourceTask.setPostCode("Postcode");
        assertFalse(sourceTask.getPreCode().equals(targetTask.getPreCode()));
        assertFalse(sourceTask.getPostCode().equals(targetTask.getPostCode()));
    }

    @Test
    public void deepCopyShouldNotDuplicateCategoryIfGivenAsParameter() {
        Task sourceTask = SampleTasks.createMinimalTask(this.categories.beginnersTasks, this.users.editor, this.courses.autumnCourse, Task.Type.PROGRAMMING);
        Category targetCategory = new Category();
        targetCategory.setName(ENGLISH, "Target Category");
        targetCategory.setCourse(this.courses.selfStudyCourse);
        this.courses.selfStudyCourse.getCategories().add(targetCategory);
        Task targetTask = sourceTask.deepCopy(this.courses.selfStudyCourse, targetCategory, null, false);
        assertSame(targetCategory, targetTask.getCategory());
    }
    
    @Test
    public void deepCopyShouldUpdateCollectionsInCourseAndCategoryIfRequested() {
        
    }
    
    /* Test hasCompleteTranslation(Locale) */
    
    @Test
    public void hasCompleteTranslationShouldReturnFalseAsLongAsThereAreNotAllTranslationsAvailable() {
        Criterion criterion = new SimpleCriterion();
        Task task = new Task();
        assertFalse(task.hasCompleteTranslation(Locale.ENGLISH));
        task.setTitle(Locale.GERMAN, "Beispielaufgabe");
        assertFalse(task.hasCompleteTranslation(Locale.GERMAN));
        assertFalse(task.hasCompleteTranslation(Locale.ENGLISH));
        task.setDescription(Locale.GERMAN, "Eine Beschreibung");
        assertFalse(task.hasCompleteTranslation(Locale.ENGLISH));
        assertTrue(task.hasCompleteTranslation(Locale.GERMAN));
        
        // Criteria may be left untranslated.
        task.getCriteria().add(criterion);
        assertFalse(task.hasCompleteTranslation(Locale.ENGLISH));
        assertTrue(task.hasCompleteTranslation(Locale.GERMAN));
    }

    @Test
    public void hasCompleteTranslationShouldReturnTrueIfTranslationIsComplete() {
        Task task = new Task();
        Category category = new Category();
        Course course = new Course(ENGLISH, "Some Course");
        category.setName(Locale.ENGLISH, "Some Category!");
        task.setDescription(Locale.ENGLISH, "A Short Description!");
        task.setTitle(Locale.ENGLISH, "Some Title");
        task.setCategory(category);
        task.setType(Task.Type.PROGRAMMING);
        task.setCourse(course);
        assertFalse(task.hasCompleteTranslation(Locale.GERMAN));
        assertTrue(task.hasCompleteTranslation(Locale.ENGLISH));
    }
    
    @Test
    public void hasCompleteTranslationShouldReturnFalseIfTitleIsSetButDescriptionIsNotInTheRightLocale() {
        Task task = new Task();
        task.setTitle(Locale.ENGLISH, "Some Title");
        task.setDescription(Locale.GERMAN, "Eine Beschreibung");
        assertFalse(task.hasCompleteTranslation(Locale.ENGLISH));
    }
    
    /* Test isFillIn() */
    
    @Test
    public void isFillInShouldBehaveCorrectly() {
        Task task = new Task();
        task.setType(Task.Type.FILL_IN);
        assertTrue(task.isFillIn());
        task.setType(Task.Type.PROGRAMMING);
        assertFalse(task.isFillIn());
    }
    
    /* Test isProgramming() */
    
    @Test
    public void isProgrammingShouldBehaveCorrectly() {
        Task task = new Task();
        task.setType(Task.Type.PROGRAMMING);
        assertTrue(task.isProgramming());
        task.setType(Task.Type.FILL_IN);
        assertFalse(task.isProgramming());
    }
    
    /* Test custom validation logic */
    
    @Test(expected = InvalidStateException.class)
    public void shouldNotBeSaveableIfCategoryNotInCourse() {
        Task task = SampleTasks.createMinimalTask(this.categories.beginnersTasks, this.users.editor, this.courses.emptyCourse, Task.Type.PROGRAMMING);
        session.save(task);
    }
    
}
