package fi.helsinki.cs.titotrainer.app.model;

import static org.junit.Assert.*;

import java.util.Locale;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.junit.Before;
import org.junit.Test;

import fi.helsinki.cs.titotrainer.testsupport.TitoTestCase;
import fi.helsinki.cs.titotrainer.testsupport.app.model.NonCooperativeCriterion;
import fi.helsinki.cs.titotrainer.testsupport.app.model.SimpleCriterion;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleAnswers;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleCriteria;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleTasks;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleUsers;
import fi.helsinki.cs.titotrainer.testsupport.framework.model.TestDb;

public class CriterionTest extends TitoTestCase {
    
    private Session session;
    
    private SampleTasks tasks;
    private SampleUsers users;

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
        this.users = this.tasks.users;
    }
    
    /* General Tests */
    
    @Test
    public void shouldSaveValidCriterion() {
        Criterion criterion;
        criterion = new SimpleCriterion();
        criterion.setTask(this.tasks.minimalTask);
        criterion.setAcceptMessage(Locale.ENGLISH, "That was great!");
        criterion.setRejectMessage(Locale.ENGLISH, "That was bad!");
        criterion.setParameters("Some=Data");
        criterion.getTask().getCriteria().add(criterion);
        this.session.save(criterion);
        this.session.flush();
    }
    
    /* Test setAcceptMessage(Locale, String) */
    
    @Test
    public void shouldAcceptValidAcceptMessage() {
        Criterion criterion = new SimpleCriterion();
        criterion.setAcceptMessage(Locale.ENGLISH, "The criterion is satisifed");
        assertEquals("The criterion is satisifed", criterion.getAcceptMessage(Locale.ENGLISH));
    }

    /* Test setRejectMessage */
    
    @Test
    public void shouldAcceptValidRejectMessage() {
        Criterion criterion = new SimpleCriterion();
        criterion.setRejectMessage(Locale.ENGLISH, "The criterion was not satisfied!");
        assertEquals("The criterion was not satisfied!", criterion.getRejectMessage(Locale.ENGLISH));
    }
    
    /* Test setTask(...) */
    
    @Test
    public void shouldAcceptValidTask() {
        Criterion criterion = new SimpleCriterion();
        criterion.setTask(this.tasks.minimalTask);
        assertEquals(this.tasks.minimalTask, criterion.getTask());
    }
    
    @Test(expected = NullPointerException.class)
    public void shouldNotAcceptNullTask() {
        Criterion criterion = new SimpleCriterion();
        criterion.setTask(null);
    }
    
    /* Test deepCopy() */
    
    @Test(expected = NullPointerException.class)
    public void deepCopyShouldThrowNullPointerExceptionOnNullTask() {
        Criterion source = new SimpleCriterion(null);
        source.deepCopy();
    }
    
    @Test(expected = IllegalStateException.class)
    public void deepCopyShouldThrowIllegalStateException() {
        Criterion source = new NonCooperativeCriterion(this.tasks.standardTask);
        source.deepCopy();
    }
    
    @Test
    public void deepCopyShouldNotDuplicateTaskOrData() {
        Criterion source = new SimpleCriterion(this.tasks.standardTask);
        source.setParameters("Some.Data");
        Criterion copy = source.deepCopy();
        assertSame(source.getTask(), copy.getTask());
        assertSame(source.getParameters(), copy.getParameters());
    }

    @Test
    public void deepCopyShouldNotCopyOrDuplicateValidations() {
        Criterion source = SampleCriteria.createPointlessCriterion(this.tasks.standardTask);
        Answer answer = SampleAnswers.createAnswerComment(this.tasks.standardTask, this.users.nykanen);
        Validation validation = new Validation();
        validation.setCriterion(source);
        validation.setAnswer(answer);
        source.getValidations().add(validation);
        Criterion copy = source.deepCopy();
        assertEquals(1, source.getValidations().size());
        assertSame(validation, source.getValidations().iterator().next());
        assertEquals(0, copy.getValidations().size());
    }

    @Test
    public void deepCopyShouldNotUpdateCollectionInTask() {
        Criterion source = new SimpleCriterion(this.tasks.standardTask);
        this.tasks.standardTask.getCriteria().add(source);
        source.setParameters("Some.Data");
        source.setAcceptMessage(ENGLISH, "Accepted!");
        source.setRejectMessage(ENGLISH, "Rejected!");
        Criterion copy = source.deepCopy();
        assertFalse(source.getTask().getCriteria().contains(copy));
    }

    @Test
    public void deepCopyShouldPreserveInputIfCopiedWithinTask() {
        Criterion source = new SimpleCriterion(this.tasks.standardTask);
        Input input = new Input(this.tasks.standardTask, "1,2,3,4", false);
        source.setParameters("Some.Data");
        source.setInput(input);
        Criterion copy = source.deepCopy();
        assertSame(source.getInput(), copy.getInput());
    }
    
    @Test
    public void deepCopyShouldPreserveQualityCriterionStatus() {
        Criterion source = new SimpleCriterion(this.tasks.standardTask);
        source.setParameters("Some.Data");
        source.setQualityCriterion(true);
        assertTrue(source.deepCopy().isQualityCriterion());
    }

    /* Test deepCopy(Task, Input, boolean) */
    
    @Test(expected = IllegalArgumentException.class)
    public void deepCopyShouldThrowExceptionIfTaskAndInputClash() {
        Criterion source = new SimpleCriterion(this.tasks.standardTask);
        source.setAcceptMessage(ENGLISH, "Accepted!");
        source.setRejectMessage(ENGLISH, "Rejected");
        this.tasks.standardTask.getCriteria().add(source);
        Input input = new Input(this.tasks.standardTask, "1,2,3", false);
        source.setInput(input);
        this.tasks.standardTask.getInputs().add(input);
        this.tasks.standardTask.getCriteria().add(source);
        this.session.save(source);
        source.deepCopy(this.tasks.minimalTask, input, false);
    }
    
    @Test
    public void deepCopyShouldReturnTrueCopy() {
        Criterion source = new SimpleCriterion(this.tasks.standardTask);
        this.tasks.standardTask.getCriteria().add(source);
        source.setParameters("Some.Data");
        source.setAcceptMessage(ENGLISH, "Accepted!");
        source.setRejectMessage(ENGLISH, "Rejected!");
        this.session.save(source);
        Criterion copy = source.deepCopy();
        assertNotSame(source, copy);
        assertFalse(source.equals(copy));
        assertFalse(copy.equals(source));
        assertNotSame(source.getAcceptMessage(), copy.getAcceptMessage());
        assertNotSame(source.getAcceptMessage().getTranslations(), copy.getAcceptMessage().getTranslations());
        assertEquals(source.getAcceptMessage().getTranslations(), copy.getAcceptMessage().getTranslations());
        assertNotSame(source.getRejectMessage(), copy.getRejectMessage());
        assertNotSame(source.getRejectMessage().getTranslations(), copy.getRejectMessage().getTranslations());
        assertEquals(source.getRejectMessage().getTranslations(), copy.getRejectMessage().getTranslations());
    }
    
    @Test
    public void deepCopyShouldAttachCopyToTargetTask() {
        Criterion source = new SimpleCriterion(this.tasks.standardTask);
        source.setParameters("Some.Data");
        Criterion copy = source.deepCopy(this.tasks.minimalTask, null, false);
        assertSame(this.tasks.minimalTask, copy.getTask());
    }

    @Test
    public void deepCopyShouldDuplicateInputIfCopiedToAnotherTask() {
        Criterion source = new SimpleCriterion(this.tasks.standardTask);
        Input input = new Input(this.tasks.standardTask, "1,2,3,4", false);
        source.setInput(input);
        source.setParameters("Some.Data");
        Criterion copy = source.deepCopy(this.tasks.minimalTask, null, false);
        assertNotSame(source.getInput(), copy.getInput());
        assertSame(this.tasks.minimalTask, copy.getInput().getTask());
    }
    
    @Test
    public void deepCopyShouldUpdateCollectionInTask() {
        Criterion source = new SimpleCriterion(this.tasks.standardTask);
        Input input = new Input(this.tasks.standardTask, "1,2,3,4", false);
        this.tasks.standardTask.getInputs().add(input);
        source.setInput(input);
        this.tasks.standardTask.getCriteria().add(source);
        source.setParameters("Some.Data");
        source.setAcceptMessage(ENGLISH, "Accepted!");
        source.setRejectMessage(ENGLISH, "Rejected!");
        this.session.save(source);
        this.session.save(input);
        Criterion copy = source.deepCopy(this.tasks.minimalTask, null, true);
        this.session.save(copy);
        assertNotSame(source.getInput(), copy.getInput());
        assertTrue(this.tasks.standardTask.getCriteria().contains(source));
        assertTrue(this.tasks.minimalTask.getCriteria().contains(copy));
        assertTrue(this.tasks.minimalTask.getInputs().contains(copy.getInput()));
    }
    
    /* Test hasCompleteTranslation(Locale) */
    
    @Test(expected = NullPointerException.class)
    public void hasCompleteTranslationShouldNotAcceptNullLocale() {
        Criterion criterion = new SimpleCriterion();
        criterion.hasCompleteTranslation(null);
    }

    @Test
    public void hasCompleteTranslationShouldReturnFalseIfAllTranslationsAreMissing() {
        Criterion criterion = new SimpleCriterion();
        assertFalse(criterion.hasCompleteTranslation(Locale.ENGLISH));
    }

    @Test
    public void hasCompleteTranslationShouldReturnTrueIfAllTranslationsAreSet() {
        Criterion criterion = new SimpleCriterion();
        criterion.setAcceptMessage(Locale.ENGLISH, "Accepted!");
        criterion.setRejectMessage(Locale.ENGLISH, "Rejected!");
        assertTrue(criterion.hasCompleteTranslation(Locale.ENGLISH));
    }

    @Test
    public void hasCompleteTranslationShouldReturnTrueOnlyForTheGivenLocale() {
        Criterion criterion = new SimpleCriterion();
        criterion.setAcceptMessage(Locale.GERMAN, "Akzeptiert!");
        criterion.setRejectMessage(Locale.GERMAN, "Abgewiesen!");
        assertTrue(criterion.hasCompleteTranslation(Locale.GERMAN));
        assertFalse(criterion.hasCompleteTranslation(Locale.ENGLISH));        
    }

    @Test
    public void hasCompleteTranslationShouldReturnFalseAfterTranslationsHaveBeenRemoved() {
        Criterion criterion = new SimpleCriterion();
        criterion.setAcceptMessage(Locale.ENGLISH, "Accepted!");
        criterion.setRejectMessage(Locale.ENGLISH, "Rejected!");
        assertTrue(criterion.hasCompleteTranslation(Locale.ENGLISH));
        criterion.setRejectMessage(Locale.ENGLISH, null);
        assertFalse(criterion.hasCompleteTranslation(Locale.ENGLISH));
    }

}